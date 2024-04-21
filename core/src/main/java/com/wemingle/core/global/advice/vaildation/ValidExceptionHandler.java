package com.wemingle.core.global.advice.vaildation;

import com.wemingle.core.domain.matching.controller.MatchingController;
import com.wemingle.core.domain.member.controller.MemberController;
import com.wemingle.core.domain.nickname.controller.NicknameController;
import com.wemingle.core.domain.post.controller.MatchingPostController;
import com.wemingle.core.global.responseform.ResponseHandler;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = {NicknameController.class, MemberController.class, MatchingController.class, MatchingPostController.class})
public class ValidExceptionHandler {

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ResponseHandler<String>> HttpMessageNotReadableException(Exception e) {
        return ResponseEntity.badRequest().body(
                ResponseHandler.<String>builder()
                        .responseMessage("JSON parse error")
                        .responseData(e.getMessage())
                        .build());
    }

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

//    @ExceptionHandler(RuntimeException.class)
//    public ResponseEntity<ResponseHandler<String>> RuntimeException(Exception e) {
//        return ResponseEntity.badRequest().body(
//                ResponseHandler.<String>builder()
//                        .responseMessage("RuntimeException")
//                        .responseData(e.getMessage())
//                        .build()
//        );
//    }
}
