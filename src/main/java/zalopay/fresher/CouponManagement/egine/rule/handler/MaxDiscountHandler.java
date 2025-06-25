package zalopay.fresher.CouponManagement.egine.rule.handler;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;
import zalopay.fresher.CouponManagement.model.OrderContext;
import zalopay.fresher.CouponManagement.egine.rule.KindRule;
import zalopay.fresher.CouponManagement.egine.rule.RuleHandler;
import zalopay.fresher.CouponManagement.egine.rule.RuleResult;
import zalopay.fresher.CouponManagement.egine.rule.TypeRule;

@Component
public class MaxDiscountHandler implements RuleHandler {
    @Override
    public TypeRule getType() {
        return TypeRule.MAX_DISCOUNT;
    }

    @Override
    public KindRule getKindRule() {
        return KindRule.ADJUSTMENT;
    }

    @Override
    public RuleResult validate(JsonNode config, OrderContext context) {
        RuleResult result = new RuleResult();
        if (config == null || !config.has("maxDiscountAmout")) {
            result.setValid(false);
            result.setMessage("Max discount rule configuration is missing.");
            return result;
        }
        double maxDiscount = config.get("maxDiscountAmout").asDouble();
        if (maxDiscount < 0) {
            result.setValid(false);
            result.setMessage("Max discount cannot be negative.");
        } else {
            result.setValid(true);
            result.setMessage("Max discount rule is valid.");
        }
        return result;
    }

    @Override
    public Double adjust(JsonNode config, OrderContext context, Double baseDiscount) {
        if (config == null || !config.has("maxDiscountAmount")) {
            return baseDiscount;
        }

        Double maxDiscount = config.get("maxDiscountAmount").asDouble();
        if (baseDiscount > maxDiscount && maxDiscount >= 0) {
            return maxDiscount;
        }
        return baseDiscount;
    }
}
