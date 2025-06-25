package zalopay.fresher.CouponManagement.dto;

import lombok.Getter;

@Getter
public enum ResponseCode {
    COUPON_NOT_FOUND(404, "Coupon not found"),
    RESPONSE_OK(200, "Success"),
    CREATE_SUCCESS(201, "Create successfully"),
    SAVE_SUCCESS(200, "Save successfully"),
    INVALID_COUPON_CODE(400, "Invalid coupon code"),
    INTERNAL_SERVER_ERROR(500, "Internal server error"),
    ;

    private final int code;
    private final String message;

    ResponseCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
