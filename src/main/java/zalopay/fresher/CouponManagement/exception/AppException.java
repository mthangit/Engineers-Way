package zalopay.fresher.CouponManagement.exception;

import lombok.Getter;
import lombok.Setter;
import zalopay.fresher.CouponManagement.dto.ResponseCode;

@Setter
@Getter
public class AppException extends RuntimeException{
    private ResponseCode responseCode;

    public AppException(ResponseCode responseCode) {
        super(responseCode.getMessage());
        this.responseCode = responseCode;
    }

    public AppException(ResponseCode responseCode, String message) {
        super(message);
        this.responseCode = responseCode;
    }

}
