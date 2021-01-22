package com.ordersmanagement.crm;

import com.ordersmanagement.crm.dao.business.OrderRepository;
import com.ordersmanagement.crm.dao.business.OrderTypeRepository;
import com.ordersmanagement.crm.models.entities.Order;
import com.ordersmanagement.crm.models.entities.OrderKind;
import com.ordersmanagement.crm.models.entities.OrderType;
import com.ordersmanagement.crm.services.OrderService;
import com.ordersmanagement.crm.services.OrderTypeService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

//
//@RunWith(SpringRunner.class)
//@SpringBootTest
//class OrdersManagementSystemApplicationTests {
//
//	@Autowired
//	private OrderTypeRepository orderTypeRepository;
//
//	@Test
//    @Transactional
//	public void whenFindByName_thenReturnEmployee() {
//		OrderType found = orderTypeRepository.findById(2).orElse(null);
//        OrderType found2 = orderTypeRepository.findById(3).orElse(null);
//
//        assert found != null;
//        List<OrderKind> orderKinds = found.getOrderKinds();
//		assertThat(orderKinds.size()).isEqualTo(7);
//	}
//}
