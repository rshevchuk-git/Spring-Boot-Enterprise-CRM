package com.ordersmanagement.crm.models.forms;

import com.ordersmanagement.crm.models.entities.*;
import java.time.LocalDate;
import lombok.Data;

@Data
public class SortForm {
    private Integer orderId;
    private CustomerEntity customer;
    private EntrepreneurEntity entrepreneur;
    private EmployeeEntity employee;
    private StatusEntity status;
    private LocalDate dateFrom;
    private LocalDate dateTill;
    private LocalDate payDateFrom;
    private LocalDate payDateTill;
    private OrderKindEntity orderKind;
    private OrderTypeEntity orderType;
    private String receiver;
    private String details;

//    public boolean isEmptyForm() {
//        Field[] fields = this.getClass().getDeclaredFields();
//        for (Field f : fields) {
//            try {
//                Object value = f.get(this);
//                if ((value instanceof String && ((String) value).trim().equals("")) ||  value != null) {
//                    return false;
//                }
//            }
//            catch (IllegalArgumentException | IllegalAccessException e) {
//                e.printStackTrace();
//            }
//        }
//        return true;
//    }
}
