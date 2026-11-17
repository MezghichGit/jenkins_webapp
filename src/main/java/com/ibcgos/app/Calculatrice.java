package com.ibcgos.app;

public class Calculatrice {

	public double addition(double premierNombre, double deuxiemeNombre) {
		return premierNombre + deuxiemeNombre;
	}

	public double soustraction(double premierNombre, double deuxiemeNombre) {
		return premierNombre - deuxiemeNombre;
	}

	public double multiplication(double premierNombre, double deuxiemeNombre) {
		return premierNombre * deuxiemeNombre;
	}

	public double division(double dividende, double diviseur) {
		if (diviseur == 0) {
			throw new ArithmeticException("Division par zero");
		}
		return dividende / diviseur;
	}
}
