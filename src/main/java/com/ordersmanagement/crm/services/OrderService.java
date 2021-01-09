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
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Service
@AllArgsConstructor
public class OrderService {

    private final AuthService authService;
    private final CustomerService customerService;
    private final OrderTypeService orderTypeService;
    private final OrderRepository orderRepository;
    private final OrderRepositoryCustom customOrderRepository;

    public List<OrderEntity> getRecentOrders() {
        return customOrderRepository.getRecentOrders();
    }

    public List<OrderEntity> getCustomerOrders(Integer customerId) {
        return customOrderRepository.getOrdersMadeBy(customerId);
    }

    public Optional<OrderEntity> getOrderById(Integer id) {
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
        newOrder.setPaySum(PaymentUtils.calculatePaymentSum(newOrder.getPayLog()));
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
        OrderEntity savedOrder = orderRepository.findById(changedOrder.getOrderId()).orElseThrow(OrderNotFoundException::new);
        return savedOrder.getCustomer().getCustomerId() != changedOrder.getCustomer().getCustomerId();
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
        return filterAllowedOrdersForRoles(sortedOrders);
    }

    public List<OrderEntity> getOrdersBySelections(BooleanBuilder where) {
        if(where.getValue() == null) {
            return getRecentOrders();
        } else {
            return orderRepository.findAll(where, Sort.by(Sort.Direction.DESC, "orderId"));
        }
    }

    public List<OrderEntity> filterAllowedOrdersForRoles(List<OrderEntity> orderList) {
        for (GrantedAuthority grantedAuthority : authService.getUserRoles()) {
            orderList = orderList.stream()
                    .filter(orderEntity -> orderTypeService.typeFilterByRole
                            .getOrDefault(grantedAuthority.getAuthority(), (val) -> true)
                            .apply(orderEntity.getOrderType()))
                    .collect(toList());
        }
        return orderList;
    }
}
