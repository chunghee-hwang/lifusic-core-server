package com.chung.lifusic.core.dto;

import com.chung.lifusic.core.common.constants.DefaultQuery;
import com.chung.lifusic.core.exception.BadRequestException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Arrays;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetMusicsRequestDto {
    private String keyword;

    private String orderBy;

    private String orderDirection;

    private Integer limit;

    private Integer page;
}
