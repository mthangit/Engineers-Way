package zalopay.fresher.CouponManagement.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class CouponTest {

    @Test
    void isValid_ShouldReturnTrue_WhenCouponIsActiveAndInValidTimeRange() {
        // Given
        Coupon coupon = new Coupon();
        coupon.setIsActive(true);
        coupon.setStartDate(LocalDateTime.now().minusDays(1));
        coupon.setExpireDate(LocalDateTime.now().plusDays(1));
        
        // When
        boolean result = coupon.isValid(LocalDateTime.now());
        
        // Then
        assertTrue(result);
    }

    @Test
    void isValid_ShouldReturnFalse_WhenCouponIsInactive() {
        // Given
        Coupon coupon = new Coupon();
        coupon.setIsActive(false);
        coupon.setStartDate(LocalDateTime.now().minusDays(1));
        coupon.setExpireDate(LocalDateTime.now().plusDays(1));
        
        // When
        boolean result = coupon.isValid(LocalDateTime.now());
        
        // Then
        assertFalse(result);
    }

    @Test
    void isValid_ShouldReturnFalse_WhenCouponIsExpired() {
        // Given
        Coupon coupon = new Coupon();
        coupon.setIsActive(true);
        coupon.setStartDate(LocalDateTime.now().minusDays(2));
        coupon.setExpireDate(LocalDateTime.now().minusDays(1));
        
        // When
        boolean result = coupon.isValid(LocalDateTime.now());
        
        // Then
        assertFalse(result);
    }

    @Test
    void calculateDiscount_ShouldCalculatePercentageCorrectly() {
        // Given
        Coupon coupon = new Coupon();
        coupon.setDiscountType(DiscountType.PERCENTAGE_DISCOUNT);
        coupon.setValue(20.0); // 20%
        
        // When
        double discount = coupon.calculateDiscount(100000.0);
        
        // Then
        assertEquals(20000.0, discount); // 20% of 100k = 20k
    }

    @Test
    void calculateDiscount_ShouldCalculateFixedAmountCorrectly() {
        // Given
        Coupon coupon = new Coupon();
        coupon.setDiscountType(DiscountType.FIXED_DISCOUNT);
        coupon.setValue(50000.0); // 50k fixed
        
        // When
        double discount = coupon.calculateDiscount(200000.0);
        
        // Then
        assertEquals(50000.0, discount);
    }

    @Test
    void calculateDiscount_ShouldNotExceedOrderAmount_WithFixedDiscount() {
        // Given
        Coupon coupon = new Coupon();
        coupon.setDiscountType(DiscountType.FIXED_DISCOUNT);
        coupon.setValue(100000.0); // 100k fixed
        
        // When
        double discount = coupon.calculateDiscount(50000.0); // Order only 50k
        
        // Then
        assertEquals(50000.0, discount); // Cannot exceed order amount
    }
} 