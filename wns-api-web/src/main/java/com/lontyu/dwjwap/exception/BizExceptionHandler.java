package com.lontyu.dwjwap.exception;

import com.lontyu.dwjwap.dto.BaseResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * 异常处理器
 *
 * @author Mark sunlightcs@gmail.com
 * @since 1.0.0 2016-10-27
 */
@RestControllerAdvice
public class BizExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(BizExceptionHandler.class);
    /**
     * 处理自定义异常
     */
    @ExceptionHandler(BizException.class)
    public BaseResponse handleBizException(BizException e) {
        logger.error(getTrace(e));
        return BaseResponse.buildFail(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public BaseResponse handleException(Exception e) {
        logger.error(getTrace(e));
        return BaseResponse.buildFail(9999, e.getMessage());
    }

    private String getTrace(Throwable t) {
        StringWriter stringWriter= new StringWriter();
        PrintWriter writer= new PrintWriter(stringWriter);
        t.printStackTrace(writer);
        StringBuffer buffer= stringWriter.getBuffer();
        return buffer.toString();
    }
}
