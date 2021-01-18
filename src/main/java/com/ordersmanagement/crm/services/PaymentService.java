package com.ordersmanagement.crm.services;

import com.ordersmanagement.crm.dao.orders.CustomerRepository;
import com.ordersmanagement.crm.dao.orders.PaymentMethodRepository;
import com.ordersmanagement.crm.exceptions.CustomerNotFoundException;
import com.ordersmanagement.crm.models.entities.CustomerEntity;
import com.ordersmanagement.crm.models.entities.OrderEntity;
import com.ordersmanagement.crm.models.entities.PaymentMethodEntity;
import com.ordersmanagement.crm.models.forms.PaymentForm;
import com.ordersmanagement.crm.utils.PaymentUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final OrderService orderService;
    private final CustomerRepository customerRepository;
    private final PaymentMethodRepository paymentMethodRepository;

    public List<PaymentMethodEntity> getAllPaymentMethods() {
        return paymentMethodRepository.findAll();
    }

    public PaymentMethodEntity updatePaymentMethod(PaymentMethodEntity paymentMethod) {
        return paymentMethodRepository.save(paymentMethod);
    }

    @Transactional
    public void makePayment(PaymentForm payment) {
        int remainingMoney = PaymentUtils.calculateOperatingSum(payment.getSum(), payment.getPercentage());
        if (remainingMoney <= 0) return;

        List<OrderEntity> unpaidOrders = orderService.getUnpaidOrdersOf(payment.getCustomer(), payment.getEntrepreneur());
        for (OrderEntity order : unpaidOrders) {
            int requiredPayment = order.getFinalSum() - order.getPaySum();
            if (remainingMoney >= requiredPayment) {
                order.addPayments(PaymentUtils.formatPayLog(payment.getPaymentDate(), requiredPayment, payment.getReceiver()));
                remainingMoney -= requiredPayment;
            } else if (remainingMoney != 0) {
                order.addPayments(PaymentUtils.formatPayLog(payment.getPaymentDate(), remainingMoney, payment.getReceiver()));
                remainingMoney = 0;
            }
        }
        putRemainingMoneyOnBalance(payment.getCustomer().getCustomerId(), remainingMoney, payment.getPaymentDate(), payment.getReceiver());
    }

    @Transactional
    public void putRemainingMoneyOnBalance(int customerId, int money, LocalDateTime date, String receiver) {
        if (money <= 0) return;
        customerRepository.findById(customerId).ifPresent(customer -> customer.putOnBalance(money, date, receiver));
    }

    @Transactional
    public void payFromCustomerBalance(OrderEntity order) throws CustomerNotFoundException {
        CustomerEntity customer = customerRepository.findById(order.getCustomer().getCustomerId()).orElseThrow(CustomerNotFoundException::new);
        if (customer.getMoney() == 0) return;

        int remainingUnpaid = order.getFinalSum() - order.getPaySum();
        if (remainingUnpaid >= customer.getMoney()) {
            order.addPayments(customer.getPayLog());
            customer.removeAllPayments();
        } else {
            List<String> changedLogs = new ArrayList<>();
            for (String paymentLog : customer.getPayLog().split("\\n")) {
                int paymentSum = PaymentUtils.getSumFromLog(paymentLog);
                if (remainingUnpaid >= paymentSum) {
                    order.addPayments(paymentLog);
                    customer.setMoney(customer.getMoney() - paymentSum);
                    remainingUnpaid -= paymentSum;
                } else if (order.getPaySum() != order.getFinalSum()) {
                    order.addPayments(PaymentUtils.replaceSumInLog(paymentLog, remainingUnpaid));
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

            distributeOrderPayments(savedOrder.getPayLog(), savedOrder.getCustomer());
            savedOrder.removeAllPayments();
            updatedOrder.removeAllPayments();
        });
    }

    @Transactional
    public void distributeOrderPayments(String paymentLog, CustomerEntity customer) {
        if (paymentLog == null || paymentLog.isEmpty()) return;
        Arrays.stream(paymentLog.split("\n")).forEach(log -> {
            PaymentForm payment = new PaymentForm();
            payment.setCustomer(customer);
            payment.setSum(PaymentUtils.getSumFromLog(log));
            payment.setPaymentDate(PaymentUtils.getLocalDateTimeFromLog(log));
            payment.setReceiver(PaymentUtils.getTypeNameFromLog(log));
            makePayment(payment);
        });
    }

    @Transactional
    public void distributeOrderOverpayment(OrderEntity updatedOrder, int overpayment) {
        if (overpayment <= 0) return;
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
