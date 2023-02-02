package ru.elseff.demo.exception.handling.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@EqualsAndHashCode
@RequiredArgsConstructor
public class Violation {
    private final String fieldName;

    private final String message;
}
