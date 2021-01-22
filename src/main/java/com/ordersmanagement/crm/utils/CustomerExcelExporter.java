package com.ordersmanagement.crm.utils;

import com.ordersmanagement.crm.models.entities.Customer;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class CustomerExcelExporter {

    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private List<Customer> customers;

    public CustomerExcelExporter(List<Customer> customers) {
        this.customers = customers;
        customers.sort(Comparator.comparing(Customer::getCustomerName));

        this.workbook = new XSSFWorkbook();
        this.sheet = workbook.createSheet("Клієнти");
    }

    private void writeHeaderRow() {
        Row row = sheet.createRow(0);
        row.createCell(0).setCellValue("Назва Замовника");
        row.createCell(1).setCellValue("Група");
        row.createCell(2).setCellValue("Конт. Особа 1");
        row.createCell(3).setCellValue("Телефон 1");
        row.createCell(4).setCellValue("Email 1");
        row.createCell(5).setCellValue("Конт. Особа 2");
        row.createCell(6).setCellValue("Телефон 2");
        row.createCell(7).setCellValue("Email 2");
        row.createCell(8).setCellValue("Конт. Особа 3");
        row.createCell(9).setCellValue("Телефон 3");
        row.createCell(10).setCellValue("Email 3");
        row.createCell(11).setCellValue("Місто");
        row.createCell(12).setCellValue("Інформація");
    }

    private void writeDataRows(){
        AtomicInteger rowIdx = new AtomicInteger(1);

        customers.forEach(customer -> {
            Row row = sheet.createRow(rowIdx.get());

            row.createCell(0).setCellValue(customer.getCustomerName());
            row.createCell(1).setCellValue(customer.getCustomerGroup());
            row.createCell(2).setCellValue(customer.getFirstPerson());
            row.createCell(3).setCellValue(customer.getFirstPhone());
            row.createCell(4).setCellValue(customer.getFirstEmail());
            row.createCell(5).setCellValue(customer.getSecondPerson());
            row.createCell(6).setCellValue(customer.getSecondPhone());
            row.createCell(7).setCellValue(customer.getSecondEmail());
            row.createCell(8).setCellValue(customer.getThirdPerson());
            row.createCell(9).setCellValue(customer.getThirdEmail());
            row.createCell(10).setCellValue(customer.getThirdEmail());
            row.createCell(11).setCellValue(customer.getCity());
            row.createCell(12).setCellValue(customer.getInfo());

            rowIdx.getAndIncrement();
        });

        for (int i = 0; i < 13; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    public ByteArrayInputStream export() throws IOException {
        writeHeaderRow();
        writeDataRows();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);

        workbook.close();
        outputStream.close();

        return new ByteArrayInputStream(outputStream.toByteArray());
    }
}
