package com.emp.model;

import java.time.LocalDate;

public record Holiday(Long id, LocalDate date, String name) {
}
