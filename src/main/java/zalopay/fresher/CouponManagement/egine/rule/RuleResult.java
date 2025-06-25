package zalopay.fresher.CouponManagement.egine.rule;

import lombok.Data;

@Data
public class RuleResult {
    private boolean isValid;
    private String message;
    private KindRule kindRule;
    private TypeRule typeRule;
}
