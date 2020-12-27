package com.ordersmanagement.crm.services;

import com.ordersmanagement.crm.dao.orders.CustomerRepository;
import com.ordersmanagement.crm.dao.orders.PaymentMethodRepository;
import com.ordersmanagement.crm.exceptions.CustomerNotFoundException;
import com.ordersmanagement.crm.models.entities.CustomerEntity;
import com.ordersmanagement.crm.models.entities.EntrepreneurEntity;
import com.ordersmanagement.crm.models.entities.OrderEntity;
import com.ordersmanagement.crm.models.entities.PaymentMethodEntity;
import com.ordersmanagement.crm.models.forms.PaymentForm;
import com.ordersmanagement.crm.utils.PaymentUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentService {

    @PersistenceContext
    private EntityManager ordersEntityManager;
    private final OrderService orderService;
    private final CustomerRepository customerRepository;
    private final PaymentMethodRepository paymentMethodRepository;

    public List<PaymentMethodEntity> getAllPaymentMethods() {
        return paymentMethodRepository.findAll();
    }

    public Optional<PaymentMethodEntity> savePaymentMethod(PaymentMethodEntity paymentMethod) {
        if (paymentMethodRepository.existsByTypeName(paymentMethod.getTypeName())) {
            return Optional.empty();
        }
        paymentMethod.setPaymentTypeID(0);
        return Optional.of(paymentMethodRepository.save(paymentMethod));
    }

    public List<OrderEntity> getUnpaidOrdersOf(CustomerEntity customer, EntrepreneurEntity entrepreneur) {
        TypedQuery<OrderEntity> unpaidOrdersQuery = ordersEntityManager.createQuery("from OrderEntity where customer = :customerName and (:entrepreneurName is null or entrepreneur = :entrepreneurName) and paySum < finalSum order by id asc", OrderEntity.class);
        unpaidOrdersQuery.setParameter("entrepreneurName", entrepreneur);
        unpaidOrdersQuery.setParameter("customerName", customer);
        return unpaidOrdersQuery.getResultList();
    }

    @Transactional
    public void makePayment(PaymentForm payment) throws CustomerNotFoundException {
        List<OrderEntity> unpaidOrders = getUnpaidOrdersOf(payment.getCustomer(), payment.getEntrepreneur());
        int remainingMoney = PaymentUtils.calculateOperatingSum(payment.getSum(), payment.getPercentage());

        for (OrderEntity currentOrder : unpaidOrders) {
            int requiredPayment = currentOrder.getFinalSum() - currentOrder.getPaySum();
            LocalDateTime paymentDate = payment.getPaymentDate();
            if (remainingMoney >= requiredPayment) {
                currentOrder.addPayment(PaymentUtils.formatPayLog(paymentDate, requiredPayment, payment.getReceiver()));
                remainingMoney -= requiredPayment;
            } else if (remainingMoney != 0) {
                currentOrder.addPayment(PaymentUtils.formatPayLog(paymentDate, remainingMoney, payment.getReceiver()));
                remainingMoney = 0;
            }
        }
        if(remainingMoney > 0) {
            CustomerEntity customer = customerRepository.findById(payment.getCustomer().getCustomerId()).orElseThrow(CustomerNotFoundException::new);
            customer.putOnBalance(remainingMoney, payment.getPaymentDate(), payment.getReceiver());
        }
    }

    @Transactional
    public void payFromCustomerBalance(OrderEntity order) throws CustomerNotFoundException {
        CustomerEntity customer = customerRepository.findById(order.getCustomer().getCustomerId()).orElseThrow(CustomerNotFoundException::new);
        if (customer.getMoney() == 0) return;

        int remainingUnpaid = order.getFinalSum() - order.getPaySum();
        if (remainingUnpaid >= customer.getMoney()) {
            order.addPayment(customer.getPayLog());
            customer.removeAllPayments();
        } else {
            List<String> changedLogs = new ArrayList<>();
            for (String paymentLog : customer.getPayLog().split("\\n")) {
                int paymentSum  = PaymentUtils.getSumFromLog(paymentLog);
                if (remainingUnpaid >= paymentSum) {
                    order.addPayment(paymentLog);
                    customer.setMoney(customer.getMoney() - paymentSum);
                    remainingUnpaid -= paymentSum;
                } else if (order.getPaySum() != order.getFinalSum()) {
                    order.addPayment(PaymentUtils.replaceSumInLog(paymentLog, remainingUnpaid));
                    customer.setMoney(customer.getMoney() - remainingUnpaid);
                    changedLogs.add(PaymentUtils.replaceSumInLog(paymentLog, paymentSum - remainingUnpaid));
                    remainingUnpaid = 0;
                } else {
                    changedLogs.add(paymentLog);
                }
            }
            customer.setPayLog(String.join("\n", changedLogs) + "\n");
        }
    }

    @Transactional
    public void removePaymentsFrom(OrderEntity updatedOrder) {
        orderService.getOrderById(updatedOrder.getOrderId()).ifPresent(savedOrder -> {
            savedOrder.setPaySum(savedOrder.getFinalSum()); // Make this order fully paid to skip it during re-payment

            distributePayments(savedOrder.getPayLog(), savedOrder.getCustomer());
            savedOrder.removeAllPayments();
            updatedOrder.removeAllPayments();
        });
    }

    @Transactional
    public void distributePayments(String paymentLog, CustomerEntity customer) {
        if (paymentLog.isEmpty()) return;
        Arrays.stream(paymentLog.split("\n")).forEach(log -> {
            PaymentForm payment = new PaymentForm();
            payment.setCustomer(customer);
            payment.setSum(PaymentUtils.getSumFromLog(log));
            payment.setPaymentDate(PaymentUtils.getLocalDateTimeFromLog(log));
            payment.setReceiver(PaymentUtils.getTypeNameFromLog(log));
            try {
                makePayment(payment);
            } catch (CustomerNotFoundException e) {
                e.printStackTrace();
            }
        });
    }

    @Transactional
    public void distributeOverpayment(OrderEntity updatedOrder, int overpayment) throws CustomerNotFoundException {
        int paid = 0;
        while (paid < overpayment) {
            int   remainingPayment = overpayment - paid;
            String     lastPayment = PaymentUtils.getLastPayment(updatedOrder.getPayLog());
            LocalDateTime dateTime = PaymentUtils.getLocalDateTimeFromLog(lastPayment);
            String        receiver = PaymentUtils.getReceiverFromLog(lastPayment);
            int     lastPaymentSum = PaymentUtils.getSumFromLog(lastPayment);
            PaymentForm payment = new PaymentForm();
            payment.setCustomer(updatedOrder.getCustomer());
            payment.setReceiver(receiver);

            if (lastPaymentSum <= remainingPayment) {
                payment.setSum(lastPaymentSum);
                payment.setPaymentDate(dateTime);
                makePayment(payment);

                updatedOrder.setPaySum(updatedOrder.getPaySum() - lastPaymentSum);
                updatedOrder.setPayLog(updatedOrder.getPayLog().replaceAll(updatedOrder.getPayLog().substring(updatedOrder.getPayLog().indexOf(lastPayment)), ""));
                paid += lastPaymentSum;
            } else {
                payment.setSum(remainingPayment);
                payment.setPaymentDate(dateTime);
                makePayment(payment);

                updatedOrder.setPaySum(updatedOrder.getPaySum() - remainingPayment);
                String newLog = PaymentUtils.replaceSumInLog(lastPayment, lastPaymentSum - remainingPayment);
                updatedOrder.setPayLog(updatedOrder.getPayLog().replaceAll(updatedOrder.getPayLog().substring(updatedOrder.getPayLog().indexOf(lastPayment)), newLog));
                paid += remainingPayment;
            }
        }
    }
}
