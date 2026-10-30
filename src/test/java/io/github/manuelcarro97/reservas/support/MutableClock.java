package io.github.manuelcarro97.reservas.support;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;

public class MutableClock extends Clock {

	public static final Instant START = Instant.parse("2026-12-01T09:00:00Z");

	private volatile Instant now = START;

	public void set(Instant instant) {
		now = instant;
	}

	public void reset() {
		now = START;
	}

	@Override
	public Instant instant() {
		return now;
	}

	@Override
	public ZoneId getZone() {
		return ZoneOffset.UTC;
	}

	@Override
	public Clock withZone(ZoneId zone) {
		return this;
	}

}
