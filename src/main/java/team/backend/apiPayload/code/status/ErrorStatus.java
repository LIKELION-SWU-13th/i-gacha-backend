package team.backend.apiPayload.code.status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import team.backend.apiPayload.code.BaseErrorCode;
import team.backend.apiPayload.code.ErrorReasonDTO;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode {
    // 가장 일반적인 응답
    _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
    _BAD_REQUEST(HttpStatus.BAD_REQUEST,"COMMON400","잘못된 요청입니다."),
    _UNAUTHORIZED(HttpStatus.UNAUTHORIZED,"COMMON401","인증이 필요합니다."),
    _FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "금지된 요청입니다."),

    // 이벤트 오류
    _USER_NOT_CREATE_EVENT(HttpStatus.BAD_REQUEST, "EVENT401", "해당 사용자는 해당 이벤트를 생성하지 않았습니다."),
    _USER_EVENT_WISH(HttpStatus.BAD_REQUEST, "EVENT402", "사용자 ID, 이벤트 ID, 위시 ID 설정 오류"),
    _EVENT_NOT_FOUND(HttpStatus.BAD_REQUEST, "EVENT403", "해당 이벤트가 존재하지 않습니다."),
    _EVENT_WISH_NOT_EXIST(HttpStatus.BAD_REQUEST, "EVENT404", "해당 이벤트에 위시는 존재하지 않습니다."),

    _WISH_NOT_FOUND(HttpStatus.BAD_REQUEST, "WISH401", "해당 wish 상품은 존재하지 않습니다."),

    _LINK_ERROR(HttpStatus.BAD_REQUEST, "LINK401", "올바른 link를 던져주세요"),

    _CRAWLING_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "CRAW400", "크롤링 에러"),

    _INVALID_URL_FORMAT(HttpStatus.BAD_REQUEST, "URL400", "링크 형식이 잘못되었습니다. 올바른 링크를 추가했는지 확인해 주세요.")
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ErrorReasonDTO getReason() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .build();
    }

    @Override
    public ErrorReasonDTO getReasonHttpStatus() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .httpStatus(httpStatus)
                .build();
    }

}
