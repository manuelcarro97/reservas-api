package io.github.manuelcarro97.reservas.domain.exception;

public class RestaurantClosedException extends DomainException {

	public RestaurantClosedException() {
		super("RESTAURANT_CLOSED", "The restaurant is closed on that date");
	}

}
