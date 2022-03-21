package com.prueba.fhu.rest;

import com.prueba.fhu.model.Account;
import com.prueba.fhu.model.Customer;
import com.prueba.fhu.repository.AccountRepository;
import com.prueba.fhu.repository.CustomerRepository;
import com.prueba.fhu.repository.MovementRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.Serializable;
import java.util.List;

@RestController
@RequestMapping(path = "/api/account")
public class AccountRest {

	@Autowired
	private AccountRepository accountRepository;
	
	@Autowired
	private CustomerRepository customerRepository;
	
	@Autowired
	private MovementRepository movementRepository;
	
	@PostMapping(path = "/customer/{customerId}")
	public ResponseEntity<Serializable> create(@PathVariable Long customerId, @RequestBody Account account) {
		
		if (this.customerRepository.existsById(customerId) == false) {
			return new ResponseEntity<Serializable>("No existe cliente", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if (account.getNumber() == null) {
			return new ResponseEntity<Serializable>("El número de cuenta es requerido",
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if (account.getType() == null) {
			return new ResponseEntity<Serializable>("El tipo de cuenta es requerido", HttpStatus.INTERNAL_SERVER_ERROR);
		}

		if (this.accountRepository.existsById(account.getNumber()) == true) {
			return new ResponseEntity<Serializable>("Número de cuenta ya existe", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		account.setCustomer(new Customer(customerId));
		account.setBalance(account.getInitialBalance());
		this.accountRepository.save(account);
		return new ResponseEntity<Serializable>("Nueva cuenta creada correctamente", HttpStatus.OK);
	}

	@GetMapping
	public List<Account> findAll() {
		return this.accountRepository.findAll();
	}

	@PutMapping("/{numberAccount}")
	public ResponseEntity<Serializable> modify(@PathVariable String numberAccount, @RequestBody Account newAccount) {
		if (numberAccount == null) {
			return new ResponseEntity<Serializable>("El número de cuenta es requerido", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return this.accountRepository.findById(numberAccount).map(account -> {
			account.setBalance(newAccount.getBalance());
			account.setInitialBalance(newAccount.getInitialBalance());
			account.setBalance(newAccount.getInitialBalance());
			account.setStatus(newAccount.getStatus());
			account.setType(newAccount.getType());
			this.accountRepository.save(account);
			return new ResponseEntity<Serializable>("Cuenta actualizada correctamente", HttpStatus.OK);
		}).orElseGet(() -> {
			return new ResponseEntity<Serializable>("No existe cuenta", HttpStatus.INTERNAL_SERVER_ERROR);
		});
	}
	
	@DeleteMapping("/{numberAccount}")
	public ResponseEntity<Serializable> delete(@PathVariable String numberAccount) {
		if (numberAccount == null) {
			return new ResponseEntity<Serializable>("El id es requerido", HttpStatus.INTERNAL_SERVER_ERROR);
		}

		if (this.accountRepository.existsById(numberAccount) == false) {
			return new ResponseEntity<Serializable>("No existe cuenta", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		Account account = this.accountRepository.getById(numberAccount);
		this.movementRepository.deleteAll(account.getMovements());
		this.accountRepository.deleteById(numberAccount);
		return new ResponseEntity<Serializable>("Cuenta eliminada correctamente", HttpStatus.OK);
	}
}
