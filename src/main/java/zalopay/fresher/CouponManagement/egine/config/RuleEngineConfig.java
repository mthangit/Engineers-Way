package zalopay.fresher.CouponManagement.egine.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import zalopay.fresher.CouponManagement.egine.processor.RuleProcessor;
import zalopay.fresher.CouponManagement.egine.rule.RuleHandler;
import zalopay.fresher.CouponManagement.egine.rule.TypeRule;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
public class RuleEngineConfig {
    @Bean
    public Map<TypeRule, RuleHandler> ruleHandlerMap(List<RuleHandler> ruleHandlers) {
        return ruleHandlers.stream()
                .collect(Collectors.toMap(RuleHandler::getType, handler -> handler));
    }

    @Bean
    public RuleProcessor ruleProcessor(Map<TypeRule, RuleHandler> ruleHandlerMap, ObjectMapper objectMapper) {
        RuleProcessor ruleProcessor = new RuleProcessor();
        ruleProcessor.setRuleHandlerMap(ruleHandlerMap);
        ruleProcessor.setObjectMapper(objectMapper);
        return ruleProcessor;
    }
}
