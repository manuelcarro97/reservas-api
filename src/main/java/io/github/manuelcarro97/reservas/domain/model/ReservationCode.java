package io.github.manuelcarro97.reservas.domain.model;

import java.security.SecureRandom;

public final class ReservationCode {

	private static final String ALPHABET = "23456789ABCDEFGHJKLMNPQRSTUVWXYZ";
	private static final int LENGTH = 16;
	private static final SecureRandom RANDOM = new SecureRandom();

	private ReservationCode() {
	}

	public static String generate() {
		StringBuilder code = new StringBuilder(LENGTH);
		for (int i = 0; i < LENGTH; i++) {
			code.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
		}
		return code.toString();
	}

}
