package com.tlf.cloud.storage.server.aop;

import com.tlf.common.lang.aop.GlobalLogAspect;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LogAspect extends GlobalLogAspect {

    @Pointcut("within(com.tlf.cloud.storage.server.controller..*) " +
            "&& !@annotation(com.tlf.common.lang.annotation.LogIgnore)")
    public void controllerLogService() {}

}
