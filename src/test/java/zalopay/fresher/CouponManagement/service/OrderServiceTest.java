package zalopay.fresher.CouponManagement.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import zalopay.fresher.CouponManagement.dto.OrderCreateRequest;
import zalopay.fresher.CouponManagement.dto.OrderResponse;
import zalopay.fresher.CouponManagement.engine.processor.RuleProcessor;
import zalopay.fresher.CouponManagement.engine.model.CouponApplyResponse;
import zalopay.fresher.CouponManagement.model.Coupon;
import zalopay.fresher.CouponManagement.model.DiscountType;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private CouponService couponService;
    
    @Mock 
    private RuleProcessor ruleProcessor;

    @InjectMocks
    private OrderService orderService;

    @Test
    void applyCouponToOrder_WithValidCoupon_ShouldReturnSuccessResponse() {
        // Given
        OrderCreateRequest request = new OrderCreateRequest();
        request.setCouponCode("SAVE20");
        request.setOrderTotalAmount(100000.0);
        request.setOrderDate(LocalDateTime.now());

        Coupon validCoupon = createValidCoupon();
        given(couponService.getCouponByCode("SAVE20")).willReturn(Optional.of(validCoupon));

        CouponApplyResponse applyResponse = new CouponApplyResponse();
        applyResponse.setValid(true);
        applyResponse.setDiscountAmount(20000.0);
        given(ruleProcessor.processRules(any(), any(), any())).willReturn(applyResponse);

        // When
        OrderResponse response = orderService.applyCouponToOrder(request);

        // Then
        assertNotNull(response);
        assertEquals(20000.0, response.getDiscountAmount());
        assertEquals(80000.0, response.getFinalAmount());
    }

    @Test
    void applyCouponToOrder_WithInvalidCoupon_ShouldReturnFailureResponse() {
        // Given
        OrderCreateRequest request = new OrderCreateRequest();
        request.setCouponCode("INVALID");
        request.setOrderTotalAmount(100000.0);
        request.setOrderDate(LocalDateTime.now());

        given(couponService.getCouponByCode("INVALID")).willReturn(Optional.empty());

        // When
        OrderResponse response = orderService.applyCouponToOrder(request);

        // Then
        assertNotNull(response);
        assertEquals(0.0, response.getDiscountAmount());
        assertEquals(100000.0, response.getFinalAmount());
    }

    @Test
    void applyCouponToOrder_WithoutCouponCode_ShouldProcessAutomatically() {
        // Given
        OrderCreateRequest request = new OrderCreateRequest();
        request.setCouponCode(null);
        request.setOrderTotalAmount(100000.0);
        request.setOrderDate(LocalDateTime.now());

        given(couponService.getValidCoupons(any())).willReturn(java.util.List.of());

        // When
        OrderResponse response = orderService.applyCouponToOrder(request);

        // Then
        assertNotNull(response);
        assertEquals(0.0, response.getDiscountAmount());
        assertTrue(response.getMessage().contains("Không có coupon"));
    }

    @Test 
    void formatCurrency_ShouldFormatCorrectly() throws Exception {
        Method formatCurrencyMethod = OrderService.class.getDeclaredMethod("formatCurrency", double.class);
        formatCurrencyMethod.setAccessible(true);

        // When
        String result = (String) formatCurrencyMethod.invoke(orderService, 123456.0);

        // Then
        assertEquals("123,456", result);
    }

    private Coupon createValidCoupon() {
        Coupon coupon = new Coupon();
        coupon.setId("test-id");
        coupon.setCode("SAVE20");
        coupon.setTitle("Test Coupon");
        coupon.setDiscountType(DiscountType.PERCENTAGE_DISCOUNT);
        coupon.setValue(20.0);
        coupon.setIsActive(true);
        coupon.setStartDate(LocalDateTime.now().minusDays(1));
        coupon.setExpireDate(LocalDateTime.now().plusDays(30));
        return coupon;
    }
}