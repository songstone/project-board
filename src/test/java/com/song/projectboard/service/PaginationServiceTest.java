package com.song.projectboard.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@DisplayName("페이지네이션 서비스 테스트")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = PaginationService.class) // 테스트 무게 줄이기
class PaginationServiceTest {

    private final PaginationService paginationService;

    public PaginationServiceTest(@Autowired PaginationService paginationService) {
        this.paginationService = paginationService;
    }

    @DisplayName("현재 페이지 번호, 총 페이지 번호 -> 페이징 리스트 반환")
    @MethodSource("testPageNumSource")
    @ParameterizedTest
    void get_pagination_numbers(int currentPageNumber, int totalPages, List<Integer> expects) {
        //given

        //when
        List<Integer> actual = paginationService.getPaginationNumbers(currentPageNumber, totalPages);

        //then
        assertThat(actual).isEqualTo(expects);
    }

    static Stream<Arguments> testPageNumSource() {
        return Stream.of(
            arguments(0, 13, List.of(0, 1, 2, 3, 4)),
            arguments(1, 13, List.of(0, 1, 2, 3, 4)),
            arguments(2, 13, List.of(0, 1, 2, 3, 4)),
            arguments(3, 13, List.of(1, 2, 3, 4, 5)),
            arguments(4, 13, List.of(2, 3, 4, 5, 6)),
            arguments(5, 13, List.of(3, 4, 5, 6, 7)),
            arguments(10, 13, List.of(8, 9, 10, 11, 12)),
            arguments(11, 13, List.of(9, 10, 11, 12)),
            arguments(12, 13, List.of(10, 11, 12))
        );
    }

}