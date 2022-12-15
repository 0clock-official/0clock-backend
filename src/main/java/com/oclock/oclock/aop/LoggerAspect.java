package com.oclock.oclock.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

@Slf4j
@Aspect
@Component
public class LoggerAspect { //Todo 웹소켓 통신시 작동하는 핸들러에도 동작하도록 수정 필요.

    @Around("execution(* com.oclock.oclock..*Controller.*(..)) || execution(* com.oclock.oclock.controller.websocket.*.*(..)) " +
            "|| execution(* com.oclock.oclock..*Service.*(..)) || execution(* com.oclock.oclock.repository..*Repository.*(..))")
    public Object printLog(ProceedingJoinPoint joinPoint) throws Throwable {

        String name = joinPoint.getSignature().getDeclaringTypeName();
        String type = "";

        if (name.contains("Controller")) {
            type = "Controller ===> ";
            String uuid = UUID.randomUUID().toString();
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
            request.setAttribute("uuid", uuid);
            log.info("Request ===> uuid = " + uuid + " uri = " + request.getRequestURI());
        } else if (name.contains("Service")) {
            type = "ServiceImpl ===> ";

        } else if (name.contains("Repository")) {
            type = "Repository ===> ";
        } else {
            type = "ChattingHandler ===> ";
        }

        log.info(type + name + "." + joinPoint.getSignature().getName() + "()");
        return joinPoint.proceed();
    }

}
