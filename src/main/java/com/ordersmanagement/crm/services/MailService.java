package com.ordersmanagement.crm.services;

import com.ordersmanagement.crm.models.entities.Customer;
import lombok.AllArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class MailService {

    public final JavaMailSender emailSender;

    public void sendNotification(Customer newCustomer) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("psdruk.rv@gmail.com");
        message.setTo("psdruk@gmail.com");
        message.setSubject("Додано нового клієнта");
        String text = "Назва клієнта: " + newCustomer.getCustomerName() +"\n" +
                "Місто: " + newCustomer.getCity() + "\n" +
                "Додаткова інформація: " + newCustomer.getInfo() + "\n" +
                "Група: " + newCustomer.getCustomerGroup() + "\n" +
                "Контактна особа 1: " + newCustomer.getFirstPerson() + "  Телефон 1: " + newCustomer.getFirstPhone() + "  Email 1: " + newCustomer.getFirstEmail() + "\n" +
                "Контактна особа 1: " + newCustomer.getSecondPerson() +  "  Телефон 1: " + newCustomer.getSecondPhone() + "  Email 1: " + newCustomer.getSecondPerson() + "\n" +
                "Контактна особа 1: " + newCustomer.getThirdPerson() + "  Телефон 1: " + newCustomer.getThirdPhone() + "  Email 1: " + newCustomer.getThirdEmail();
        message.setText(text);
        emailSender.send(message);
    }
}
