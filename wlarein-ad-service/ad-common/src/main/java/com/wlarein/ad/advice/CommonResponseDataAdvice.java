package com.wlarein.ad.advice;

import com.wlarein.ad.annotation.IgnoreResponseAdvice;
import com.wlarein.ad.vo.CommonResponse;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * @RestControllerAdvice 对响应统一拦截
 * ResponseBodyAdvice的作用是在响应体返回之前做一些自定义处理工作，一般我们在这里包装统一的响应返回
 */
@RestControllerAdvice
public class CommonResponseDataAdvice implements ResponseBodyAdvice<Object> {
    /**
     * 该方法用来判断一个响应是否要拦截
     * @param methodParameter
     * @param aClass
     * @return
     */
    @Override
    @SuppressWarnings("all")
    public boolean supports(MethodParameter methodParameter, Class<? extends HttpMessageConverter<?>> aClass) {

        // 如果拿到的类被IgnoreResponseAdvice标识，就不会被拦截
        if(methodParameter.getDeclaringClass().isAnnotationPresent(IgnoreResponseAdvice.class)){
            return false;
        }

        // 如果拿到的方法被IgnoreResponseAdvice标识，就不会被拦截
        if(methodParameter.getMethod().isAnnotationPresent(IgnoreResponseAdvice.class)){
            return false;
        }
        return true;
    }

    /**
     * 该方法用来在写入响应之前做一些操作
     * 对CommonResponse进行拦截
     * @param o 这是响应对象
     * @param methodParameter
     * @param mediaType
     * @param aClass
     * @param serverHttpRequest
     * @param serverHttpResponse
     * @return
     */
    @Nullable
    @Override
    @SuppressWarnings("all")
    public Object beforeBodyWrite(@Nullable Object o,
                                  MethodParameter methodParameter,
                                  MediaType mediaType,
                                  Class<? extends HttpMessageConverter<?>> aClass,
                                  ServerHttpRequest serverHttpRequest,
                                  ServerHttpResponse serverHttpResponse) {

        // 定义最终的响应对象
        CommonResponse<Object> response = new CommonResponse<>(0, "");
        if(null == o){
            // 如果o是null，则respnse不需要设置data
            return response;
        }
        else if(o instanceof CommonResponse){
            // 如果o一句是CommonResponse，强转型即可
            response = (CommonResponse<Object>) o;
        }
        else{
            // 否则，把响应对象作为CommonResponse的data部分
            response.setData(o);
        }
        return response;
    }
}
