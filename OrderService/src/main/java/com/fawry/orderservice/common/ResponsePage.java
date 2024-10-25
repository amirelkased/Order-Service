package com.fawry.orderservice.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponsePage <T>{
    private List<T> data;
    private Long totalElements;
    private int totalPages;
    private int pageSize;
    private int pageNumber;
    private int numberOfElements;
}

