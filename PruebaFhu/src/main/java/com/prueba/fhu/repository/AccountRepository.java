package com.prueba.fhu.repository;

import com.prueba.fhu.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, String> {
}
