package com.prueba.fhu.repository;

import com.prueba.fhu.model.Account;
import com.prueba.fhu.model.Customer;
import com.prueba.fhu.model.Movement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface MovementRepository extends JpaRepository<Movement, Long> {


    List<Movement> findByAccountAndDateBetween(Account account, Date startDate, Date endDate);
    
    List<Movement> findByCustomerAndDateBetween(Customer customer, Date startDate, Date endDate);

}
