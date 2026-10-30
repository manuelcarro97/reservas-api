package io.github.manuelcarro97.reservas.adapter.in.web;

final class TextRules {

	// PostgreSQL no admite el carácter NUL en columnas de texto.
	static final String NO_NUL = "[^\\x00]*";

	private TextRules() {
	}

}
