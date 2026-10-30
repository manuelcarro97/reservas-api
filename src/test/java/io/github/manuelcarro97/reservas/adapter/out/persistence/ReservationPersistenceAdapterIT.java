package io.github.manuelcarro97.reservas.adapter.out.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.EnumSet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import io.github.manuelcarro97.reservas.application.port.out.ReservationRepositoryPort;
import io.github.manuelcarro97.reservas.domain.exception.ConcurrentUpdateException;
import io.github.manuelcarro97.reservas.domain.model.DiningTable;
import io.github.manuelcarro97.reservas.domain.model.Reservation;
import io.github.manuelcarro97.reservas.domain.model.Restaurant;
import io.github.manuelcarro97.reservas.domain.model.Shift;
import io.github.manuelcarro97.reservas.support.PostgresIT;

class ReservationPersistenceAdapterIT extends PostgresIT {

	private static final LocalDate DATE = LocalDate.of(2026, 12, 1);

	@Autowired
	private ReservationRepositoryPort reservations;

	private Restaurant restaurant;
	private DiningTable table;
	private Shift shift;

	@BeforeEach
	void seed() {
		restaurant = restaurants.save(new Restaurant(null, "demo", "Demo", ZoneId.of("Europe/Madrid"), 2));
		table = tables.save(new DiningTable(null, restaurant.id(), "M1", 4, 10, 20));
		shift = shifts.save(new Shift(null, restaurant.id(), "Cena", LocalTime.of(20, 0), LocalTime.of(22, 0),
				EnumSet.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY)));
	}

	private Reservation reservation(String code) {
		return Reservation.newPending(code, restaurant.id(), shift.id(), table.id(), DATE, 2, "Ana", "600000000", null,
				Instant.parse("2026-11-01T10:00:00Z"));
	}

	@Test
	void shiftKeepsItsDaysOfWeek() {
		assertThat(shifts.findById(restaurant.id(), shift.id()).orElseThrow().days())
				.containsExactlyInAnyOrder(DayOfWeek.MONDAY, DayOfWeek.TUESDAY);
	}

	@Test
	void tryInsertReturnsReservationWithId() {
		assertThat(reservations.tryInsert(reservation("A")).orElseThrow().id()).isNotNull();
	}

	@Test
	void tryInsertReturnsEmptyWhenSlotTaken() {
		reservations.tryInsert(reservation("A"));

		assertThat(reservations.tryInsert(reservation("B"))).isEmpty();
	}

	@Test
	void findFreeExcludesActiveReservations() {
		reservations.tryInsert(reservation("A"));

		assertThat(tables.findFree(restaurant.id(), shift.id(), DATE)).isEmpty();
	}

	@Test
	void findFreeIncludesCancelledReservations() {
		Reservation inserted = reservations.tryInsert(reservation("A")).orElseThrow();
		inserted.cancel();
		reservations.update(inserted);

		assertThat(tables.findFree(restaurant.id(), shift.id(), DATE)).containsExactly(table);
	}

	@Test
	void updateWithStaleVersionThrowsConcurrentUpdate() {
		Reservation inserted = reservations.tryInsert(reservation("A")).orElseThrow();
		Reservation first = reservations.findById(inserted.id()).orElseThrow();
		Reservation stale = reservations.findById(inserted.id()).orElseThrow();
		first.confirm();
		reservations.update(first);
		stale.cancel();

		assertThatThrownBy(() -> reservations.update(stale)).isInstanceOf(ConcurrentUpdateException.class);
	}

	@Test
	void closureIsReportedForThatDateOnly() {
		restaurants.addClosure(restaurant.id(), DATE, "Festivo");

		assertThat(restaurants.isClosed(restaurant.id(), DATE)).isTrue();
		assertThat(restaurants.isClosed(restaurant.id(), DATE.plusDays(1))).isFalse();
	}

	@Test
	void maxCapacityIsTheBiggestTable() {
		tables.save(new DiningTable(null, restaurant.id(), "M2", 8, 0, 0));

		assertThat(restaurants.maxCapacity(restaurant.id())).isEqualTo(8);
	}

}
