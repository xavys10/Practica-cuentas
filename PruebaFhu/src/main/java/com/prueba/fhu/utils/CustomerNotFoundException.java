package com.prueba.fhu.utils;

public class CustomerNotFoundException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CustomerNotFoundException(Long id) {
		super("No se pudo encontrar al cliente" + id);
	}
}
