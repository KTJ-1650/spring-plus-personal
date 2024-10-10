package org.example.expert.domain.todo.dto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TodoProjectionDto {
    private final String title;
    private final long managerCount;
    private final long commentCount;


}
