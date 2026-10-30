package io.github.manuelcarro97.reservas.adapter.in.web;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

record CreateClosureRequest(@NotNull LocalDate date, @Size(max = 200) @Pattern(regexp = TextRules.NO_NUL) String reason) {
}
