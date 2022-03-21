package com.prueba.fhu.rest;

import com.prueba.fhu.model.Customer;
import com.prueba.fhu.repository.AccountRepository;
import com.prueba.fhu.repository.CustomerRepository;
import com.prueba.fhu.repository.MovementRepository;
import com.prueba.fhu.utils.CustomerNotFoundException;

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
@RequestMapping(path = "/api/customer")
public class CustomerRest {

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private MovementRepository movementRepository;
	
	@PostMapping
	public ResponseEntity<Serializable> create(@RequestBody Customer customer) {

		if (customer.getIdentification() == null) {
			return new ResponseEntity<Serializable>("La identificación es requerido", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if (customer.getName() == null) {
			return new ResponseEntity<Serializable>("El nombre es requerido", HttpStatus.INTERNAL_SERVER_ERROR);
		}

		this.customerRepository.save(customer);
		return new ResponseEntity<Serializable>("Nuevo cliente creado correctamente", HttpStatus.OK);
	}

	@GetMapping
	public List<Customer> findAll() {
		return this.customerRepository.findAll();
	}

	@GetMapping("/{id}")
	public Customer oneCustomer(@PathVariable Long id) {

		return this.customerRepository.findById(id).orElseThrow(() -> new CustomerNotFoundException(id));
	}

	@PutMapping("/{id}")
	public ResponseEntity<Serializable> replaceEmployee(@RequestBody Customer newCustomer, @PathVariable Long id) {
		if (id == null) {
			return new ResponseEntity<Serializable>("El id es requerido", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return this.customerRepository.findById(id).map(customer -> {
			customer.setIdentification(newCustomer.getIdentification());
			customer.setName(newCustomer.getName());
			customer.setGender(newCustomer.getGender());
			customer.setAge(newCustomer.getAge());
			customer.setAddress(newCustomer.getAddress());
			customer.setPhone(newCustomer.getPhone());
			this.customerRepository.save(customer);
			return new ResponseEntity<Serializable>("Cliente actualizado correctamente", HttpStatus.OK);
		}).orElseGet(() -> {
			this.customerRepository.save(newCustomer);
			return new ResponseEntity<Serializable>("Nuevo cliente creado correctamente", HttpStatus.OK);
		});
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Serializable> delete(@PathVariable Long id) {
		if (id == null) {
			return new ResponseEntity<Serializable>("El id es requerido", HttpStatus.INTERNAL_SERVER_ERROR);
		}

		if (this.customerRepository.existsById(id) == false) {
			return new ResponseEntity<Serializable>("No existe cliente", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		Customer customer = this.customerRepository.getById(id);
		this.movementRepository.deleteAll(customer.getMovements());
		this.accountRepository.deleteAll(customer.getAccounts());
		this.customerRepository.deleteById(id);
		return new ResponseEntity<Serializable>("Cliente eliminado correctamente", HttpStatus.OK);
	}
}
