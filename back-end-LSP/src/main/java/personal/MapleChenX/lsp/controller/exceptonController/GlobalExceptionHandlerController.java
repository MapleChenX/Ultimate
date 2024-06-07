package personal.MapleChenX.lsp.controller.exceptonController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import personal.MapleChenX.lsp.common.model.ret.ResponseTemplate;

import static personal.MapleChenX.lsp.common.enums.ServerEnum.*;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandlerController {

    @ExceptionHandler(ValidationException.class)
    public ResponseTemplate<?> validateError(ValidationException exception) {
        log.warn("Resolved [{}: {}]", exception.getClass().getName(), exception.getMessage());
        return ResponseTemplate.failure(400, "请求参数有误");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseTemplate<?> handleValidationExceptions(MethodArgumentNotValidException ex, WebRequest request) {
        String errorMessage = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        return ResponseTemplate.failure(801, errorMessage);
    }

//    @ExceptionHandler(CustomException.class)
//    public ResponseTemplate<?> handleCustomException(CustomException ex) {
//        ex.printStackTrace();
//        System.out.println("dsadsad");
//        return ResponseTemplate.failure(ex.getCode() != null ? ex.getCode() : 250, ex.getMessage());
//    }

    @ExceptionHandler(value = Exception.class)
    ResponseTemplate handleException(Exception e, HttpServletRequest request) {
        log.error("请求错误，请求地址{},错误信息:", request.getRequestURL(), e);
        ResponseTemplate<Object> resp = new ResponseTemplate<>();
        //404
        if (e instanceof NoHandlerFoundException) {
            resp.setCode(CODE_404.getCode());
            resp.setMessage(CODE_404.getMsg());
        } else if (e instanceof BindException|| e instanceof MethodArgumentTypeMismatchException) {
            //参数类型错误
            resp.setCode(CODE_600.getCode());
            resp.setMessage(CODE_600.getMsg());
        } else if (e instanceof DuplicateKeyException) {
            //主键冲突
            resp.setCode(CODE_601.getCode());
            resp.setMessage(CODE_601.getMsg());
        } else {
            resp.setCode(CODE_500.getCode());
            resp.setMessage(e.getMessage());
        }
        return resp;
    }

}
