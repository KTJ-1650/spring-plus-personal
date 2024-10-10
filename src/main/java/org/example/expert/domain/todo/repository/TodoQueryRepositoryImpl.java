package org.example.expert.domain.todo.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.todo.dto.TodoProjectionDto;
import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.example.expert.domain.comment.entity.QComment.comment;
import static org.example.expert.domain.manager.entity.QManager.manager;
import static org.example.expert.domain.todo.entity.QTodo.todo;
import static org.example.expert.domain.user.entity.QUser.user;

@Repository
@RequiredArgsConstructor
public class TodoQueryRepositoryImpl implements TodoQueryRepository{

    private final JPAQueryFactory queryFactory;
    @Override
    public Todo findByIdByDsl(long todoId) {

        return queryFactory
                .select(todo)
                .from(todo)
                .leftJoin(todo.user).fetchJoin()
                .where(
                        todoIdEq(todoId)
                ).fetchOne();
    }

    @Override
    public Page<TodoProjectionDto> todoSearch(Pageable pageable, String title,String nickname, LocalDate startDate, LocalDate endDate ) {
//생성자,에노테이션 //중복방지 //JPAQuery라는 타입이 별도 존재.
        List<TodoProjectionDto> query = queryFactory
                .select(Projections.constructor(
                        TodoProjectionDto.class,
                        todo.title,
                        manager.id.countDistinct(),
                        comment.id.countDistinct()
                ))
                .from(todo)
                .leftJoin(todo.managers, manager)
                .leftJoin(todo.comments, comment)
                .where(
                        titleContains(title),
                        nicknameContains(nickname),
                        createdAtBetween(startDate, endDate)
                )
                .orderBy(todo.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        //페이징 처리하면 무조건 전체 개수 조회를 해야 한다.
        // 전체 개수 조회
        long total = queryFactory
                .select(todo.count())
                .from(todo)
                .where(
                        titleContains(title),
                        nicknameContains(nickname),
                        createdAtBetween(startDate, endDate)
                )
                .fetchOne();

        return new PageImpl<>(query, pageable, total);
    }



    private BooleanExpression todoIdEq(Long todoId) {
        return todoId != null ? todo.id.eq(todoId) : null;
    }


    private BooleanExpression titleContains(String title) {
        return title != null ? todo.title.contains(title) : null;
    }

    private BooleanExpression nicknameContains(String nickname) {
        return nickname != null ? user.nickname.contains(nickname) : null;
    }

    private BooleanExpression createdAtBetween(LocalDate startDate, LocalDate endDate) {
        if (startDate != null && endDate != null) {
            return todo.createdAt.between(startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX));
        }
        return null;
    }

}
