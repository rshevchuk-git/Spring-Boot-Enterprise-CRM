package com.ordersmanagement.crm.services;

import com.ordersmanagement.crm.models.entities.Order;
import com.ordersmanagement.crm.utils.OrderExcelExporter;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Service
@AllArgsConstructor
public class ExporterService {

    private final OrderService orderService;
    private final String path = "/home/application/lastOrders.xlsx";

    @Scheduled(cron = "0 0 0 * * ?")
    private void exportDB() throws IOException {
        List<Order> orderList = orderService.getRecentOrders();
        Collections.reverse(orderList);
        OrderExcelExporter orderExcelExporter = new OrderExcelExporter(orderList);
        orderExcelExporter.exportToFile(path);
    }
}
