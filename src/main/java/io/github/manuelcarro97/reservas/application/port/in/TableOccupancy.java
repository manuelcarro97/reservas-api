package io.github.manuelcarro97.reservas.application.port.in;

public record TableOccupancy(long tableId, String label, int capacity, int posX, int posY, boolean occupied) {
}
