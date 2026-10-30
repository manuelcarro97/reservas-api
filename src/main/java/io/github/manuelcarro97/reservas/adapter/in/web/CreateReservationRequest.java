package io.github.manuelcarro97.reservas.adapter.in.web;

import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import io.github.manuelcarro97.reservas.application.port.in.CreateReservationCommand;

record CreateReservationRequest(@NotNull LocalDate date, @NotNull Long shiftId, @Min(1) int partySize,
		@NotBlank @Size(max = 100) String customerName, @NotBlank @Size(max = 30) String customerPhone,
		@Email @Size(max = 254) String customerEmail) {

	CreateReservationCommand toCommand() {
		return new CreateReservationCommand(date, shiftId, partySize, customerName, customerPhone, customerEmail);
	}

}
