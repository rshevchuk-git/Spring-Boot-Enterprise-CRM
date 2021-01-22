package com.ordersmanagement.crm.services;

import com.ordersmanagement.crm.models.dto.PaymentForm;
import com.ordersmanagement.crm.models.entities.Customer;
import com.ordersmanagement.crm.models.entities.Order;
import com.ordersmanagement.crm.models.pojos.Payment;
import com.ordersmanagement.crm.services.facades.PaymentServiceFacade;
import com.ordersmanagement.crm.utils.PaymentUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

@Service
@AllArgsConstructor
public class OrderPaymentsManager {

    private final OrderService orderService;
    private final PaymentServiceFacade paymentServiceFacade;

    @Transactional
    public void removePaymentsFrom(Order updatedOrder) {
        orderService.getOrderById(updatedOrder.getOrderId()).ifPresent(savedOrder -> {
            savedOrder.setPaySum(savedOrder.getFinalSum()); // Make this order fully paid to skip it during re-payment
            distributePayments(savedOrder.getPayLog(), savedOrder.getCustomer());
            savedOrder.removeAllPayments();
            updatedOrder.removeAllPayments();
        });
    }

    @Transactional
    public void distributePayments(String paymentLog, Customer customer) {
        if (paymentLog == null || paymentLog.isEmpty()) return;
        Arrays.stream(paymentLog.split("\n")).forEach(log -> {
            PaymentForm payment = new PaymentForm();
            payment.setCustomer(customer);
            payment.setSum(PaymentUtils.getSumFromLog(log));
            payment.setPaymentDate(PaymentUtils.getLocalDateTimeFromLog(log));
            payment.setReceiver(PaymentUtils.getTypeNameFromLog(log));
            paymentServiceFacade.makePayment(payment);
        });
    }

    @Transactional
    public void distributeOverpayment(Order updatedOrder, int overpayment) {
        if (overpayment <= 0) return;
        int paid = 0;
        while (paid < overpayment) {
            int remainingPayment = overpayment - paid;
            Payment lastPayment = PaymentUtils.getLastPayment(updatedOrder.getPayLog());
            PaymentForm newPayment = new PaymentForm(updatedOrder.getCustomer(), lastPayment);

            if (lastPayment.getSum() <= remainingPayment) {
                paymentServiceFacade.makePayment(newPayment);
                updatedOrder.removeLastPayment();
            } else {
                newPayment.setSum(remainingPayment);
                paymentServiceFacade.makePayment(newPayment);
                lastPayment.setSum(lastPayment.getSum() - remainingPayment);
                updatedOrder.replaceLastPayment(lastPayment.toString());
            }
            paid += newPayment.getSum();
        }
    }
}
