package com.ordersmanagement.crm.services;

import com.ordersmanagement.crm.models.dto.PaymentForm;
import com.ordersmanagement.crm.models.entities.CustomerEntity;
import com.ordersmanagement.crm.models.entities.OrderEntity;
import com.ordersmanagement.crm.models.pojos.Payment;
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
            paymentServiceFacade.makePayment(payment);
        });
    }

    @Transactional
    public void distributeOrderOverpayment(OrderEntity updatedOrder, int overpayment) {
        if (overpayment <= 0) return;
        int paid = 0;
        while (paid < overpayment) {
            int remainingPayment = overpayment - paid;
            Payment lastPayment = PaymentUtils.getLastPayment(updatedOrder.getPayLog());

            PaymentForm newPayment = new PaymentForm();
            newPayment.setCustomer(updatedOrder.getCustomer());
            newPayment.setReceiver(lastPayment.getReceiver());

            if (lastPayment.getSum() <= remainingPayment) {
                newPayment.setSum(lastPayment.getSum());
                newPayment.setPaymentDate(lastPayment.getDateTime());
                paymentServiceFacade.makePayment(newPayment);

                updatedOrder.removeLastPayment();
                paid += lastPayment.getSum();
            } else {
                newPayment.setSum(remainingPayment);
                newPayment.setPaymentDate(lastPayment.getDateTime());
                paymentServiceFacade.makePayment(newPayment);

                lastPayment.setSum(lastPayment.getSum() - remainingPayment);
                updatedOrder.replaceLastPayment(lastPayment.toString());
                paid += remainingPayment;
            }
        }
    }
}
