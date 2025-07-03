package zalopay.fresher.CouponManagement.util;

public class ErrorMessages {
    
    // ===== VALIDATION MESSAGES =====
    public static final String COUPON_CODE_MAX_LENGTH = "Coupon code cannot exceed 50 characters";
    public static final String COUPON_CODE_INVALID_FORMAT = "Coupon code can only contain letters, numbers, underscore, and hyphen";
    public static final String COUPON_NOT_FOUNT = "Coupon not found";
    public static final String TITLE_MAX_LENGTH = "Title cannot exceed 255 characters";
    public static final String TITLE_INVALID_CONTENT = "Title cannot contain HTML special characters";
    
    public static final String DESCRIPTION_MAX_LENGTH = "Description cannot exceed 2000 characters";
    public static final String DESCRIPTION_INVALID_CONTENT = "Description cannot contain HTML special characters";
    
    public static final String ORDER_AMOUNT_REQUIRED = "Order amount is required";
    public static final String ORDER_AMOUNT_MIN = "Order amount must be greater than 0";
    public static final String ORDER_DATE_REQUIRED = "Order date is required";
    
    public static final String INVALID_START_DATE = "Start date must be before end date";
    public static final String INVALID_END_DATE = "End date must be after start date";

    // ===== RULE ENGINE MESSAGES =====
    public static final String MAX_DISCOUNT_INVALID_CONFIG = "Max discount rule configuration is invalid";
    public static final String MAX_DISCOUNT_ADJUSTMENT = "Max discount rule applied. Adjusted from %.2f to %.2f";
    
    public static final String MIN_ORDER_INVALID_CONFIG = "Min order rule configuration is invalid";
    public static final String ORDER_NOT_MEET_REQUIREMENT = "Order does not meet minimum order amount requirement";
    public static final String ORDER_MEET_REQUIREMENT = "Order meets minimum order amount requirement";
    
    public static final String EXPIRY_INVALID_CONFIG = "Expiry rule configuration is invalid or missing expiry date time";
    public static final String COUPON_STILL_VALID = "Coupon is still valid and not expired";
    public static final String COUPON_EXPIRED_FORMAT = "Coupon has expired. Expiry: %s, Current: %s";
    
    public static final String EXPIRY_RULE_ERROR_PREFIX = "Expiry rule error: ";
    public static final String UNEXPECTED_ERROR_PREFIX = "Unexpected error processing expiry rule: ";
    public static final String INVALID_DATE_FORMAT = "Invalid expiry date format. Expected format: yyyy-MM-ddTHH:mm:ss";
    
    // ===== SUCCESS MESSAGES =====
    public static final String COUPON_APPLIED_SUCCESSFULLY = "Coupon applied successfully";
    public static final String COUPON_UPDATED_SUCCESSFULLY = "Coupon updated successfully";
    public static final String COUPON_APPLIED_NO_RULES = "Coupon applied successfully with no additional rules. Discount: %s";
    public static final String COUPON_APPLIED_WITH_RULES = "Coupon applied successfully. Final discount: %s";
    public static final String ORDER_NOT_QUALIFY = "Order does not meet coupon qualification requirements. Discount: 0";
} 