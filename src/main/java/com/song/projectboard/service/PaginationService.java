package com.song.projectboard.service;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.IntStream;

@Service
public class PaginationService {

    private static final int DEFAULT_PAGE_LENGTH = 5;

    public List<Integer> getPaginationNumbers(int currentPageNum, int totalPageNum) {
        int startNum = Math.max(currentPageNum - (DEFAULT_PAGE_LENGTH / 2), 0);
        int endNum = Math.min(startNum + DEFAULT_PAGE_LENGTH, totalPageNum);

        return IntStream.range(startNum, endNum).boxed().toList();
    }
}
