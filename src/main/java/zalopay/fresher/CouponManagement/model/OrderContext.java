package zalopay.fresher.CouponManagement.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderContext {
    Double orderAmount;
    String couponCode;
    LocalDateTime orderDate;
    Double baseDiscountAmount;

}
