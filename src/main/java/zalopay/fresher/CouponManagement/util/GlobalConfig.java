package zalopay.fresher.CouponManagement.util;

public class GlobalConfig {
    
    // ===== DISCOUNT AMOUNT CONSTANTS =====
    public static final double MIN_DISCOUNT_AMOUNT = 0.0;
    public static final double DEFAULT_DISCOUNT = 0.0;
    public static final double NO_DISCOUNT_ADJUSTMENT = 0.0;
    public static final double PERCENTAGE_DIVISOR = 100.0;
    
    // ===== VALIDATION CONSTANTS =====  
    public static final double MIN_VALID_AMOUNT = 0.0;
    public static final int MIN_STRING_LENGTH = 0;
    
    // ===== COMPARISON CONSTANTS =====
    public static final int EQUAL_COMPARISON = 0;

    // ===== DATABASE FIELD LENGTH CONSTANTS =====
    public static final int TITLE_MAX_LENGTH = 255;
    public static final int DISCOUNT_TYPE_MAX_LENGTH = 50;

    // ===== RULE CONFIG KEYS =====
    public static final String MAX_DISCOUNT_CONFIG_KEY = "maxDiscountAmount";
    public static final String MIN_ORDER_CONFIG_KEY = "minOrderAmount";
    public static final String EXPIRY_DATETIME_CONFIG_KEY = "expiryDateTime";
}
