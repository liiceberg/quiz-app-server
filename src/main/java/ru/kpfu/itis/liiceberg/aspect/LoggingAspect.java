package ru.kpfu.itis.liiceberg.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.annotation.Annotation;
import java.util.Arrays;

@Aspect
@Component
public class LoggingAspect {
    public static final Logger LOGGER = LoggerFactory.getLogger(LoggingAspect.class.getName());

    @Pointcut("execution(* ru.kpfu.itis.liiceberg.controller..*.*(..))")
    public void logController() {
    }
    @Pointcut("execution(* ru.kpfu.itis.liiceberg.filter.JwtFilter.doFilter())")
    public void logFilter() {

    }
    @AfterThrowing(pointcut = "logController() || logFilter()", throwing = "ex")
    public void logException(Exception ex) {
        String message = "An error occurred: " + ex.getMessage();
        LOGGER.error(message);
    }


    @Around("logController()")
    public Object log(ProceedingJoinPoint joinPoint) {

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        LOGGER.info("Method: {}", signature.getName());

        for (Annotation a : signature.getMethod().getDeclaredAnnotations()) {
            if (a.annotationType().isAnnotationPresent(RequestMapping.class)) {
                RequestMapping[] reqMappingAnnotations = a.annotationType().getAnnotationsByType(RequestMapping.class);

                for (RequestMapping annotation : reqMappingAnnotations) {
                    for (RequestMethod reqMethod : annotation.method()) {
                        LOGGER.info("Request method: {}", reqMethod.name());
                    }
                    LOGGER.info("Headers: {}", Arrays.toString(annotation.headers()));
                    LOGGER.info("Params: {}", Arrays.toString(annotation.params()));
                }
            }
        }
        Object result;
        try {
            result = joinPoint.proceed();
        } catch (Throwable ex) {
            return null;
        }

        return result;
    }
}
