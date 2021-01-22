package com.ordersmanagement.crm.models.entities;

import com.ordersmanagement.crm.models.pojos.Payment;
import com.ordersmanagement.crm.utils.PaymentUtils;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Arrays;

@Entity
@Data
@NoArgsConstructor
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private int orderId;

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "status_id")
    private Status status;

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.REFRESH})
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "entrepreneur_id")
    private Entrepreneur entrepreneur;

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "order_kind_id")
    private OrderKind orderKind;

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "order_type_id")
    private OrderType orderType;

    @Column(name = "bill_no")
    private int billNo;

    @Column(name = "width")
    private int width;

    @Column(name = "height")
    private int height;

    @Column(name = "amount")
    private int amount;

    @Column(name = "fees")
    private int fees;

    @Column(name = "m2")
    private double m2;

    @Column(name = "price")
    private double price;

    @Column(name = "final_sum")
    private int finalSum;

    @Column(name = "date")
    @DateTimeFormat(pattern = "dd/MM/yyyy hh:mm")
    private LocalDateTime date;

    @Column(name = "pay_date")
    @DateTimeFormat(pattern = "dd/MM/yyyy hh:mm")
    private LocalDateTime payDate;

    @Column(name = "pay_sum")
    private int paySum;

    @Column(name = "comment")
    private String comment;

    @Column(name = "pay_log")
    private String payLog = "";

    public Integer getCustomerId() {
        return this.customer.getCustomerId();
    }

    public void addPayments(String paymentLog) {
        Arrays.stream(paymentLog.split("\\n")).forEach(payment -> {
            this.payDate = PaymentUtils.getLocalDateTimeFromLog(payment);
            this.paySum += PaymentUtils.getSumFromLog(payment);
            this.appendPayLog(payment);
        });
    }

    public void setPayLog(String newLog) {
        if (newLog == null || newLog.isEmpty()) return;
        this.payLog = newLog.trim() + "\n";
    }

    public void appendPayLog(String appendix) {
        if (appendix == null || appendix.isEmpty()) return;
        String formattedLog = appendix.endsWith("\n") ? appendix : (appendix + "\n");
        if (this.payLog == null || this.payLog.isEmpty()) {
            this.payLog = formattedLog;
        } else {
            this.payLog += formattedLog;;
        }
    }

    public void replaceLastPayment(String newLog) {
        this.removeLastPayment();
        this.addPayments(newLog);
    }

    public void removeLastPayment() {
        Payment lastPayment = PaymentUtils.getLastPayment(this.getPayLog());
        int lastNewLineIdx = this.getPayLog().trim().lastIndexOf("\n");
        String newLog = this.getPayLog().substring(0, lastNewLineIdx);
        this.setPaySum(this.getPaySum() - lastPayment.getSum());
        this.setPayLog(newLog);
    }

    public void removeAllPayments() {
        this.paySum = 0;
        this.payDate = null;
        this.payLog = "";
    }
}
