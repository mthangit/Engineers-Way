package zalopay.fresher.CouponManagement.egine.rule.handler;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;
import zalopay.fresher.CouponManagement.model.OrderContext;
import zalopay.fresher.CouponManagement.egine.rule.KindRule;
import zalopay.fresher.CouponManagement.egine.rule.RuleHandler;
import zalopay.fresher.CouponManagement.egine.rule.RuleResult;
import zalopay.fresher.CouponManagement.egine.rule.TypeRule;

@Component
public class MinOrderHandler implements RuleHandler {

    @Override
    public TypeRule getType() {
        return TypeRule.MIN_ORDER;
    }

    @Override
    public KindRule getKindRule() {
        return KindRule.QUALIFICATION;
    }

    @Override
    public RuleResult validate(JsonNode config, OrderContext context) {
        RuleResult result = new RuleResult();
        if (config == null || !config.has("minOrderAmount")) {
            result.setValid(false);
            return result;
        }
        Double minOrder = config.get("minOrderAmount").asDouble();
        Double orderAmount = context.getOrderAmount();
        result.setValid(orderAmount != null && orderAmount.compareTo(minOrder) >= 0);
        if (result.isValid()) {
            result.setMessage("Order amount is valid for minimum order rule.");
        } else {
            result.setMessage("Order amount does not meet the minimum order requirement.");
        }
        return result;
    }
}
