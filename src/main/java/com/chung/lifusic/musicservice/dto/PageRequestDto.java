package com.chung.lifusic.musicservice.dto;

import com.chung.lifusic.musicservice.common.utils.QueryUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Pageable;


@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageRequestDto extends SortRequestDto{
    private Integer limit;
    private Integer page;
    public Pageable toPage(String ...permittedOrderBy) {
        return QueryUtil.getPage(this.page, this.limit, this.getOrderBy(), this.getOrderDirection(), permittedOrderBy);
    }
}
