package io.github.manuelcarro97.reservas.domain.exception;

public class RestaurantNotFoundException extends DomainException {

	public RestaurantNotFoundException() {
		super("RESTAURANT_NOT_FOUND", ErrorKind.NOT_FOUND, "Restaurant not found");
	}

}
