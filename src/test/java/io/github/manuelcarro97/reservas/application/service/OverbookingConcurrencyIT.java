package io.github.manuelcarro97.reservas.application.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import io.github.manuelcarro97.reservas.application.port.in.CreateReservationCommand;
import io.github.manuelcarro97.reservas.application.port.in.CreateReservationUseCase;
import io.github.manuelcarro97.reservas.domain.exception.NoTableAvailableException;
import io.github.manuelcarro97.reservas.domain.model.Reservation;
import io.github.manuelcarro97.reservas.domain.model.Restaurant;
import io.github.manuelcarro97.reservas.domain.model.Shift;
import io.github.manuelcarro97.reservas.support.PostgresIT;

class OverbookingConcurrencyIT extends PostgresIT {

	private static final LocalDate DATE = LocalDate.of(2026, 12, 2);
	private static final int THREADS = 20;

	@Autowired
	private CreateReservationUseCase createReservation;

	private List<Reservation> reserveInParallel(long shiftId) throws InterruptedException, ExecutionException {
		CountDownLatch start = new CountDownLatch(1);
		Callable<Reservation> attempt = () -> {
			start.await();
			try {
				return createReservation.create("demo",
						new CreateReservationCommand(DATE, shiftId, 2, "Ana", "600000000", null));
			}
			catch (NoTableAvailableException noTable) {
				return null;
			}
		};
		try (ExecutorService executor = Executors.newFixedThreadPool(THREADS)) {
			List<Future<Reservation>> futures = new ArrayList<>();
			for (int i = 0; i < THREADS; i++) {
				futures.add(executor.submit(attempt));
			}
			start.countDown();
			List<Reservation> created = new ArrayList<>();
			for (Future<Reservation> future : futures) {
				Reservation reservation = future.get();
				if (reservation != null) {
					created.add(reservation);
				}
			}
			return created;
		}
	}

	private long activeRows() {
		return jdbc.sql("SELECT count(*) FROM reservation WHERE status IN ('PENDING', 'CONFIRMED')")
			.query(Long.class)
			.single();
	}

	@Test
	void onlyOneWinsLastTable() throws Exception {
		Restaurant restaurant = givenRestaurant("demo");
		Shift shift = givenDinnerShift(restaurant);
		givenTable(restaurant, "M1", 2);

		assertThat(reserveInParallel(shift.id())).hasSize(1);
		assertThat(activeRows()).isEqualTo(1);
	}

	@Test
	void neverExceedsTableCount() throws Exception {
		Restaurant restaurant = givenRestaurant("demo");
		Shift shift = givenDinnerShift(restaurant);
		givenTable(restaurant, "M1", 2);
		givenTable(restaurant, "M2", 2);
		givenTable(restaurant, "M3", 2);

		List<Reservation> created = reserveInParallel(shift.id());

		assertThat(created).hasSize(3);
		assertThat(created).extracting(Reservation::tableId).doesNotHaveDuplicates();
		assertThat(activeRows()).isEqualTo(3);
	}

}
