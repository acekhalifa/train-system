package com.esl.academy.api.core.constants;

import lombok.Getter;

@Getter
public enum CacheId {

    AUTH_USER("authUserCache"),

    AUTH_TOKEN("authTokenCache");

    private final String cacheName;

    CacheId(String cacheName) {
        this.cacheName = cacheName;
    }

}
