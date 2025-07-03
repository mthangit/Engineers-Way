package zalopay.fresher.CouponManagement.engine.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import zalopay.fresher.CouponManagement.engine.processor.RuleProcessor;
import zalopay.fresher.CouponManagement.engine.rule.RuleHandler;
import zalopay.fresher.CouponManagement.engine.rule.RuleType;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
public class RuleEngineConfig {
    @Bean
    public Map<RuleType, RuleHandler> ruleHandlerMap(List<RuleHandler> ruleHandlers) {
        return ruleHandlers.stream()
                .collect(Collectors.toMap(RuleHandler::getType, handler -> handler));
    }

    @Bean
    public RuleProcessor ruleProcessor(Map<RuleType, RuleHandler> ruleHandlerMap, ObjectMapper objectMapper) {
        RuleProcessor ruleProcessor = new RuleProcessor();
        ruleProcessor.setRuleHandlerMap(ruleHandlerMap);
        ruleProcessor.setObjectMapper(objectMapper);
        return ruleProcessor;
    }
}
