package zalopay.fresher.CouponManagement.engine.rule.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import zalopay.fresher.CouponManagement.engine.rule.RuleResult;
import zalopay.fresher.CouponManagement.model.OrderContext;

import static org.junit.jupiter.api.Assertions.*;

class MinOrderHandlerSimpleTest {

    private final MinOrderHandler handler = new MinOrderHandler();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void applyRule_ShouldReturnValid_WhenOrderMeetsMinimum() throws Exception {
        // Given
        String config = "{\"minOrderAmount\": 50000}";
        JsonNode configNode = objectMapper.readTree(config);
        
        OrderContext context = new OrderContext();
        context.setOrderAmount(100000.0);

        // When
        RuleResult result = handler.applyRule(configNode, context);

        // Then
        assertTrue(result.isPassed());
        assertTrue(result.getEvaluationMessage().contains("meets the minimum"));
        assertEquals(0.0, result.getAdjustDiscountAmount());
    }

    @Test
    void applyRule_ShouldReturnInvalid_WhenOrderBelowMinimum() throws Exception {
        // Given
        String config = "{\"minOrderAmount\": 150000}";
        JsonNode configNode = objectMapper.readTree(config);
        
        OrderContext context = new OrderContext();
        context.setOrderAmount(100000.0); // Order 100k, min required 150k

        // When
        RuleResult result = handler.applyRule(configNode, context);

        // Then
        assertFalse(result.isPassed());
        assertTrue(result.getEvaluationMessage().contains("does not meet"));
        assertEquals(0.0, result.getAdjustDiscountAmount());
    }

    @Test
    void applyRule_ShouldReturnValid_WhenOrderExactlyAtMinimum() throws Exception {
        // Given
        String config = "{\"minOrderAmount\": 100000}";
        JsonNode configNode = objectMapper.readTree(config);
        
        OrderContext context = new OrderContext();
        context.setOrderAmount(100000.0); // Order exactly 100k, min required 100k

        // When
        RuleResult result = handler.applyRule(configNode, context);

        // Then
        assertTrue(result.isPassed());
        assertTrue(result.getEvaluationMessage().contains("meets the minimum"));
    }

    @Test
    void applyRule_ShouldReturnInvalid_WhenConfigIsMissing() throws Exception {
        // Given
        String config = "{}"; // Missing minOrderAmount
        JsonNode configNode = objectMapper.readTree(config);
        
        OrderContext context = new OrderContext();
        context.setOrderAmount(100000.0);

        // When
        RuleResult result = handler.applyRule(configNode, context);

        // Then
        assertFalse(result.isPassed());
        assertTrue(result.getEvaluationMessage().contains("configuration is invalid"));
    }
} 