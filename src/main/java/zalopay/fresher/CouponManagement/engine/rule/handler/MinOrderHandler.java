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
@ForRule(RuleType.MIN_ORDER)
public class MinOrderHandler implements RuleHandler {

    private boolean isValidConfig(JsonNode config) {
        if (config == null || !config.has(GlobalConfig.MIN_ORDER_CONFIG_KEY)) {
            return false;
        }
        double minOrder = config.get(GlobalConfig.MIN_ORDER_CONFIG_KEY).asDouble();
        return minOrder >= GlobalConfig.MIN_VALID_AMOUNT && !Double.isInfinite(minOrder);
    }

    private Double getMinOrderAmount(JsonNode config) {
        return config.get(GlobalConfig.MIN_ORDER_CONFIG_KEY).asDouble();
    }

    private Double getOrderAmount(OrderContext context) {
        return context.getOrderAmount();
    }

    private boolean isOrderAmountValid(Double orderAmount, Double minOrder) {
        return orderAmount != null && orderAmount.compareTo(minOrder) >= GlobalConfig.EQUAL_COMPARISON;
    }

    private boolean isValidMinOrder(JsonNode config, OrderContext context) {
        Double minOrder = getMinOrderAmount(config);
        Double orderAmount = getOrderAmount(context);
        return isOrderAmountValid(orderAmount, minOrder);
    }

    @Override
    public RuleResult applyRule(JsonNode config, OrderContext context) {
        if (!isValidConfig(config)) {
            return createResult(false, ErrorMessages.MIN_ORDER_INVALID_CONFIG, GlobalConfig.NO_DISCOUNT_ADJUSTMENT);
        }

        try {
            double minOrderAmount = config.get(GlobalConfig.MIN_ORDER_CONFIG_KEY).asDouble();
            double orderTotalAmount = context.getOrderAmount();
            
            if (orderTotalAmount >= minOrderAmount) {
                return createResult(true, ErrorMessages.ORDER_MEET_REQUIREMENT, GlobalConfig.NO_DISCOUNT_ADJUSTMENT);
            }
            
            return createResult(false, ErrorMessages.ORDER_NOT_MEET_REQUIREMENT, GlobalConfig.NO_DISCOUNT_ADJUSTMENT);
        } catch (Exception e) {
            return createResult(false, ErrorMessages.MIN_ORDER_INVALID_CONFIG, GlobalConfig.NO_DISCOUNT_ADJUSTMENT);
        }
    }
}
