//package com.wemingle.core.global.advice.vaildation;
//
//import com.wemingle.core.global.exception.NotManagerException;
//import com.wemingle.core.global.exception.NotWriterException;
//import com.wemingle.core.global.exception.WriterNotAllowedException;
//import com.wemingle.core.global.responseform.ResponseHandler;
//import jakarta.persistence.EntityNotFoundException;
//import jakarta.validation.ConstraintViolationException;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.http.converter.HttpMessageNotReadableException;
//import org.springframework.web.bind.MethodArgumentNotValidException;
//import org.springframework.web.bind.MissingServletRequestParameterException;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.RestControllerAdvice;
//import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
//
////@RestControllerAdvice(assignableTypes = {NicknameController.class, MemberController.class, MatchingController.class, MatchingPostController.class})
//@RestControllerAdvice
//public class ValidExceptionHandler {
//
//    @ExceptionHandler(HttpMessageNotReadableException.class)
//    public ResponseEntity<ResponseHandler<String>> HttpMessageNotReadableException(Exception e) {
//        return ResponseEntity.badRequest().body(
//                ResponseHandler.<String>builder()
//                        .responseMessage("JSON parse error")
//                        .responseData(e.getMessage())
//                        .build());
//    }
//
//    @ExceptionHandler(ConstraintViolationException.class)
//    public ResponseEntity<ResponseHandler<String>> ConstraintViolationException(Exception e) {
//        return ResponseEntity.badRequest().body(
//                ResponseHandler.<String>builder()
//                        .responseMessage("Error")
//                        .responseData(e.getMessage())
//                        .build()
//        );
//    }
//
//    @ExceptionHandler(IllegalStateException.class)
//    public ResponseEntity<ResponseHandler<String>> UnavailableNicknameException(Exception e) {
//        return ResponseEntity.badRequest().body(
//                ResponseHandler.<String>builder()
//                        .responseMessage("Unavailable nickname")
//                        .responseData(e.getMessage())
//                        .build()
//        );
//    }
//
//    @ExceptionHandler(RuntimeException.class)
//    public ResponseEntity<ResponseHandler<String>> RuntimeException(Exception e) {
//        return ResponseEntity.badRequest().body(
//                ResponseHandler.<String>builder()
//                        .responseMessage("RuntimeException")
//                        .responseData(e.getMessage())
//                        .build()
//        );
//    }
//
//    @ExceptionHandler(EntityNotFoundException.class)
//    public ResponseEntity<ResponseHandler<String>> EntityNotFoundException(Exception e) {
//        return ResponseEntity.badRequest().body(
//                ResponseHandler.<String>builder()
//                        .responseMessage("EntityNotFoundException")
//                        .responseData(e.getMessage())
//                        .build()
//        );
//    }
//
//    @ExceptionHandler(MissingServletRequestParameterException.class)
//    public ResponseEntity<ResponseHandler<String>> MissingServletRequestParameterException(Exception e) {
//        return ResponseEntity.badRequest().body(
//                ResponseHandler.<String>builder()
//                        .responseMessage("MissingServletRequestParameterException")
//                        .responseData(e.getMessage())
//                        .build()
//        );
//    }
//
//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public ResponseEntity<ResponseHandler<String>> MethodArgumentNotValidException(Exception e){
//        return ResponseEntity.badRequest().body(
//                ResponseHandler.<String>builder()
//                        .responseMessage("MethodArgumentNotValidException")
//                        .responseData(e.getMessage())
//                        .build()
//        );
//    }
//
//    @ExceptionHandler(NoSuchKeyException.class)
//    public ResponseEntity<ResponseHandler<String>> NoSuchKeyException(Exception e){
//        return ResponseEntity.badRequest().body(
//                ResponseHandler.<String>builder()
//                        .responseMessage("imgs don't exist in s3")
//                        .responseData(e.getMessage())
//                        .build()
//        );
//    }
//
//    @ExceptionHandler(NotWriterException.class)
//    public ResponseEntity<ResponseHandler<String>> NotWriterException(Exception e) {
//        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
//                ResponseHandler.<String>builder()
//                        .responseMessage("NotWriterException")
//                        .responseData(e.getMessage())
//                        .build()
//        );
//    }
//
//    @ExceptionHandler(NotManagerException.class)
//    public ResponseEntity<ResponseHandler<String>> NotManagerException(Exception e) {
//        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
//                ResponseHandler.<String>builder()
//                        .responseMessage("NotManagerException")
//                        .responseData(e.getMessage())
//                        .build()
//        );
//    }
//
//    @ExceptionHandler(WriterNotAllowedException.class)
//    public ResponseEntity<ResponseHandler<String>> WriterNotAllowedException(Exception e) {
//        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
//                ResponseHandler.<String>builder()
//                        .responseMessage("WriterNotAllowedException")
//                        .responseData(e.getMessage())
//                        .build()
//        );
//    }
//}
