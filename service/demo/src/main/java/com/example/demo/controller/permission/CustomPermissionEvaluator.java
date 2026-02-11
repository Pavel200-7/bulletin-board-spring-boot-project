package com.example.demo.controller.permission;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Slf4j
@Component("testEvaluator")
public class CustomPermissionEvaluator implements PermissionEvaluator {

    private final int expectedNum = 111;

    public boolean hasPermission(Authentication auth, Integer num) {
        log.error("Вызван кастомный метод с 2 параметрами");
        if (auth == null || num == null) {
            return false;
        }
        return num == expectedNum;
    }

    @Override
    public boolean hasPermission(Authentication auth, Object targetDomainObject, Object permission) {
        if ((auth == null) || (targetDomainObject == null) || !(permission instanceof String)){
            return false;
        }

        log.error("проверку прохожу 2");

        if (targetDomainObject instanceof Integer) {
            int num = (Integer) targetDomainObject;
            return num == expectedNum;
        }

        return false;
    }

    @Override
    public boolean hasPermission(Authentication auth, Serializable targetId, String targetType, Object permission) {
        if ((auth == null) || (targetType == null) || !(permission instanceof String)) {
            return false;
        }

        log.error("проверку прохожу 1");
        int num = (int)targetId;

        return num == expectedNum;
    }
}
