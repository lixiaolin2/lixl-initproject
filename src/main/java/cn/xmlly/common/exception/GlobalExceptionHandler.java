package cn.xmlly.common.exception;

import cn.xmlly.common.base.BaseApiService;
import cn.xmlly.common.base.BaseResponse;
import cn.xmlly.common.enums.SystemEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Set;


/**
 * 全局异常处理
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ResponseBody
    @ExceptionHandler(value = MethodArgumentTypeMismatchException.class)
    public BaseResponse paramMisMatch(MethodArgumentTypeMismatchException ex) {
       return new BaseApiService().setResultParamsErrorMsg(ex.getName()+"参数格式有问题!");
    }

    @ResponseBody
    @ExceptionHandler(value = HttpRequestMethodNotSupportedException.class)
    public BaseResponse paramMisMatch(HttpRequestMethodNotSupportedException ex) {
        log.error("HttpRequestMethodNotSupportedException error:{}",ex);
       return new BaseApiService().setResultParamsErrorMsg("请求方式错误！");
    }

    @ResponseBody
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public BaseResponse paramMisMatch(MethodArgumentNotValidException ex) {
        StringBuffer msg=new StringBuffer();
        ex.getBindingResult().getAllErrors().forEach(objectError -> {
            msg.append(objectError.getDefaultMessage());
        });
        return new BaseApiService().setResultParamsErrorMsg(msg.toString());
    }
    @ResponseBody
    @ExceptionHandler(value = BindException.class)
    public BaseResponse paramMisMatch(BindException ex) {
        StringBuffer msg=new StringBuffer();
        ex.getBindingResult().getAllErrors().forEach(objectError -> {
            msg.append(objectError.getDefaultMessage());
        });
        return new BaseApiService().setResultParamsErrorMsg(msg.toString());
    }

    @ResponseBody
    @ExceptionHandler(value = ConstraintViolationException.class)
    public BaseResponse paramMisMatch(ConstraintViolationException exs) {
        StringBuffer msg=new StringBuffer();
        Set<ConstraintViolation<?>> violations = exs.getConstraintViolations();
        for (ConstraintViolation<?> item : violations) {
            msg.append(item.getMessage());
        }
        return new BaseApiService().setResultParamsErrorMsg(msg.toString());
    }

    @ExceptionHandler(RespException.class)
    @ResponseBody
    public BaseResponse exceptionProcess(RespException e) {
        log.info(e.getMessage());
        return new BaseApiService().setResult(e.getCode(),e.getMessage(),null);
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public BaseResponse exceptionProcess(Exception e) {
        log.error("Exception error:{}",e);
        return new BaseApiService().setResult(SystemEnum.FAIL.getCode(), SystemEnum.FAIL.getMsg(),null);
    }
}
