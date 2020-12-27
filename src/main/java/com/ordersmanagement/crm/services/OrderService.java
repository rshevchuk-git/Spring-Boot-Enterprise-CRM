package com.ordersmanagement.crm.services;

import com.ordersmanagement.crm.dao.orders.*;
import com.ordersmanagement.crm.exceptions.OrderNotFoundException;
import com.ordersmanagement.crm.models.entities.*;
import com.ordersmanagement.crm.models.forms.SortForm;
import com.ordersmanagement.crm.models.response.Summary;
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

    private final CustomerService customerService;
    private final OrderRepository orderRepository;
    private final OrderRepositoryCustom customOrderRepository;

    public List<OrderEntity> getRecentOrders(){
        return customOrderRepository.getRecentOrders();
    }

    public List<OrderEntity> getCustomerOrders(Integer id){
        return customOrderRepository.getOrdersMadeBy(id);
    }

    public Optional<OrderEntity> getOrderById(Integer id){
        return orderRepository.findById(id);
    }

    public OrderEntity saveNewOrder(OrderEntity newOrder) {
        newOrder.setOrderId(0);
        newOrder.setDate(LocalDateTime.now(ZoneId.of("Europe/Kiev")));
        newOrder.setM2(OrderUtils.calculateM2(newOrder));
        return orderRepository.save(newOrder);
    }

    public OrderEntity updateOrder(OrderEntity newOrder) {
        newOrder.setM2(OrderUtils.calculateM2(newOrder));
        if (newOrder.getPaySum() > 0) {
            newOrder.setPayDate(PaymentUtils.getLocalDateTimeFromLog(PaymentUtils.getLastPayment(newOrder.getPayLog())));
        }
        return orderRepository.save(newOrder);
    }

    public boolean deleteOrder(int orderId) {
        if (orderRepository.existsById(orderId)) {
            orderRepository.deleteById(orderId);
            return true;
        } else {
            return false;
        }
    }

    public Boolean isCustomerChanged(OrderEntity changedOrder) throws OrderNotFoundException {
        OrderEntity persistedOrder = orderRepository.findById(changedOrder.getOrderId()).orElseThrow(OrderNotFoundException::new);
        return persistedOrder.getCustomer().getCustomerId() != changedOrder.getCustomer().getCustomerId();
    }

    public Summary summarize(List<OrderEntity> orders, String paymentMethod, CustomerEntity customer) {
        double  m2 = OrderUtils.totalOrdersM2(orders);
        int   fees = OrderUtils.totalOrdersFees(orders);
        int amount = OrderUtils.totalOrdersAmount(orders);
        int   paid = OrderUtils.totalOrdersPaid(orders, paymentMethod);

        if (!paymentMethod.isEmpty()) {
            paid += customerService.paymentsOnCustomerBalance(paymentMethod, customer);
        }
        return new Summary(orders, paid, fees, amount, m2);
    }

    public List<OrderEntity> getSortedOrders(SortForm sortForm) {
        QOrderEntity order = QOrderEntity.orderEntity;
        BooleanBuilder where = new BooleanBuilder();
        if(sortForm.getSelectedNo() != null && sortForm.getSelectedNo() > 0) where.and(order.orderId.eq(sortForm.getSelectedNo()));
        if(sortForm.getSelectedEntrepreneur() != null) where.and(order.entrepreneur.eq(sortForm.getSelectedEntrepreneur()));
        if(sortForm.getSelectedOperator() != null) where.and(order.employee.eq(sortForm.getSelectedOperator()));
        if(sortForm.getSelectedCustomer() != null) where.and(order.customer.eq(sortForm.getSelectedCustomer()));
        if(sortForm.getSelectedEmployee() != null) where.and(order.employee.eq(sortForm.getSelectedEmployee()));
        if(sortForm.getSelectedStatus() != null) where.and(order.status.eq(sortForm.getSelectedStatus()));
        if(sortForm.getSelectedDateFrom() != null) where.and(order.date.after(sortForm.getSelectedDateFrom().atStartOfDay()));
        if(sortForm.getSelectedDateTill() != null) where.and(order.date.before(sortForm.getSelectedDateTill().atStartOfDay().plusDays(1)));
        if(sortForm.getSelectedKind() != null) where.and(order.orderKind.eq(sortForm.getSelectedKind()));
        if(sortForm.getSelectedType() != null) where.and(order.orderType.eq(sortForm.getSelectedType()));
        if(sortForm.getSelectedReceiver() != null) where.and(order.payLog.contains(sortForm.getSelectedReceiver()));
        if(sortForm.getSelectedPayDateFrom() != null) where.and(order.payDate.after(sortForm.getSelectedPayDateFrom().atStartOfDay()));
        if(sortForm.getSelectedPayDateTill() != null) where.and(order.payDate.before(sortForm.getSelectedPayDateTill().atStartOfDay().plusDays(1)));
        if(sortForm.getSelectedDetails() != null && !sortForm.getSelectedDetails().trim().equals("")) where.and(order.comment.contains(sortForm.getSelectedDetails()));
        return orderRepository.findAll(where, Sort.by(Sort.Direction.DESC, "orderId"));
    }
}
