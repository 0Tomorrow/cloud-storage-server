package com.tlf.cloud.storage.server.aop;

import com.tlf.commonlang.aop.GlobalLogAspect;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LogAspect extends GlobalLogAspect {

    @Pointcut("execution(public * com.tlf.cloud.storage.server.controller.*.*(..)) " +
            "&& !execution(public * com.tlf.cloud.storage.server.controller.FileController.sliceUpload(..))")
    public void controllerLogService() {}

}
