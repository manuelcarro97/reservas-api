package io.github.manuelcarro97.reservas.domain.service;

import java.util.Comparator;
import java.util.List;

import io.github.manuelcarro97.reservas.domain.model.DiningTable;

public class TableAssigner {

	public List<DiningTable> candidates(List<DiningTable> free, int partySize) {
		return free.stream()
			.filter(table -> table.capacity() >= partySize)
			.sorted(Comparator.comparingInt(DiningTable::capacity).thenComparing(DiningTable::id))
			.toList();
	}

}
