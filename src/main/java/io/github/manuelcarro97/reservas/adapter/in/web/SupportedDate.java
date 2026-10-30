package io.github.manuelcarro97.reservas.adapter.in.web;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

/** Limita las fechas a un rango que PostgreSQL pueda almacenar y que tenga sentido para un local. */
@Target({ ElementType.FIELD, ElementType.PARAMETER, ElementType.RECORD_COMPONENT })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = SupportedDateValidator.class)
@interface SupportedDate {

	String message() default "date is out of the supported range";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

}
