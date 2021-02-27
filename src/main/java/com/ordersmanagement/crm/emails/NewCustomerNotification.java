package com.ordersmanagement.crm.emails;

import com.ordersmanagement.crm.models.entities.Customer;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

public class NewCustomerNotification implements Runnable {

    private Customer customer;
    private JavaMailSender mailSender;

    public NewCustomerNotification(Customer customer, JavaMailSender mailSender) {
        this.customer = customer;
        this.mailSender = mailSender;
    }

    @Override
    public void run() {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("psdruk.rv@gmail.com");
        message.setTo("psdruk@gmail.com");
        message.setSubject("Додано нового клієнта");
        String text = "Назва клієнта: " + customer.getCustomerName() +"\n" +
                "Місто: " + customer.getCity() + "\n" +
                "Додаткова інформація: " + customer.getInfo() + "\n" +
                "Група: " + customer.getCustomerGroup() + "\n" +
                "Контактна особа 1: " + customer.getFirstPerson() + "  Телефон 1: " + customer.getFirstPhone() + "  Email 1: " + customer.getFirstEmail() + "\n" +
                "Контактна особа 1: " + customer.getSecondPerson() +  "  Телефон 1: " + customer.getSecondPhone() + "  Email 1: " + customer.getSecondPerson() + "\n" +
                "Контактна особа 1: " + customer.getThirdPerson() + "  Телефон 1: " + customer.getThirdPhone() + "  Email 1: " + customer.getThirdEmail();
        message.setText(text);
        mailSender.send(message);
    }
}

