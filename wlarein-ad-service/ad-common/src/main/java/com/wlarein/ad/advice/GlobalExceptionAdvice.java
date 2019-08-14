package com.wlarein.ad.advice;

import com.wlarein.ad.exception.AdException;
import com.wlarein.ad.vo.CommonResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionAdvice {
    /**
     * @ExceptionHandler 用来发现AdException异常传进来了,然后捕获
     * @param req
     * @param ex
     * @return
     */
    @ExceptionHandler(value = AdException.class)
    public CommonResponse<String> handlerAdException(HttpServletRequest req,
                                                     AdException ex){
        CommonResponse<String> response = new CommonResponse<>(-1, "bussiness error");
        response.setData(ex.getMessage());
        return response;
    }
}
