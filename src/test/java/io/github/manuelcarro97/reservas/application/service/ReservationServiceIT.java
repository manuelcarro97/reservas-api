package io.github.manuelcarro97.reservas.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import io.github.manuelcarro97.reservas.application.port.in.CreateReservationCommand;
import io.github.manuelcarro97.reservas.application.port.in.CreateReservationUseCase;
import io.github.manuelcarro97.reservas.domain.exception.ShiftNotFoundException;
import io.github.manuelcarro97.reservas.domain.model.DiningTable;
import io.github.manuelcarro97.reservas.domain.model.Reservation;
import io.github.manuelcarro97.reservas.domain.model.ReservationStatus;
import io.github.manuelcarro97.reservas.domain.model.Restaurant;
import io.github.manuelcarro97.reservas.domain.model.Shift;
import io.github.manuelcarro97.reservas.support.PostgresIT;

class ReservationServiceIT extends PostgresIT {

	private static final LocalDate DATE = LocalDate.of(2026, 12, 2);

	@Autowired
	private CreateReservationUseCase createReservation;

	private Restaurant restaurant;
	private Shift shift;
	private DiningTable small;
	private DiningTable big;

	@BeforeEach
	void seed() {
		restaurant = givenRestaurant("demo");
		shift = givenDinnerShift(restaurant);
		big = givenTable(restaurant, "M4", 4);
		small = givenTable(restaurant, "M2", 2);
	}

	private Reservation reserveTwo(long shiftId) {
		return createReservation.create("demo",
				new CreateReservationCommand(DATE, shiftId, 2, "Ana", "600000000", null));
	}

	@Test
	void createsPendingReservationWithCode() {
		Reservation reservation = reserveTwo(shift.id());

		assertThat(reservation.status()).isEqualTo(ReservationStatus.PENDING);
		assertThat(reservation.code()).hasSize(16);
	}

	@Test
	void assignsSmallestTableThatFits() {
		assertThat(reserveTwo(shift.id()).tableId()).isEqualTo(small.id());
	}

	@Test
	void fallsBackToNextTableWhenSmallestTaken() {
		reserveTwo(shift.id());

		assertThat(reserveTwo(shift.id()).tableId()).isEqualTo(big.id());
	}

	@Test
	void shiftFromOtherRestaurantThrowsNotFound() {
		Restaurant other = givenRestaurant("otro");
		Shift foreignShift = givenDinnerShift(other);

		assertThatThrownBy(() -> reserveTwo(foreignShift.id())).isInstanceOf(ShiftNotFoundException.class);
		assertThat(jdbc.sql("SELECT count(*) FROM reservation").query(Long.class).single()).isZero();
	}

}
