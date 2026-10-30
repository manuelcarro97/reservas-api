package io.github.manuelcarro97.reservas.application.port.out;

import java.util.List;
import java.util.Optional;

import io.github.manuelcarro97.reservas.domain.model.Shift;

public interface ShiftRepositoryPort {

	Optional<Shift> findById(long restaurantId, long shiftId);

	List<Shift> findAll(long restaurantId);

	Shift save(Shift shift);

}
