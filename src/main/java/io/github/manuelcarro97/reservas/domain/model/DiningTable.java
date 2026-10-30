package io.github.manuelcarro97.reservas.domain.model;

public record DiningTable(Long id, long restaurantId, String label, int capacity, int posX, int posY) {
}
