package com.ordersmanagement.crm.services;

import com.ordersmanagement.crm.dao.orders.OrderRepository;
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

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;


@Service
@AllArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CustomerService customerService;
    private final TypeFilterService typeFilterService;

    public Optional<OrderEntity> getOrderById(Integer id) {
        return orderRepository.findById(id);
    }

    public List<OrderEntity> getUnpaidOrdersOf(CustomerEntity customer, EntrepreneurEntity entrepreneur) {
        return orderRepository.getUnpaidOrdersOf(customer, entrepreneur);
    }

    public List<OrderEntity> getAllByOrderKind(OrderKindEntity orderKind) {
        return orderRepository.findByOrderKind(orderKind);
    }

    public List<OrderEntity> getAllByOrderType(OrderTypeEntity orderType) {
        return orderRepository.findByOrderType(orderType);
    }

    public List<OrderEntity> getCustomerOrders(Integer customerId) {
        return orderRepository.getOrdersMadeBy(customerId);
    }

    public List<OrderEntity> getRecentOrders() {
        LocalDateTime dayBefore = LocalDateTime.now(ZoneId.of("Europe/Kiev")).minusDays(5);
        return orderRepository.getOrdersStartingFrom(dayBefore);
    }

    public OrderEntity changeOrderStatus(OrderEntity order, StatusEntity status) {
        order.setStatus(status);
        return orderRepository.save(order);
    }

    public boolean isCustomerChanged(OrderEntity changedOrder) throws OrderNotFoundException {
        OrderEntity savedOrder = orderRepository.findById(changedOrder.getOrderId()).orElseThrow(OrderNotFoundException::new);
        return savedOrder.getCustomer().getCustomerId() != changedOrder.getCustomer().getCustomerId();
    }

    public void saveAll(List<OrderEntity> orders) {
        orderRepository.saveAll(orders);
    }

    public void deleteById(Integer orderId) {
        orderRepository.deleteById(orderId);
    }

    public OrderEntity saveNewOrder(OrderEntity newOrder) {
        newOrder.setOrderId(0);
        newOrder.setDate(LocalDateTime.now(ZoneId.of("Europe/Kiev")));
        newOrder.setM2(OrderUtils.calculateM2(newOrder));
        return orderRepository.save(newOrder);
    }

    public OrderEntity updateOrder(OrderEntity newOrder) {
        newOrder.setM2(OrderUtils.calculateM2(newOrder));
        newOrder.setPaySum(PaymentUtils.calculatePaymentSum(newOrder.getPayLog()));
        if (newOrder.getPaySum() > 0) {
            Payment lastPayment = PaymentUtils.getLastPayment(newOrder.getPayLog());
            LocalDateTime lastPaymentDate = lastPayment.getDateTime();
            newOrder.setPayDate(lastPaymentDate);
        }
        return orderRepository.save(newOrder);
    }

    public Summary summarize(List<OrderEntity> orders, String paymentMethod, CustomerEntity customer) {
        double  m2 = OrderUtils.totalOrdersM2(orders);
        int   fees = OrderUtils.totalOrdersFees(orders);
        int amount = OrderUtils.totalOrdersAmount(orders);
        int   paid = OrderUtils.totalOrdersPaid(orders, paymentMethod);

        if (paymentMethod != null && !paymentMethod.isEmpty()) {
            paid += customerService.paidOnCustomerBalance(customer.getCustomerId(), paymentMethod);
        }
        return new Summary(orders, paid, fees, amount, m2);
    }

    public List<OrderEntity> getSortedOrders(SortForm sortForm) {
        QOrderEntity order = QOrderEntity.orderEntity;
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
        if(sortForm.getReceiver() != null) where.and(order.payLog.contains(sortForm.getReceiver()));
        if(sortForm.getPayDateFrom() != null) where.and(order.payDate.after(sortForm.getPayDateFrom().atStartOfDay()));
        if(sortForm.getPayDateTill() != null) where.and(order.payDate.before(sortForm.getPayDateTill().atStartOfDay().plusDays(1)));
        if(sortForm.getDetails() != null && !sortForm.getDetails().trim().equals("")) where.and(order.comment.contains(sortForm.getDetails()));

        List<OrderEntity> sortedOrders = getOrdersBySelections(where);
        return typeFilterService.filterOrdersForRoles(sortedOrders);
    }

    private List<OrderEntity> getOrdersBySelections(BooleanBuilder where) {
        if (where.getValue() == null) {
            return getRecentOrders();
        } else {
            return orderRepository.findAll(where, Sort.by(Sort.Direction.DESC, "orderId"));
        }
    }
}
