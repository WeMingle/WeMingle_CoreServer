package com.wemingle.core.global.advice.vaildation;

import com.wemingle.core.domain.nickname.controller.NicknameController;
import com.wemingle.core.domain.user.controller.MemberController;
import com.wemingle.core.global.responseform.ResponseHandler;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = {NicknameController.class, MemberController.class})
public class ValidExceptionHandler {
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ResponseHandler<String>> ConstraintViolationException(Exception e) {
        return ResponseEntity.badRequest().body(
                ResponseHandler.<String>builder()
                        .responseMessage("Error")
                        .responseData(e.getMessage())
                        .build()
        );
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ResponseHandler<String>> UnavailableNicknameException(Exception e) {
        return ResponseEntity.badRequest().body(
                ResponseHandler.<String>builder()
                        .responseMessage("Unavailable nickname")
                        .responseData(e.getMessage())
                        .build()
        );
    }

}
