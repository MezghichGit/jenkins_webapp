package com.ibcgos.app;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class CalculatriceTest {

	private final Calculatrice calculatrice = new Calculatrice();

	@Test
	void additionneDeuxNombres() {
		assertEquals(7.0, calculatrice.addition(3.0, 4.0));
	}

	@Test
	void soustraitDeuxNombres() {
		assertEquals(1.0, calculatrice.soustraction(5.0, 4.0));
	}

	@Test
	void multiplieDeuxNombres() {
		assertEquals(12.0, calculatrice.multiplication(3.0, 4.0));
	}

	@Test
	void diviseDeuxNombres() {
		assertEquals(2.0, calculatrice.division(8.0, 4.0));
	}

	@Test
	void refuseUneDivisionParZero() {
		assertThrows(ArithmeticException.class, () -> calculatrice.division(8.0, 0.0));
	}
}
