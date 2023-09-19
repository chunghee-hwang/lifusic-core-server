package com.chung.lifusic.core.common.utils;

import com.chung.lifusic.core.common.constants.DefaultQuery;
import com.chung.lifusic.core.exception.BadRequestException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Arrays;

public class PageUtil {
    public static Pageable getPage(Integer page, Integer limit, String orderBy, String orderDirection, String ...permittedOrderBys) {
        Sort sort;
        if (page == null || page < 1) {
            page = 1;
        }
        if (limit == null || limit < 1) {
            limit = DefaultQuery.DEFAULT_LIMIT;
        }
        if (orderBy == null) {
            if (permittedOrderBys != null && permittedOrderBys.length > 0) {
                orderBy = permittedOrderBys[0];
            } else {
                orderBy = "createdDate";
            }
        } else {
            if (permittedOrderBys != null && permittedOrderBys.length > 0) {
                boolean isPermittedOrderBy = Arrays.asList(permittedOrderBys).contains(orderBy);
                if (!isPermittedOrderBy) {
                    throw new BadRequestException();
                }
            }
        }

        if (orderDirection == null || (!orderDirection.equalsIgnoreCase("asc") && !orderDirection.equalsIgnoreCase("desc"))) {
            sort = Sort.by(Sort.Order.asc(orderBy));
        } else {
            if (orderDirection.equalsIgnoreCase("asc")) {
                sort = Sort.by(Sort.Order.asc(orderBy));
            } else {
                sort = Sort.by(Sort.Order.desc(orderBy));
            }
        }
        return PageRequest.of(page - 1, limit, sort);
    }
}
