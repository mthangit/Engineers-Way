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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Component
@ForRule(RuleType.NOT_EXPIRE)
public class NotExpireHandler implements RuleHandler {

    private boolean isValidConfig(JsonNode config) {
        if (config == null || !config.has(GlobalConfig.EXPIRY_DATETIME_CONFIG_KEY)) {
            return false;
        }
        String expiryDateTime = config.get(GlobalConfig.EXPIRY_DATETIME_CONFIG_KEY).asText();
        return expiryDateTime != null && !expiryDateTime.isBlank();
    }

    private LocalDateTime parseExpiryDateTime(JsonNode config) {
        try {
            String expiryDateTime = config.get(GlobalConfig.EXPIRY_DATETIME_CONFIG_KEY).asText();
            return LocalDateTime.parse(expiryDateTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(ErrorMessages.INVALID_DATE_FORMAT);
        }
    }

    private LocalDateTime getCurrentDateTime(OrderContext context) {
        return context.getOrderDate() != null ? context.getOrderDate() : LocalDateTime.now();
    }

    private boolean isCouponNotExpired(LocalDateTime expiryDateTime, LocalDateTime currentDateTime) {
        return currentDateTime.isBefore(expiryDateTime) || currentDateTime.isEqual(expiryDateTime);
    }

    @Override
    public RuleResult applyRule(JsonNode config, OrderContext context) {
        if (!isValidConfig(config)) {
            return createResult(false, ErrorMessages.EXPIRY_INVALID_CONFIG, GlobalConfig.NO_DISCOUNT_ADJUSTMENT);
        }

        try {
            LocalDateTime expiryDateTime = parseExpiryDateTime(config);
            LocalDateTime currentDateTime = getCurrentDateTime(context);

            if (isCouponNotExpired(expiryDateTime, currentDateTime)) {
                return createResult(true, ErrorMessages.COUPON_STILL_VALID, GlobalConfig.NO_DISCOUNT_ADJUSTMENT);
            } else {
                String expiredMessage = String.format(ErrorMessages.COUPON_EXPIRED_FORMAT, 
                                  expiryDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                                  currentDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                return createResult(false, expiredMessage, GlobalConfig.NO_DISCOUNT_ADJUSTMENT);
            }
        } catch (IllegalArgumentException e) {
            return createResult(false, ErrorMessages.EXPIRY_RULE_ERROR_PREFIX + e.getMessage(), GlobalConfig.NO_DISCOUNT_ADJUSTMENT);
        } catch (Exception e) {
            return createResult(false, ErrorMessages.UNEXPECTED_ERROR_PREFIX + e.getMessage(), GlobalConfig.NO_DISCOUNT_ADJUSTMENT);
        }
    }
} 