package zalopay.fresher.CouponManagement.engine.rule;

import lombok.Data;

@Data
public class RuleResult {
    private boolean isPassed;
    private String evaluationMessage;
    private RuleType ruleType;
    private double adjustDiscountAmount;
}
