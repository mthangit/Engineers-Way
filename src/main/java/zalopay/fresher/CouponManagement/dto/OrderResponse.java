package zalopay.fresher.CouponManagement.dto;

import lombok.Data;

import java.util.List;

@Data
public class OrderResponse {
    private List<CouponResponse> coupons;
    private double totalAmount;
    private double discountAmount;
    private double finalAmount;
    private String orderDate;
    private String message;
}
