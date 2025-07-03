package zalopay.fresher.CouponManagement.engine.rule.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import zalopay.fresher.CouponManagement.engine.rule.RuleResult;
import zalopay.fresher.CouponManagement.model.OrderContext;

import static org.junit.jupiter.api.Assertions.*;

class MaxDiscountHandlerSimpleTest {

    private final MaxDiscountHandler handler = new MaxDiscountHandler();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void applyRule_ShouldLimitDiscount_WhenMaxDiscountIsLower() throws Exception {
        // Given
        String config = "{\"maxDiscountAmount\": 15000}";
        JsonNode configNode = objectMapper.readTree(config);
        
        OrderContext context = new OrderContext();
        context.setBaseDiscountAmount(20000.0);

        // When
        RuleResult result = handler.applyRule(configNode, context);

        // Then
        assertTrue(result.isPassed());
        assertEquals(15000.0, result.getAdjustDiscountAmount()); // Limited to 15k
        assertTrue(result.getEvaluationMessage().contains("Max discount rule applied"));
    }

    @Test
    void applyRule_ShouldKeepDiscount_WhenMaxDiscountIsHigher() throws Exception {
        // Given
        String config = "{\"maxDiscountAmount\": 30000}";
        JsonNode configNode = objectMapper.readTree(config);
        
        OrderContext context = new OrderContext();
        context.setBaseDiscountAmount(20000.0);

        // When
        RuleResult result = handler.applyRule(configNode, context);

        // Then
        assertTrue(result.isPassed());
        assertEquals(20000.0, result.getAdjustDiscountAmount());
    }

    @Test
    void applyRule_ShouldReturnInvalid_WhenConfigIsMissing() throws Exception {
        // Given
        String config = "{}";
        JsonNode configNode = objectMapper.readTree(config);
        
        OrderContext context = new OrderContext();
        context.setBaseDiscountAmount(20000.0);

        // When
        RuleResult result = handler.applyRule(configNode, context);

        // Then
        assertFalse(result.isPassed());
    }

    @Test
    void adjust_ShouldReturnLowerValue() {
        // Given
        Double baseDiscount = 25000.0;
        Double maxDiscount = 20000.0;

        // When
        Double result = handler.adjust(baseDiscount, maxDiscount);

        // Then
        assertEquals(20000.0, result);
    }

    @Test
    void adjust_ShouldReturnOriginalValue_WhenMaxIsHigher() {
        // Given
        Double baseDiscount = 15000.0;
        Double maxDiscount = 20000.0;

        // When
        Double result = handler.adjust(baseDiscount, maxDiscount);

        // Then
        assertEquals(15000.0, result);
    }
} 