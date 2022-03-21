package com.prueba.fhu.rest;

import com.prueba.fhu.model.Account;
import com.prueba.fhu.model.Customer;
import com.prueba.fhu.model.Movement;
import com.prueba.fhu.repository.AccountRepository;
import com.prueba.fhu.repository.MovementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(path = "/api/movement")
public class MovementRest {

	@Autowired
	private MovementRepository movementRepository;
	@Autowired
	private AccountRepository accountRepository;

	private BigDecimal dailyLimit = new BigDecimal(1000);
	private Date today;

	@PostMapping(path = "/account/{numberAccount}")
	public ResponseEntity<Serializable> create(@PathVariable String numberAccount, @RequestBody Movement movement) {
		try {
			if (this.accountRepository.existsById(numberAccount) == false) {
				return new ResponseEntity<Serializable>("No existe cuenta", HttpStatus.INTERNAL_SERVER_ERROR);
			}
			if (movement.getDate() == null) {
				today = new Date();
			} else {
				today = movement.getDate();
			}
			if ("Retiro".equals(movement.getMovementType())) {
				List<Movement> todayMovements = this.movementRepository
						.findByAccountAndDateBetween(new Account(numberAccount), today, today);
				BigDecimal todayDebits = calculateDailyDebits(todayMovements);
				movement.setValue(movement.getValue().negate());
				System.out.println("--->" + todayDebits);

				if (todayDebits.compareTo(BigDecimal.ZERO) == 0) {
					todayDebits = movement.getValue().abs();
				} else {

					todayDebits = todayDebits.abs().add(movement.getValue().abs());
				}

				if (todayDebits.compareTo(dailyLimit) > 0) {
					return new ResponseEntity<Serializable>("Cupo diario excedido", HttpStatus.INTERNAL_SERVER_ERROR);
				}
			}

			Account account = this.accountRepository.findById(numberAccount).get();
			BigDecimal newBalance = calculateNewBalance(account.getBalance(), movement.getValue(),
					movement.getMovementType());

			if (newBalance.compareTo(BigDecimal.ZERO) == -1) {
				return new ResponseEntity<Serializable>("Saldo no disponible", HttpStatus.INTERNAL_SERVER_ERROR);
			}

			account.setBalance(newBalance);
			movement.setCustomer(new Customer(account.getCustomer().getCustomerId()));
			movement.setAccount(new Account(numberAccount));
			movement.setBalance(newBalance);

			if (movement.getDate() == null) {
				movement.setDate(today);
			}
			this.accountRepository.save(account);

			this.movementRepository.save(movement);
			return new ResponseEntity<Serializable>("Se realizó el " + movement.getMovementType() + " Correctamente ",
					HttpStatus.OK);
		} catch (Exception exc) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, exc.getMessage(), exc);
		}
	}

	@GetMapping
	public List<Movement> findAll() {
		return this.movementRepository.findAll();
	}

	private BigDecimal calculateDailyDebits(List<Movement> todayMovements) throws Exception {

		BigDecimal todayDebits = BigDecimal.ZERO;

		for (Movement movement : todayMovements) {
			if ("Retiro".equals(movement.getMovementType())) {
				todayDebits = todayDebits.add(movement.getValue().abs());
			}
		}

		return todayDebits;

	}

	private BigDecimal calculateNewBalance(BigDecimal balance, BigDecimal value, String movementType) throws Exception {
		BigDecimal newBalance = BigDecimal.ZERO;

		if ("Retiro".equals(movementType)) {
			newBalance = balance.subtract(value.abs());
		} else {
			newBalance = balance.add(value.abs());
		}
		return newBalance;

	}
}
