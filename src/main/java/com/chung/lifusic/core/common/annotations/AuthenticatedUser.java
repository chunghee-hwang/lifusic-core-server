package com.chung.lifusic.core.common.annotations;

import java.lang.annotation.*;

/**
 * 로그인한 유저를 Account서버에 요청하여 받아오는 어노테이션
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthenticatedUser {
}
