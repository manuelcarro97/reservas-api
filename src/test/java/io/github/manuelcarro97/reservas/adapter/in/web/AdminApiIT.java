package io.github.manuelcarro97.reservas.adapter.in.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import io.github.manuelcarro97.reservas.application.port.in.CreateReservationCommand;
import io.github.manuelcarro97.reservas.application.port.in.CreateReservationUseCase;
import io.github.manuelcarro97.reservas.domain.model.Reservation;
import io.github.manuelcarro97.reservas.domain.model.Restaurant;
import io.github.manuelcarro97.reservas.domain.model.Shift;
import io.github.manuelcarro97.reservas.support.PostgresIT;

@AutoConfigureMockMvc
class AdminApiIT extends PostgresIT {

	private static final LocalDate DATE = LocalDate.of(2026, 12, 2);
	private static final Instant AFTER_DINNER_STARTS = Instant.parse("2026-12-02T19:30:00Z");

	@Autowired
	private MockMvc mvc;

	@Autowired
	private CreateReservationUseCase createReservation;

	private Restaurant restaurant;
	private Shift shift;

	@BeforeEach
	void seed() {
		restaurant = givenRestaurant("demo");
		shift = givenDinnerShift(restaurant);
		givenTable(restaurant, "M2", 2);
	}

	private static String basic(String password) {
		return "Basic " + Base64.getEncoder().encodeToString(("admin:" + password).getBytes());
	}

	private ResultActions asAdmin(MockHttpServletRequestBuilder request) throws Exception {
		return mvc.perform(request.header(HttpHeaders.AUTHORIZATION, basic("test-password")));
	}

	private ResultActions adminPost(String path, String json) throws Exception {
		return asAdmin(post(path).contentType(MediaType.APPLICATION_JSON).content(json));
	}

	private Reservation givenReservation() {
		return createReservation.create("demo",
				new CreateReservationCommand(DATE, shift.id(), 2, "Ana", "600000000", null));
	}

	private String reservationPath(Reservation reservation, String action) {
		return "/api/v1/admin/reservations/" + reservation.id() + "/" + action;
	}

	@Test
	void adminEndpointWithoutCredentialsReturns401() throws Exception {
		mvc.perform(get("/api/v1/admin/restaurants/demo/reservations").param("date", DATE.toString()))
			.andExpect(status().isUnauthorized());
	}

	@Test
	void wrongPasswordReturns401() throws Exception {
		mvc.perform(get("/api/v1/admin/restaurants/demo/reservations").param("date", DATE.toString())
			.header(HttpHeaders.AUTHORIZATION, basic("mala")))
			.andExpect(status().isUnauthorized());
	}

