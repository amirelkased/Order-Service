package com.fawry.orderservice.common;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class ResponsePageMapper {

    public <T> ResponsePage<T> toResponsePage(Page<T> pageResult) {
        return ResponsePage.<T>builder()
                .data(pageResult.getContent())
                .totalElements(pageResult.getTotalElements())
                .numberOfElements(pageResult.getNumberOfElements())
                .totalPages(pageResult.getTotalPages())
                .pageNumber(pageResult.getNumber())
                .pageSize(pageResult.getSize())
                .build();
    }
}
