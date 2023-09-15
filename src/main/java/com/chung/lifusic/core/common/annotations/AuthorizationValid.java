package com.chung.lifusic.core.common.annotations;

import com.chung.lifusic.core.common.enums.Role;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 사용자가 특정 역할을 인가 받았는 지 확인하는 어노테이션.
 * 특정 역할이 아니면 403 forbidden 반환
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthorizationValid {
    Role role();
}
