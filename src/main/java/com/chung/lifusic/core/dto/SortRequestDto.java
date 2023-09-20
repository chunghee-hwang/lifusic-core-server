package com.chung.lifusic.core.dto;

import com.chung.lifusic.core.common.utils.QueryUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Sort;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SortRequestDto {
    private String orderBy; // 정렬 대상 필드 이름
    private String orderDirection; // asc or desc
    public Sort.Order toOrder(String ...permittedOrderBy) {
        return QueryUtil.getOrder(this.orderBy, this.orderDirection, permittedOrderBy);
    }
}
