package io.github.manuelcarro97.reservas.application.port.out;

import java.time.LocalDate;
import java.util.Optional;

import io.github.manuelcarro97.reservas.domain.model.Restaurant;

public interface RestaurantRepositoryPort {

	Optional<Restaurant> findBySlug(String slug);

	Optional<Restaurant> findById(long id);

	Restaurant save(Restaurant restaurant);

	void addClosure(long restaurantId, LocalDate date, String reason);

	boolean isClosed(long restaurantId, LocalDate date);

	int maxCapacity(long restaurantId);

}
