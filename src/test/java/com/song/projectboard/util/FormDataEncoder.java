package com.song.projectboard.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@TestComponent
public class FormDataEncoder {

    private final ObjectMapper objectMapper;

    public FormDataEncoder(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String encode(Object object) {
        Map<String, String> fields = objectMapper.convertValue(object, new TypeReference<>() {});
        MultiValueMap<String, String> values = new LinkedMultiValueMap<>();
        values.setAll(fields);

        return UriComponentsBuilder.newInstance()
            .queryParams(values)
            .encode()
            .build()
            .getQuery();
    }
}
