package com.prueba.fhu.rest;

import com.prueba.fhu.dto.Report;
import com.prueba.fhu.model.Account;
import com.prueba.fhu.model.Customer;
import com.prueba.fhu.model.Movement;
import com.prueba.fhu.repository.MovementRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(path = "/api/report")
@Slf4j
public class ReportRest {

    @Autowired
    private MovementRepository movementRepository;

    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");

    @GetMapping(path = "/customer/{customerId}/startDate/{startDate}/endDate/{endDate}")
    private List<Report> query(@PathVariable Long customerId, @PathVariable String startDate, @PathVariable String endDate) {


        try {
            Date start = formatter.parse(startDate);
            Date end = formatter.parse(endDate);
            System.out.println(start);
            System.out.println(end);
            List<Movement> movements = this.movementRepository.findByCustomerAndDateBetween(new Customer(customerId), start, end);
            return toReportListFrom(movements);
        } catch (ParseException e) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, e.getMessage(), e);
        }

    }

    private List<Report> toReportListFrom(List<Movement> movements) {

        List<Report> reportList = new ArrayList<>();
        Report report;
        for (Movement movement : movements) {
            report = new Report();
            report.setCustomer(movement.getCustomer().getName());
            Account account = movement.getAccount();
            report.setAccount(account.getNumber());
            report.setInitialBalance(account.getInitialBalance());
            report.setType(account.getType());
            report.setDate(formatter.format(movement.getDate()));
            report.setStatus(account.getStatus());
            report.setBalance(movement.getBalance());
            report.setValue(movement.getValue());
            reportList.add(report);
        }

        return reportList;
    }

}
