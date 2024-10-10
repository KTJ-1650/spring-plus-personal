package org.example.expert.domain.todo.repository;

import org.example.expert.domain.todo.dto.TodoProjectionDto;
import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface TodoQueryRepository {
    Todo findByIdByDsl(long todoId);

//    Page<TodoResponse> search(Pageable pageable);
//
////    TodoProjectionDto findByIdFromProjection(long todoId);
//
   Page<TodoProjectionDto> todoSearch(Pageable pageable, String title, String nickname, LocalDate startDate, LocalDate endDate);
}
