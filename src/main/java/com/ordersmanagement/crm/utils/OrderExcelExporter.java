package com.ordersmanagement.crm.utils;

import com.ordersmanagement.crm.models.entities.OrderEntity;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class OrderExcelExporter {

    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private List<OrderEntity> orders;

    public OrderExcelExporter(List<OrderEntity> orders) {
        this.orders = orders;
        this.workbook = new XSSFWorkbook();
        this.sheet = workbook.createSheet("Замовлення");
    }

    private void writeHeaderRow(){
        Row row = sheet.createRow(0);
        row.createCell(0).setCellValue("№");
        row.createCell(1).setCellValue("Дата");
        row.createCell(2).setCellValue("Замовник");
        row.createCell(3).setCellValue("Ширина");
        row.createCell(4).setCellValue("Висота");
        row.createCell(5).setCellValue("Кількість");
        row.createCell(6).setCellValue("М2");
        row.createCell(7).setCellValue("Ціна");
        row.createCell(8).setCellValue("Послуги");
        row.createCell(9).setCellValue("Сума");
        row.createCell(10).setCellValue("Оплачено");
        row.createCell(11).setCellValue("Дата Оплати");
        row.createCell(12).setCellValue("Вид");
        row.createCell(13).setCellValue("Тип");
        row.createCell(14).setCellValue("Назва замовлення");
    }

    private void writeDataRows(){
        AtomicInteger rowIdx = new AtomicInteger(1);
        AtomicBoolean firstLogWritten = new AtomicBoolean(false);

        XSSFDataFormat ch = workbook.createDataFormat();
        XSSFCellStyle cs = workbook.createCellStyle();
        cs.setDataFormat(ch.getFormat("# ##,0\\ [$грн]"));

        orders.forEach(order -> {
            Row row = sheet.createRow(rowIdx.get());

            row.createCell(0).setCellValue(order.getOrderId());
            row.createCell(1).setCellValue(order.getDate() == null ? "" : order.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy (hh:mm)")));
            row.createCell(2).setCellValue(order.getCustomer().getCustomerName());
            row.createCell(3).setCellValue(order.getWidth());
            row.createCell(4).setCellValue(order.getHeight());
            row.createCell(5).setCellValue(order.getAmount());
            row.createCell(6).setCellValue(order.getM2());
            row.createCell(7).setCellValue(order.getPrice());
            row.createCell(8).setCellValue(order.getFees());
            row.createCell(9).setCellValue(order.getFinalSum());
            row.createCell(12).setCellValue(order.getOrderType().getTypeName());
            row.createCell(13).setCellValue(order.getOrderKind().getKindName());
            row.createCell(14).setCellValue(order.getComment());

            List<String> logs = Arrays.asList(order.getPayLog().split("\\n"));
            logs.forEach(log -> {
                if (log.isEmpty()) return;

                if (!firstLogWritten.get()) {
                    parseAndSetValuesOf(log, row);
                    firstLogWritten.set(true);
                } else {
                    Row logRow = sheet.createRow(rowIdx.get());
                    parseAndSetValuesOf(log, logRow);
                }

                rowIdx.getAndIncrement();
            });

            if (!firstLogWritten.get()) {
                rowIdx.getAndIncrement();
            }

            firstLogWritten.set(false);
        });

        Row row = sheet.createRow(rowIdx.get() + 2);
        row.createCell(8).setCellValue("Борг :");

        XSSFCell debtValue = (XSSFCell) row.createCell(9);
        debtValue.setCellValue(orders.stream().reduce(0, (a, b) -> a + (b.getFinalSum() - b.getPaySum()), Integer::sum));
        debtValue.setCellStyle(cs);

        for (int i = 0; i < 15; i++) {
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

    private LocalDateTime getLocalDateTimeFromLog(String log){
        DateTimeFormatter DATE_FORMAT =
                new DateTimeFormatterBuilder().appendPattern("dd/MM/yyyy[ [HH][:mm][:ss][.SSS]]")
                        .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
                        .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
                        .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
                        .toFormatter();
        return LocalDateTime.parse(log.substring(log.indexOf("та : ") + 5, log.indexOf(" Сум") - 1), DATE_FORMAT);
    }

    private void parseAndSetValuesOf(String log, Row row){
        row.createCell(10).setCellValue(Integer.parseInt(log.substring(log.indexOf("ма : ") + 5, log.indexOf(" Отр") - 1)));
        row.createCell(11).setCellValue(getLocalDateTimeFromLog(log).format(DateTimeFormatter.ofPattern("dd/MM/yyyy (hh:mm)")));
    }
}
