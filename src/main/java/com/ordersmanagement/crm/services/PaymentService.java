package com.ordersmanagement.crm.services;

import com.ordersmanagement.crm.dao.business.PaymentMethodRepository;
import com.ordersmanagement.crm.models.dto.PaymentForm;
import com.ordersmanagement.crm.models.entities.Order;
import com.ordersmanagement.crm.models.entities.PaymentMethod;
import com.ordersmanagement.crm.models.pojos.Payment;
import com.ordersmanagement.crm.utils.PaymentUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentMethodRepository paymentMethodRepository;

    public List<PaymentMethod> getAllPaymentMethods() {
        return paymentMethodRepository.findAll();
    }

    public PaymentMethod updatePaymentMethod(PaymentMethod paymentMethod) {
        return paymentMethodRepository.save(paymentMethod);
    }

    @Transactional
    public Payment payOrders(PaymentForm payment, List<Order> unpaidOrders) {
        int remainingMoney = PaymentUtils.calculateOperatingSum(payment.getSum(), payment.getPercentage());
        if (remainingMoney <= 0) return Payment.EMPTY;

        for (Order order : unpaidOrders) {
            int requiredPayment = order.getFinalSum() - order.getPaySum();
            if (remainingMoney >= requiredPayment) {
                order.addPayments(PaymentUtils.formatPayLog(payment.getPaymentDate(), requiredPayment, payment.getReceiver()));
                remainingMoney -= requiredPayment;
            } else if (remainingMoney != 0) {
                order.addPayments(PaymentUtils.formatPayLog(payment.getPaymentDate(), remainingMoney, payment.getReceiver()));
                remainingMoney = 0;
            }
        }
        return new Payment(remainingMoney, payment.getPaymentDate(), payment.getReceiver());
    }
}
