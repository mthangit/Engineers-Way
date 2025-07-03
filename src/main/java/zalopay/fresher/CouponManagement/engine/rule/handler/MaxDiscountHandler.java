package zalopay.fresher.CouponManagement.engine.rule.handler;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;
import zalopay.fresher.CouponManagement.model.OrderContext;
import zalopay.fresher.CouponManagement.engine.rule.RuleHandler;
import zalopay.fresher.CouponManagement.engine.rule.RuleResult;
import zalopay.fresher.CouponManagement.engine.rule.ForRule;
import zalopay.fresher.CouponManagement.engine.rule.RuleType;
import zalopay.fresher.CouponManagement.util.GlobalConfig;
import zalopay.fresher.CouponManagement.util.ErrorMessages;

@Component
@ForRule(RuleType.MAX_DISCOUNT)
public class MaxDiscountHandler implements RuleHandler {

    private boolean isValidConfig(JsonNode config) {
        if (config == null || !config.has(GlobalConfig.MAX_DISCOUNT_CONFIG_KEY)) {
            return false;
        }
        double maxDiscount = config.get(GlobalConfig.MAX_DISCOUNT_CONFIG_KEY).asDouble();
        return maxDiscount >= GlobalConfig.MIN_VALID_AMOUNT && !Double.isInfinite(maxDiscount);
    }

    public Double adjust(Double baseDiscount, Double maxDiscount) {
        return Math.min(baseDiscount, maxDiscount);
    }

    @Override
    public RuleResult applyRule(JsonNode config, OrderContext context) {
        if (!isValidConfig(config)) {
            return createResult(false, ErrorMessages.MAX_DISCOUNT_INVALID_CONFIG, GlobalConfig.NO_DISCOUNT_ADJUSTMENT);
        }

        try {
            double maxDiscountAmount = config.get(GlobalConfig.MAX_DISCOUNT_CONFIG_KEY).asDouble();
            double baseDiscountAmount = context.getBaseDiscountAmount();
            
            if (baseDiscountAmount > maxDiscountAmount) {
                String adjustmentMessage = String.format(ErrorMessages.MAX_DISCOUNT_ADJUSTMENT, 
                                                       baseDiscountAmount, maxDiscountAmount);
                return createResult(true, adjustmentMessage, maxDiscountAmount);
            }
            
            return createResult(true, ErrorMessages.MAX_DISCOUNT_ADJUSTMENT, GlobalConfig.NO_DISCOUNT_ADJUSTMENT);
        } catch (Exception e) {
            return createResult(false, ErrorMessages.MAX_DISCOUNT_INVALID_CONFIG, GlobalConfig.NO_DISCOUNT_ADJUSTMENT);
        }
    }
}
