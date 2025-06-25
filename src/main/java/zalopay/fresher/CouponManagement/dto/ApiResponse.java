package zalopay.fresher.CouponManagement.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse {
    private String message;
    private int code;
    private Object data;

    public ApiResponse(ResponseCode responseCode, Object data) {
        this.message = responseCode.getMessage();
        this.code = responseCode.getCode();
        this.data = data;
    }

}
