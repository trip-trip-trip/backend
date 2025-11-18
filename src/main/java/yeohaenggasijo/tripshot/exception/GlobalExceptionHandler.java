package yeohaenggasijo.tripshot.exception;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import yeohaenggasijo.tripshot.dto.ApiResponse;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /** 비즈니스/도메인 예외 */
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiResponse<Void>> handleApi(ApiException ex) {
        HttpStatus st = ex.getStatus();
        // 4xx는 warn, 5xx는 error 로깅 권장
        if (st.is5xxServerError()) log.error("[API-EX] {}", ex.getMessage(), ex);
        else log.warn("[API-EX] {}", ex.getMessage());
        return ResponseEntity.status(st)
                .body(ApiResponse.error(ex.getCode(), ex.getMessage()));
    }

    /** @Valid 바디 검증 실패 (필드별 오류 반환) */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleInvalidBody(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(fe -> errors.put(fe.getField(), fe.getDefaultMessage()));
        return ResponseEntity.badRequest()
                .body(ApiResponse.of(false, 400, "Validation failed", errors));
    }

//    /** @Validated 파라미터/경로변수 검증 실패 */
//    @ExceptionHandler(ConstraintViolationException.class)
//    public ResponseEntity<ApiResponse<List<String>>> handleConstraint(ConstraintViolationException ex) {
//        List<String> msgs = ex.getConstraintViolations().stream()
//                .map(v -> v.getPropertyPath() + ": " + v.getMessage()).toList();
//        return ResponseEntity.badRequest()
//                .body(ApiResponse.of(false, 400, "Constraint violation", msgs));
//    }
    /** 서비스/도메인 단에서 던지는 IllegalArgumentException → 400으로 내려주기 */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegal(IllegalArgumentException ex) {
        log.warn("[BAD-REQUEST] {}", ex.getMessage());
        return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, ex.getMessage()));
    }

    /** DB 제약조건 위반 (unique 등) 처리 — 디버깅용으로 constraint 이름까지 내려줌 */
    @ExceptionHandler({ConstraintViolationException.class, DataIntegrityViolationException.class})
    public ResponseEntity<ApiResponse<Void>> handleDbConstraint(Exception ex) {
        String message;
        if (ex instanceof ConstraintViolationException cve) {
            message = "Database constraint violation: " + cve.getConstraintName();
        } else {
            message = "Database constraint violation: " + ex.getMessage();
        }
        log.error("[DB-CONSTRAINT] {}", message, ex);
        // 보통 409 Conflict 또는 400 중 택1 — 여기서는 409 사용
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponse.error(409, message));
    }


    /** 타입 불일치, JSON 파싱 문제 등 일반적인 400 */
    @ExceptionHandler({
            MethodArgumentTypeMismatchException.class,
            org.springframework.http.converter.HttpMessageNotReadableException.class
    })
    public ResponseEntity<ApiResponse<Void>> handleBadRequest(Exception ex) {
        return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, "Bad request: " + ex.getMessage()));
    }

    /** 404 (미매핑 경로) — 아래 yml 설정 필요(하단 참고) */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFound(NoHandlerFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(404, "No handler for " + ex.getRequestURL()));
    }

    /** 최종 방어망 */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleUnknown(Exception ex) {
        log.error("[UNEXPECTED] {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(
                        500,
                        "[UNEXPECTED] " + ex.getClass().getSimpleName() + ": " + ex.getMessage()
                ));
    }




}