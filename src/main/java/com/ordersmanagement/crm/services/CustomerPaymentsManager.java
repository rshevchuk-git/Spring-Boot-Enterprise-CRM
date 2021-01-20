package com.ordersmanagement.crm.services;

import com.ordersmanagement.crm.exceptions.CustomerNotFoundException;
import com.ordersmanagement.crm.models.entities.CustomerEntity;
import com.ordersmanagement.crm.models.entities.OrderEntity;
import com.ordersmanagement.crm.models.pojos.Payment;
import com.ordersmanagement.crm.utils.PaymentUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class CustomerPaymentsManager {

    private final CustomerService customerService;

    @Transactional
    public void putOnCustomerBalance(int customerId, Payment payment) {
        if (payment.getSum() <= 0) return;
        customerService.getById(customerId).ifPresent(customer -> customer.putOnBalance(payment));
    }

    @Transactional
    public void payFromCustomerBalance(OrderEntity order) throws CustomerNotFoundException {
        CustomerEntity customer = customerService.getById(order.getCustomerId()).orElseThrow(CustomerNotFoundException::new);
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
}
