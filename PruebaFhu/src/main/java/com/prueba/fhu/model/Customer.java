package com.prueba.fhu.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.Set;

@Entity
@Data
@RequiredArgsConstructor
@NoArgsConstructor
public class Customer extends Person {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "customer_id")
	@NonNull
	private Long customerId;
	private String password;
	private Boolean status;

	@OneToMany(mappedBy = "customer", fetch = FetchType.LAZY)
	private Set<Account> accounts;

	@OneToMany(mappedBy = "customer")
	Set<Movement> movements;

	public Customer(@NonNull Long customerId, String password, Boolean status, Set<Account> accounts) {

		this.customerId = customerId;
		this.password = password;
		this.status = status;
		this.accounts = accounts;
	}

	public Customer() {

	}

	public Customer(Long customerId2) {
		this.customerId = customerId2;
	}

	public Set<Movement> getMovements() {
		return movements;
	}

	public void setMovements(Set<Movement> movements) {
		this.movements = movements;
	}

	public Long getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Boolean getStatus() {
		return status;
	}

	public void setStatus(Boolean status) {
		this.status = status;
	}

	public Set<Account> getAccounts() {
		return accounts;
	}

	public void setAccounts(Set<Account> accounts) {
		this.accounts = accounts;
	}

}
