package com.ordersmanagement.crm.services;

import com.ordersmanagement.crm.emails.NewCustomerNotification;
import com.ordersmanagement.crm.emails.StatusNotification;
import com.ordersmanagement.crm.models.entities.Customer;
import com.ordersmanagement.crm.models.entities.Order;
import lombok.AllArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@AllArgsConstructor
public class MailService {

    public final JavaMailSender emailSender;

    public void informAboutNewCustomer(Customer newCustomer) {
        NewCustomerNotification newCustomerNotification = new NewCustomerNotification(newCustomer, emailSender);
        send(newCustomerNotification);
    }

    public void informAboutStatusChange(Order order) {
        if (order.getStatus().getId() != 3) return;
        if (order.getCustomer().getFirstEmail() == null || order.getCustomer().getFirstEmail().trim().isEmpty()) return;
        StatusNotification statusNotification = new StatusNotification(order, emailSender);
        send(statusNotification);
    }

    private void send(Runnable notification) {
        ExecutorService service = Executors.newFixedThreadPool(1);
        service.execute(notification);
    }
}
