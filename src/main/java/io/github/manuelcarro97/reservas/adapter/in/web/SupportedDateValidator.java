package io.github.manuelcarro97.reservas.adapter.in.web;

import java.time.LocalDate;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

class SupportedDateValidator implements ConstraintValidator<SupportedDate, LocalDate> {

	private static final int MIN_YEAR = 2000;
	private static final int MAX_YEAR = 2100;

	@Override
	public boolean isValid(LocalDate date, ConstraintValidatorContext context) {
		return date == null || (date.getYear() >= MIN_YEAR && date.getYear() <= MAX_YEAR);
	}

}
