package com.ordersmanagement.crm.services;

import com.ordersmanagement.crm.dao.business.CustomerRepository;
import com.ordersmanagement.crm.models.dto.SortForm;
import com.ordersmanagement.crm.models.entities.Customer;
import com.ordersmanagement.crm.utils.PaymentUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CustomerService {

    private final MailService mailService;
    private final CustomerRepository customerRepository;

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public boolean existsById(Integer customerId) {
        return customerRepository.existsById(customerId);
    }

    public Optional<Customer> getById(Integer customerId) {
        return customerRepository.findById(customerId);
    }

    public Customer saveCustomer(Customer newCustomer) {
        mailService.sendNotification(newCustomer);
        return customerRepository.save(newCustomer);
    }

    public boolean deleteCustomer(Integer customerID){
        if(existsById(customerID)) {
            customerRepository.deleteById((customerID));
            return true;
        } else {
            return false;
        }
    }

    public int paidOnCustomerBalance(SortForm selections){
        if (selections.getReceiver() == null || selections.getReceiver().isEmpty()) {
            selections.setReceiver("Отримувач");
            if (selections.getCustomer() == null && selections.getPayDateFrom() == null && selections.getPayDateTill() == null) {
                return 0;
            }
        }

        List<Customer> customerList;
        if(selections.getCustomer() != null) {
            customerList = customerRepository.findAllByCustomerIdAndPayLogContaining(selections.getCustomer().getCustomerId(), selections.getReceiver());
        } else {
            customerList = customerRepository.findAllByPayLogContaining(selections.getReceiver());
        }
        return calculatePaymentsSum(customerList, selections.getReceiver(), selections.getPayDateFrom(), selections.getPayDateTill());
    }

    private int calculatePaymentsSum(List<Customer> customerList, String receiver, LocalDate from, LocalDate till) {
        return customerList.stream()
                .map(Customer::getPayLog)
                .map(log -> log.split("\n"))
                .flatMap(Arrays::stream)
                .filter(log -> from == null || PaymentUtils.getLocalDateTimeFromLog(log).toLocalDate().isAfter(from.minusDays(1)))
                .filter(log -> till == null || PaymentUtils.getLocalDateTimeFromLog(log).toLocalDate().isBefore(till.plusDays(1)))
                .filter(log -> log.contains(receiver))
                .reduce(0, (currentVal, log) -> currentVal + PaymentUtils.getSumFromLog(log), Integer::sum);
    }
}
