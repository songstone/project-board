package com.song.projectboard.repository.querydsl;

import java.util.List;

public interface CustomArticleRepository {

    List<String> findAllHashtags();
}
