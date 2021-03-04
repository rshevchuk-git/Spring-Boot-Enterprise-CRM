package com.ordersmanagement.crm.emails;

import com.ordersmanagement.crm.models.entities.Order;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.format.DateTimeFormatter;

public class StatusNotification implements Runnable {

    private Order order;
    private JavaMailSender mailSender;

    public StatusNotification(Order order, JavaMailSender mailSender) {
        this.order = order;
        this.mailSender = mailSender;
    }

    @Override
    public void run() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("psdruk.rv@gmail.com");
        message.setTo(order.getCustomer().getFirstEmail());
        message.setSubject("Ваше замовлення №" + order.getOrderId() + " готове");
        String text = "Вітаємо,\n\n" + "Ваше замовлення №" + order.getOrderId() + " від " + formatter.format(order.getDate()) + " готове\n";
        text += order.getOrderType().getTypeName() + " / " + order.getOrderKind().getKindName() + " / " + order.getComment() + "\n\n";
        text += "З детальною інформацією по ваших замовленнях ви можете ознайомитись в особистому кабінеті: https://app.psdruk.com.ua/";
        message.setText(text);
        mailSender.send(message);
    }
}