	@Test
	void createRestaurantTableShiftAndClosure() throws Exception {
		adminPost("/api/v1/admin/restaurants", """
				{"slug":"nuevo","name":"Nuevo","zoneId":"Europe/Madrid","cancellationDeadlineHours":3}
				""").andExpect(status().isCreated());
		adminPost("/api/v1/admin/restaurants/nuevo/tables", """
				{"label":"T1","capacity":4,"posX":10,"posY":20}
				""").andExpect(status().isCreated());
		adminPost("/api/v1/admin/restaurants/nuevo/shifts", """
				{"name":"Comida","start":"13:00","end":"15:00","days":["MONDAY","SUNDAY"]}
				""").andExpect(status().isCreated()).andExpect(jsonPath("$.id").isNumber());
		adminPost("/api/v1/admin/restaurants/nuevo/closures", """
				{"date":"2026-12-25","reason":"Navidad"}
				""").andExpect(status().isCreated());

		mvc.perform(get("/api/v1/restaurants/nuevo/availability").param("date", "2026-12-25").param("partySize", "2"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$").isEmpty());
	}

	@Test
	void duplicateSlugReturns409() throws Exception {
		adminPost("/api/v1/admin/restaurants", """
				{"slug":"demo","name":"Otro","zoneId":"Europe/Madrid","cancellationDeadlineHours":2}
				""").andExpect(status().isConflict()).andExpect(jsonPath("$.code").value("ALREADY_EXISTS"));
	}

	@Test
	void unknownZoneReturns400() throws Exception {
		adminPost("/api/v1/admin/restaurants", """
				{"slug":"zona","name":"Zona","zoneId":"Marte/Base","cancellationDeadlineHours":2}
				""").andExpect(status().isBadRequest()).andExpect(jsonPath("$.code").value("INVALID_REQUEST"));
	}

	@Test
	void shiftEndingBeforeItStartsReturns400() throws Exception {
		adminPost("/api/v1/admin/restaurants/demo/shifts", """
				{"name":"Mal","start":"15:00","end":"13:00","days":["MONDAY"]}
				""").andExpect(status().isBadRequest()).andExpect(jsonPath("$.code").value("INVALID_REQUEST"));
	}

	@Test
	void reservationListShowsContactData() throws Exception {
		givenReservation();

		asAdmin(get("/api/v1/admin/restaurants/demo/reservations").param("date", DATE.toString()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$[0].customerPhone").value("600000000"))
			.andExpect(jsonPath("$[0].tableLabel").value("M2"));
	}

	@Test
	void confirmThenNoShowFollowsStateMachine() throws Exception {
		Reservation reservation = givenReservation();

		asAdmin(post(reservationPath(reservation, "confirm"))).andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("CONFIRMED"));
		clock.set(AFTER_DINNER_STARTS);
		asAdmin(post(reservationPath(reservation, "no-show"))).andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("NO_SHOW"));
	}

	@Test
	void noShowBeforeShiftStartsReturns409() throws Exception {
		Reservation reservation = givenReservation();
		asAdmin(post(reservationPath(reservation, "confirm")));

		asAdmin(post(reservationPath(reservation, "no-show"))).andExpect(status().isConflict())
			.andExpect(jsonPath("$.code").value("INVALID_TRANSITION"));
	}

	@Test
	void adminCanCancelAfterCustomerDeadline() throws Exception {
		Reservation reservation = givenReservation();
		clock.set(AFTER_DINNER_STARTS);

		asAdmin(post(reservationPath(reservation, "cancel"))).andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("CANCELLED"));
	}

	@Test
	void unknownReservationReturns404() throws Exception {
		asAdmin(post("/api/v1/admin/reservations/999/confirm")).andExpect(status().isNotFound())
			.andExpect(jsonPath("$.code").value("RESERVATION_NOT_FOUND"));
	}

	@Test
	void occupancyReturnsPositions() throws Exception {
		givenReservation();

		asAdmin(get("/api/v1/admin/restaurants/demo/occupancy").param("date", DATE.toString())
			.param("shiftId", shift.id().toString()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$[0].posX").value(1))
			.andExpect(jsonPath("$[0].posY").value(2))
			.andExpect(jsonPath("$[0].occupied").value(true));
	}

	@Test
	void concurrentConfirmsOneGets409() throws Exception {
		Reservation reservation = givenReservation();
		CountDownLatch start = new CountDownLatch(1);
		Callable<Integer> confirm = () -> {
			start.await();
			return asAdmin(post(reservationPath(reservation, "confirm"))).andReturn().getResponse().getStatus();
		};

		List<Integer> statuses = new ArrayList<>();
		try (ExecutorService executor = Executors.newFixedThreadPool(2)) {
			List<Future<Integer>> futures = List.of(executor.submit(confirm), executor.submit(confirm));
			start.countDown();
			for (Future<Integer> future : futures) {
				statuses.add(future.get());
			}
		}

		assertThat(statuses).containsExactlyInAnyOrder(200, 409);
		assertThat(jdbc.sql("SELECT status FROM reservation").query(String.class).single()).isEqualTo("CONFIRMED");
	}

}
