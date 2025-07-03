package zalopay.fresher.CouponManagement.engine.model;

import lombok.Getter;
import zalopay.fresher.CouponManagement.model.Coupon;
import zalopay.fresher.CouponManagement.util.GlobalConfig;

@Getter
public class CouponEvaluation {
    private final Coupon coupon;
    private final CouponApplyResponse applyResponse;
    private final double discountAmount;
    
    public CouponEvaluation(Coupon coupon, CouponApplyResponse applyResponse) {
        this.coupon = coupon;
        this.applyResponse = applyResponse;
        this.discountAmount = applyResponse.isValid() && applyResponse.getDiscountAmount() != null ?
            applyResponse.getDiscountAmount() : GlobalConfig.MIN_DISCOUNT_AMOUNT;
    }

    public boolean isValid() {
        return applyResponse.isValid();
    }
}