package com.ordersmanagement.crm.models.forms;

import com.ordersmanagement.crm.models.entities.*;
import java.lang.reflect.Field;
import java.time.LocalDate;
import lombok.Data;

@Data
public class SortForm {
    private Integer selectedNo;
    private CustomerEntity selectedCustomer;
    private EntrepreneurEntity selectedEntrepreneur;
    private EmployeeEntity selectedEmployee;
    private StatusEntity selectedStatus;
    private LocalDate selectedDateFrom;
    private LocalDate selectedDateTill;
    private LocalDate selectedPayDateFrom;
    private LocalDate selectedPayDateTill;
    private OrderKindEntity selectedKind;
    private OrderTypeEntity selectedType;
    private EmployeeEntity selectedOperator;
    private String selectedReceiver;
    private String selectedDetails;

    public boolean isEmptyForm() {
        Field[] fields = this.getClass().getDeclaredFields();
        for (Field f : fields) {
            try {
                Object value = f.get(this);
                if ((value instanceof String && ((String) value).trim().equals("")) ||  value != null) {
                    return false;
                }
            }
            catch (IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return true;
    }
}
