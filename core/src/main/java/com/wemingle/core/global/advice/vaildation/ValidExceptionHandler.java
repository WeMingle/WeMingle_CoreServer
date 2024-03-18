package com.wemingle.core.global.advice.vaildation;

import com.wemingle.core.domain.nickname.controller.NicknameController;
import com.wemingle.core.domain.member.controller.MemberController;
import com.wemingle.core.global.exception.CannotSaveImgException;
import com.wemingle.core.global.exception.UnsupportedFileExtensionException;
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

    @ExceptionHandler(UnsupportedFileExtensionException.class)
    public ResponseEntity<ResponseHandler<String>> unSupportedFileExtensionException(Exception e){
        return ResponseEntity.badRequest().body(
                ResponseHandler.<String>builder()
                        .responseMessage("Unsupported file extension")
                        .responseData(e.getMessage())
                        .build()
        );
    }

    @ExceptionHandler(CannotSaveImgException.class)
    public ResponseEntity<ResponseHandler<String>> cannotSaveImgException(Exception e){
        return ResponseEntity.badRequest().body(
                ResponseHandler.<String>builder()
                        .responseMessage("Unable to save the Image")
                        .responseData(e.getMessage())
                        .build()
        );
    }

}
