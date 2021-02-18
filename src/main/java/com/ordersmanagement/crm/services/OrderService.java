package com.ordersmanagement.crm.services;

import com.ordersmanagement.crm.dao.business.OrderRepository;
import com.ordersmanagement.crm.exceptions.OrderNotFoundException;
import com.ordersmanagement.crm.models.dto.SortForm;
import com.ordersmanagement.crm.models.dto.Summary;
import com.ordersmanagement.crm.models.entities.*;
import com.ordersmanagement.crm.models.pojos.Payment;
import com.ordersmanagement.crm.utils.OrderUtils;
import com.ordersmanagement.crm.utils.PaymentUtils;
import com.querydsl.core.BooleanBuilder;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@AllArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CustomerService customerService;
    private final TypeFilterService typeFilterService;

    public Optional<Order> getOrderById(Integer id) {
        return orderRepository.findById(id);
    }

    public void saveAll(List<Order> orders) {
        orderRepository.saveAll(orders);
    }

    public void deleteById(Integer orderId) {
        orderRepository.deleteById(orderId);
    }

    public List<Order> getUnpaidOrdersOf(Customer customer, Entrepreneur entrepreneur) {
        return orderRepository.getUnpaidOrdersOf(customer, entrepreneur);
    }

    public List<Order> getAllByOrderKind(OrderKind orderKind) {
        return orderRepository.findByOrderKind(orderKind);
    }

    public List<Order> getAllByOrderType(OrderType orderType) {
        return orderRepository.findByOrderType(orderType);
    }

    public List<Order> getCustomerOrders(Integer customerId) {
        return orderRepository.getOrdersMadeBy(customerId);
    }

    public List<Order> getRecentOrders() {
        LocalDateTime dayBefore = LocalDateTime.now(ZoneId.of("Europe/Kiev")).minusDays(5);
        return orderRepository.getOrdersStartingFrom(dayBefore);
    }

    public Order changeOrderStatus(Order order, Status status) {
        order.setStatus(status);
        return orderRepository.save(order);
    }

    public boolean isCustomerChanged(Order changedOrder) throws OrderNotFoundException {
        Order savedOrder = orderRepository.findById(changedOrder.getOrderId()).orElseThrow(OrderNotFoundException::new);
        return savedOrder.getCustomer().getCustomerId() != changedOrder.getCustomer().getCustomerId();
    }

    public Order saveNewOrder(Order newOrder) {
        newOrder.setOrderId(0);
        newOrder.setDate(LocalDateTime.now(ZoneId.of("Europe/Kiev")));
        newOrder.setM2(OrderUtils.calculateM2(newOrder));
        return orderRepository.save(newOrder);
    }

    public Order updateOrder(Order newOrder) {
        newOrder.setM2(OrderUtils.calculateM2(newOrder));
        newOrder.setPaySum(PaymentUtils.calculatePaymentSum(newOrder.getPayLog()));
        if (newOrder.getPaySum() > 0) {
            Payment lastPayment = PaymentUtils.getLastPayment(newOrder.getPayLog());
            LocalDateTime lastPaymentDate = lastPayment.getDateTime();
            newOrder.setPayDate(lastPaymentDate);
        }
        return orderRepository.save(newOrder);
    }

    public Summary summarize(List<Order> orders, SortForm selections) {
        double  m2 = OrderUtils.totalOrdersM2(orders);
        int   fees = OrderUtils.totalOrdersFees(orders);
        int amount = OrderUtils.totalOrdersAmount(orders);
        int   paid = OrderUtils.totalOrdersPaid(orders, selections.getReceiver()) + customerService.paidOnCustomerBalance(selections);
        return new Summary(orders, paid, fees, amount, m2);
    }

    public List<Order> getFilteredOrders(SortForm sortForm) {
        BooleanBuilder criteria = buildSortingCriteria(sortForm);
        boolean isPaymentDateSelected = isEmptyPaymentDates(sortForm);

        List<Order> sortedOrders = getOrdersByCriteria(criteria, isPaymentDateSelected);
        if(!isPaymentDateSelected) {
            sortedOrders = filterByPaymentDates(sortedOrders, sortForm.getPayDateFrom(), sortForm.getPayDateTill());
        }
        return typeFilterService.filterOrdersForRoles(sortedOrders);
    }

    private boolean isEmptyPaymentDates(SortForm selections) {
        return selections.getPayDateFrom() == null && selections.getPayDateTill() == null;
    }

    private List<Order> getOrdersByCriteria(BooleanBuilder where, boolean isEmptyDates) {
        if (where.getValue() == null && isEmptyDates) {
            return getRecentOrders();
        } else {
            return orderRepository.findAll(where, Sort.by(Sort.Direction.DESC, "orderId"));
        }
    }

    private List<Order> filterByPaymentDates(List<Order> orderList, LocalDate from, LocalDate till) {
        return orderList.stream()
                .filter(o -> Arrays.stream(o.getPayLog().split("\n"))
                        .filter(log -> log.trim().length() > 0)
                        .anyMatch(log -> from == null || PaymentUtils.getLocalDateTimeFromLog(log).toLocalDate().isAfter(from.minusDays(1))))
                .filter(o -> Arrays.stream(o.getPayLog().split("\n"))
                        .filter(log -> log.trim().length() > 0)
                        .anyMatch(log -> till == null || PaymentUtils.getLocalDateTimeFromLog(log).toLocalDate().isBefore(till.plusDays(1))))
                .collect(Collectors.toList());
    }

    private BooleanBuilder buildSortingCriteria(SortForm sortForm) {
        QOrder order = QOrder.order;
        BooleanBuilder where = new BooleanBuilder();
        if(sortForm.getOrderId() != null && sortForm.getOrderId() > 0) where.and(order.orderId.eq(sortForm.getOrderId()));
        if(sortForm.getEntrepreneur() != null) where.and(order.entrepreneur.eq(sortForm.getEntrepreneur()));
        if(sortForm.getCustomer() != null) where.and(order.customer.eq(sortForm.getCustomer()));
        if(sortForm.getEmployee() != null) where.and(order.employee.eq(sortForm.getEmployee()));
        if(sortForm.getStatus() != null) where.and(order.status.eq(sortForm.getStatus()));
        if(sortForm.getDateFrom() != null) where.and(order.date.after(sortForm.getDateFrom().atStartOfDay()));
        if(sortForm.getDateTill() != null) where.and(order.date.before(sortForm.getDateTill().atStartOfDay().plusDays(1)));
        if(sortForm.getOrderKind() != null) where.and(order.orderKind.eq(sortForm.getOrderKind()));
        if(sortForm.getOrderType() != null) where.and(order.orderType.eq(sortForm.getOrderType()));
        if(sortForm.getReceiver() != null && !sortForm.getReceiver().trim().isEmpty()) where.and(order.payLog.contains(sortForm.getReceiver()));
        if(sortForm.getDetails() != null && !sortForm.getDetails().trim().isEmpty()) where.and(order.comment.contains(sortForm.getDetails()));
        return where;
    }
}
