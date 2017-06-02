package com.maoding.corpserver;

import com.maoding.core.bean.ApiResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by Wuwq on 2016/12/14.
 * 全局异常统一处理
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    private final static Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ApiResult noHandlerFoundExceptionHandler(HttpServletRequest req, NoHandlerFoundException ex) throws Exception {
        return ApiResult.urlNotFound("找不到URL：" + ex.getRequestURL(), null);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ApiResult maxUploadSizeExceededExceptionHandler(HttpServletRequest req, MaxUploadSizeExceededException ex) throws Exception {
        return ApiResult.error("上传文件超出最大Size限制：" + ex.getMaxUploadSize(), null);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ApiResult exceptionHandler(HttpServletRequest req, Exception ex) throws Exception {
        logger.error(ex.getMessage(), ex);
        return ApiResult.error(ex.getMessage(), null);
    }
}
