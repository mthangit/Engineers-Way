package zalopay.fresher.CouponManagement.egine.rule;

import com.fasterxml.jackson.databind.JsonNode;
import zalopay.fresher.CouponManagement.model.OrderContext;

public interface RuleHandler {
    TypeRule getType();
    KindRule getKindRule();

    default RuleResult validate(JsonNode config, OrderContext context) {
        return new RuleResult();
    }

    default Double adjust(JsonNode config, OrderContext context, Double baseDiscount) {
        return baseDiscount;
    }
}
