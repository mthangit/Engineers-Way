package zalopay.fresher.CouponManagement.egine.model;

import lombok.Data;
import zalopay.fresher.CouponManagement.egine.rule.RuleResult;
import zalopay.fresher.CouponManagement.model.Rule;

import java.util.List;

@Data
public class CouponApplyResponse {
    private String couponCode;
    private List<Rule> rules;
    private List<RuleResult> ruleResults;
    private String message;
    private boolean isValid;
    private Double discountAmount;
}
