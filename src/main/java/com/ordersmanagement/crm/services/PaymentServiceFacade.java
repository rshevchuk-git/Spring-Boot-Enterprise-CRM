package com.ordersmanagement.crm.services;

import com.ordersmanagement.crm.models.dto.PaymentForm;
import com.ordersmanagement.crm.models.entities.OrderEntity;
import com.ordersmanagement.crm.models.pojos.Payment;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class PaymentServiceFacade {

    private final PaymentService paymentService;
    private final OrderService orderService;
    private final CustomerPaymentsManager customerPaymentsManager;

    public void makePayment(PaymentForm payment) {
        List<OrderEntity> unpaidOrders = orderService.getUnpaidOrdersOf(payment.getCustomer(), payment.getEntrepreneur());
        Payment remainingMoney = paymentService.makePayment(payment, unpaidOrders);
        customerPaymentsManager.putOnCustomerBalance(payment.getCustomerId(), remainingMoney);
    }
}
