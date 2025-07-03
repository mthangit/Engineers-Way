package zalopay.fresher.CouponManagement.util;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.stereotype.Component;

@Component
public class ValidationUtils {
    public static String sanitizeInput(String input) {
        if (input == null) {
            return null;
        }
        
        String cleaned = Jsoup.clean(input.strip(), Safelist.none());
        return cleaned.strip();
    }
    
    public static String validateAndSanitizeCouponCode(String code) {
        if (code == null) {
            return null;
        }
        
        String trimmed = code.strip().toUpperCase();
        return sanitizeInput(trimmed);
    }
}