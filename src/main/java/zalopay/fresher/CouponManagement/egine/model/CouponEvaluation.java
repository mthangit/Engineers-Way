package zalopay.fresher.CouponManagement.egine.model;

import lombok.Data;
import lombok.Getter;
import zalopay.fresher.CouponManagement.model.Coupon;

@Getter
public class CouponEvaluation {
    private final Coupon coupon;
    private final CouponApplyResponse applyResponse;
    private final double discountAmount;
    
    public CouponEvaluation(Coupon coupon, CouponApplyResponse applyResponse) {
        this.coupon = coupon;
        this.applyResponse = applyResponse;
        this.discountAmount = applyResponse.isValid() && applyResponse.getDiscountAmount() != null ?
            applyResponse.getDiscountAmount() : 0.0;
    }

    public boolean isValid() {
        return applyResponse.isValid();
    }
}