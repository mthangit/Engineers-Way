package zalopay.fresher.CouponManagement.egine.processor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import zalopay.fresher.CouponManagement.egine.model.CouponApplyResponse;
import zalopay.fresher.CouponManagement.model.OrderContext;
import zalopay.fresher.CouponManagement.egine.rule.KindRule;
import zalopay.fresher.CouponManagement.egine.rule.RuleHandler;
import zalopay.fresher.CouponManagement.egine.rule.RuleResult;
import zalopay.fresher.CouponManagement.egine.rule.TypeRule;
import zalopay.fresher.CouponManagement.model.Rule;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
public class RuleProcessor {
    Map<TypeRule, RuleHandler> ruleHandlerMap;
    ObjectMapper objectMapper;

    public CouponApplyResponse processRules(List<Rule> rules, OrderContext context, Double baseDiscount) {
        CouponApplyResponse response = new CouponApplyResponse();
        response.setCouponCode(context.getCouponCode());
        response.setRules(rules);
        
        List<RuleResult> ruleResults = new ArrayList<>();
        
        if (rules == null || rules.isEmpty()) {
            response.setValid(true);
            response.setMessage("Coupon applied successfully with no additional rules. Discount: " + baseDiscount);
            response.setRuleResults(ruleResults);
            response.setDiscountAmount(baseDiscount);
            return response;
        }

        boolean qualificationPassed = validateQualificationWithResults(rules, context, ruleResults);
        if (!qualificationPassed) {
            response.setValid(false);
            response.setMessage("Order does not meet coupon qualification requirements. Discount: 0");
            response.setRuleResults(ruleResults);
            response.setDiscountAmount(0.0);
            return response;
        }

        Double finalDiscount = applyAdjustmentWithResults(rules, context, baseDiscount, ruleResults);
        
        response.setValid(true);
        response.setMessage("Coupon applied successfully. Final discount: " + finalDiscount);
        response.setRuleResults(ruleResults);
        response.setDiscountAmount(finalDiscount);
        
        return response;
    }

    private boolean validateQualificationWithResults(List<Rule> rules, OrderContext context, List<RuleResult> ruleResults) {
        boolean allPassed = true;
        
        for (Rule rule : rules) {
            RuleHandler handler = ruleHandlerMap.get(rule.getType());
            if (handler == null || handler.getKindRule() != KindRule.QUALIFICATION) {
                continue;
            }
            
            try {
                JsonNode config = objectMapper.readTree(rule.getConfig());
                RuleResult result = handler.validate(config, context);
                if (result != null) {
                    result.setKindRule(KindRule.QUALIFICATION);
                    result.setTypeRule(rule.getType());
                    ruleResults.add(result);
                    
                    if (!result.isValid()) {
                        allPassed = false;
                    }
                }
            } catch (JsonProcessingException e) {
                RuleResult errorResult = new RuleResult();
                errorResult.setValid(false);
                errorResult.setMessage("Invalid rule configuration: " + e.getMessage());
                errorResult.setKindRule(KindRule.QUALIFICATION);
                errorResult.setTypeRule(rule.getType());
                ruleResults.add(errorResult);
                allPassed = false;
            }
        }
        return allPassed;
    }

    private Double applyAdjustmentWithResults(List<Rule> rules, OrderContext context, Double baseDiscount, List<RuleResult> ruleResults) {
        Double adjustedDiscount = baseDiscount;
        
        for (Rule rule : rules) {
            RuleHandler handler = ruleHandlerMap.get(rule.getType());
            if (handler == null || handler.getKindRule() != KindRule.ADJUSTMENT) {
                continue;
            }
            
            try {
                JsonNode config = objectMapper.readTree(rule.getConfig());
                Double previousDiscount = adjustedDiscount;
                adjustedDiscount = handler.adjust(config, context, adjustedDiscount);
                
                RuleResult result = new RuleResult();
                result.setValid(true);
                result.setMessage("Adjusted discount from " + previousDiscount + " to " + adjustedDiscount);
                result.setKindRule(KindRule.ADJUSTMENT);
                result.setTypeRule(rule.getType());
                ruleResults.add(result);
                
            } catch (JsonProcessingException e) {
                RuleResult errorResult = new RuleResult();
                errorResult.setValid(false);
                errorResult.setMessage("Invalid rule configuration: " + e.getMessage());
                errorResult.setKindRule(KindRule.ADJUSTMENT);
                errorResult.setTypeRule(rule.getType());
                ruleResults.add(errorResult);
            }
        }
        
        return Math.max(0.0, adjustedDiscount);
    }
}

