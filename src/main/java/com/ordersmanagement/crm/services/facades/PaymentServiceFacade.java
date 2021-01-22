package com.ordersmanagement.crm.services.facades;

import com.ordersmanagement.crm.models.dto.PaymentForm;
import com.ordersmanagement.crm.models.entities.Order;
import com.ordersmanagement.crm.models.pojos.Payment;
import com.ordersmanagement.crm.services.CustomerPaymentsManager;
import com.ordersmanagement.crm.services.OrderService;
import com.ordersmanagement.crm.services.PaymentService;
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
        List<Order> unpaidOrders = orderService.getUnpaidOrdersOf(payment.getCustomer(), payment.getEntrepreneur());
        Payment remainingMoney = paymentService.payOrders(payment, unpaidOrders);
        customerPaymentsManager.putOnCustomerBalance(payment.getCustomerId(), remainingMoney);
    }
}
