package io.github.manuelcarro97.reservas.adapter.in.scheduling;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import io.github.manuelcarro97.reservas.application.port.in.ExpirePendingReservationsUseCase;

@Component
class PendingExpirationJob {

	private final ExpirePendingReservationsUseCase expirePending;

	PendingExpirationJob(ExpirePendingReservationsUseCase expirePending) {
		this.expirePending = expirePending;
	}

	@Scheduled(initialDelayString = "PT5M", fixedDelayString = "PT5M")
	void expirePendingReservations() {
		expirePending.expire();
	}

}
