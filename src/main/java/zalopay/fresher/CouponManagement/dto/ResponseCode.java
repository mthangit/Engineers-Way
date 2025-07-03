package zalopay.fresher.CouponManagement.dto;

import lombok.Getter;

@Getter
public enum ResponseCode {
    COUPON_NOT_FOUND(404, "Coupon not found"),
    RESPONSE_OK(200, "Success"),
    INVALID_INPUT(400, "Invalid input"),
    ;

    private final int code;
    private final String message;

    ResponseCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
