package zalopay.fresher.CouponManagement.util;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ValidationUtilsTest {

    @Test
    void sanitizeInput_returnsNullForNullInput() {
        String result = ValidationUtils.sanitizeInput(null);
        Assertions.assertNull(result);
    }

    @Test
    void sanitizeInput_returnsEmptyForBlankInput() {
        String result = ValidationUtils.sanitizeInput("   ");
        Assertions.assertEquals("", result);
    }

    @Test
    void sanitizeInput_removesHtmlTags() {
        String input = "<b>bold</b>";
        String result = ValidationUtils.sanitizeInput(input);
        Assertions.assertEquals("bold", result);
    }

    @Test
    void sanitizeInput_removesScriptTags() {
        String input = "<script>alert('XSS');</script>";
        String result = ValidationUtils.sanitizeInput(input);
        Assertions.assertEquals("", result);
    }

    @Test
    void validateAndSanitizeCouponCode_returnsNullForNullInput() {
        String result = ValidationUtils.validateAndSanitizeCouponCode(null);
        Assertions.assertNull(result);
    }

    @Test
    void validateAndSanitizeCouponCode_returnsEmptyForBlankInput() {
        String result = ValidationUtils.validateAndSanitizeCouponCode("   ");
        Assertions.assertEquals("", result);
    }

    @Test
    void validateAndSanitizeCouponCode_convertsToUpperCaseAndSanitizes() {
        String input = "  <b>code123</b>  ";
        String result = ValidationUtils.validateAndSanitizeCouponCode(input);
        Assertions.assertEquals("CODE123", result);
    }

    @Test
    void validateAndSanitizeCouponCode_removesHtmlTagsAndConvertsToUpperCase() {
        String input = "<script>alert('XSS');</script>code";
        String result = ValidationUtils.validateAndSanitizeCouponCode(input);
        Assertions.assertEquals("CODE", result);
    }
}