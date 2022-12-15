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
public class LoggerAspect {

  @Around("execution(* com.oclock.oclock..*Controller.*(..)) || execution(* com.oclock.oclock..*Service.*(..)) || execution(* com.oclock.oclock.repository..*Repository.*(..))")
  public Object printLog(ProceedingJoinPoint joinPoint) throws Throwable {

    String name = joinPoint.getSignature().getDeclaringTypeName();
    String type = "";
    try {
      HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

      if (name.contains("Controller")) {
        type = "Controller ===> ";
        String uuid = UUID.randomUUID().toString();
        request.setAttribute("uuid", uuid);
        log.info("Request ===> uuid = " + uuid + " uri = " + request.getRequestURI());
      } else if (name.contains("Service")) {
        type = "ServiceImpl ===> ";

      } else if (name.contains("Repository")) {
        type = "Repository ===> ";
      }
    }catch (Exception e){
//      e.printStackTrace();
      return joinPoint.proceed();
    }

    log.info(type + name + "." + joinPoint.getSignature().getName() + "()");
    return joinPoint.proceed();
  }

}
