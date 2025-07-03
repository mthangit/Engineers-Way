package zalopay.fresher.CouponManagement.engine.processor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import zalopay.fresher.CouponManagement.engine.model.CouponApplyResponse;
import zalopay.fresher.CouponManagement.model.OrderContext;
import zalopay.fresher.CouponManagement.engine.rule.RuleHandler;
import zalopay.fresher.CouponManagement.engine.rule.RuleResult;
import zalopay.fresher.CouponManagement.engine.rule.RuleType;
import zalopay.fresher.CouponManagement.model.Rule;
import zalopay.fresher.CouponManagement.util.GlobalConfig;
import zalopay.fresher.CouponManagement.util.ErrorMessages;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
public class RuleProcessor {
    Map<RuleType, RuleHandler> ruleHandlerMap;
    ObjectMapper objectMapper;

    public CouponApplyResponse processRules(List<Rule> rules, OrderContext context, Double baseDiscount) {
        CouponApplyResponse response = new CouponApplyResponse();
        response.setCouponCode(context.getCouponCode());
        response.setRules(rules);
        List<RuleResult> ruleResults = new ArrayList<>();

        if (rules == null || rules.isEmpty()) {
            return buildNoRuleCouponResponse(response, baseDiscount, ruleResults);
        }

        Double finalDiscount = processAllRules(rules, context, baseDiscount, ruleResults);

        if (finalDiscount == null) {
            return buildFailedQualificationCouponResponse(response, ruleResults);
        }

        return buildSuccessRuleCouponResponse(response, finalDiscount, ruleResults);
    }

    private CouponApplyResponse buildNoRuleCouponResponse(CouponApplyResponse response, Double baseDiscount, List<RuleResult> ruleResults) {
        response.setValid(true);
        response.setMessage(String.format(ErrorMessages.COUPON_APPLIED_NO_RULES, baseDiscount));
        response.setRuleResults(ruleResults);
        response.setDiscountAmount(baseDiscount);
        return response;
    }

    private CouponApplyResponse buildFailedQualificationCouponResponse(CouponApplyResponse response, List<RuleResult> ruleResults) {
        response.setValid(false);
        response.setMessage(ErrorMessages.ORDER_NOT_QUALIFY);
        response.setRuleResults(ruleResults);
        response.setDiscountAmount(0.0);
        return response;
    }

    private CouponApplyResponse buildSuccessRuleCouponResponse(CouponApplyResponse response, Double finalDiscount, List<RuleResult> ruleResults) {
        response.setValid(true);
        response.setMessage(String.format(ErrorMessages.COUPON_APPLIED_WITH_RULES, finalDiscount));
        response.setRuleResults(ruleResults);
        response.setDiscountAmount(finalDiscount);
        return response;
    }

    private RuleResult buildErrorRuleResult(String message, RuleType ruleType) {
        RuleResult errorResult = new RuleResult();
        errorResult.setPassed(false);
        errorResult.setEvaluationMessage(message);
        errorResult.setRuleType(ruleType);
        return errorResult;
    }

    private Double processAllRules(List<Rule> rules, OrderContext context, Double baseDiscount, List<RuleResult> ruleResults) {
        Double currentDiscount = baseDiscount;
        context.setBaseDiscountAmount(currentDiscount);

        for (Rule rule : rules) {
            RuleHandler handler = getRuleHandler(rule, ruleResults);
            if (handler == null) {
                return null;
            }

            RuleResult result = processSingleRule(rule, handler, context, ruleResults);
            if (result == null) {
                return null;
            }

            if (!result.isPassed()) {
                return null;
            }

            currentDiscount = updateDiscountAmount(result, currentDiscount, context);
        }

        return Math.max(GlobalConfig.MIN_DISCOUNT_AMOUNT, currentDiscount);
    }

    private RuleHandler getRuleHandler(Rule rule, List<RuleResult> ruleResults) {
        RuleHandler handler = ruleHandlerMap.get(rule.getType());
        if (handler == null) {
            ruleResults.add(buildErrorRuleResult(
                "No handler found for rule type: " + rule.getType(), 
                rule.getType()
            ));
        }
        return handler;
    }

    private RuleResult processSingleRule(Rule rule, RuleHandler handler, OrderContext context, List<RuleResult> ruleResults) {
        try {
            JsonNode config = parseRuleConfig(rule);
            RuleResult result = handler.applyRule(config, context);
            
            if (result != null) {
                ruleResults.add(result);
                return result;
            }
            
            ruleResults.add(buildErrorRuleResult(
                "Rule handler returned null result", 
                rule.getType()
            ));
            return null;
            
        } catch (JsonProcessingException e) {
            ruleResults.add(buildErrorRuleResult(
                "Invalid rule configuration: %s".formatted(e.getMessage()), 
                rule.getType()
            ));
            return null;
        } catch (Exception e) {
            ruleResults.add(buildErrorRuleResult(
                "Error processing rule: %s".formatted(e.getMessage()), 
                rule.getType()
            ));
            return null;
        }
    }

    private JsonNode parseRuleConfig(Rule rule) throws JsonProcessingException {
        return objectMapper.readTree(rule.getConfig());
    }

    private Double updateDiscountAmount(RuleResult result, Double currentDiscount, OrderContext context) {
        Double adjustedAmount = result.getAdjustDiscountAmount();
        
        if (hasDiscountAdjustment(adjustedAmount, currentDiscount)) {
            context.setBaseDiscountAmount(adjustedAmount);
            return adjustedAmount;
        }
        
        return currentDiscount;
    }

    private boolean hasDiscountAdjustment(Double adjustedAmount, Double currentDiscount) {
        return adjustedAmount != null 
            && !adjustedAmount.equals(GlobalConfig.MIN_DISCOUNT_AMOUNT) 
            && !adjustedAmount.equals(currentDiscount);
    }
}
