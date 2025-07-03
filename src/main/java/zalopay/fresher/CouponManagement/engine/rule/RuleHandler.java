package zalopay.fresher.CouponManagement.engine.rule;

import com.fasterxml.jackson.databind.JsonNode;
import zalopay.fresher.CouponManagement.model.OrderContext;

public interface RuleHandler {
    
    default RuleType getType() {
        ForRule annotation = this.getClass().getAnnotation(ForRule.class);
        if (annotation != null) {
            return annotation.value();
        }
        throw new IllegalStateException("RuleHandler must have @HandlesRule annotation: " + this.getClass().getSimpleName());
    }

    default RuleResult createResult(boolean isValid, String message, double discountAmount) {
        RuleResult result = new RuleResult();
        result.setPassed(isValid);
        result.setEvaluationMessage(message);
        result.setAdjustDiscountAmount(discountAmount);
        result.setRuleType(getType());
        return result;
    }

    RuleResult applyRule(JsonNode config, OrderContext context);
}
