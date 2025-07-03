package zalopay.fresher.CouponManagement.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import zalopay.fresher.CouponManagement.dto.CouponUpdateRequest;
import zalopay.fresher.CouponManagement.exception.AppException;
import zalopay.fresher.CouponManagement.model.Coupon;
import zalopay.fresher.CouponManagement.model.DiscountType;
import zalopay.fresher.CouponManagement.repository.CouponRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CouponServiceTest {

    @Mock
    private CouponRepository couponRepository;

    @InjectMocks
    private CouponService couponService;

    @Test
    void getAllActiveCoupons_ShouldReturnActiveList() {
        // Given
        Coupon coupon = createSampleCoupon();
        given(couponRepository.findByIsActiveTrue()).willReturn(List.of(coupon));

        // When
        List<Coupon> result = couponService.getAllActiveCoupons();

        // Then
        assertEquals(1, result.size());
        assertEquals("SAVE20", result.getFirst().getCode());
        verify(couponRepository).findByIsActiveTrue();
    }

    @Test
    void getCouponByCode_ShouldReturnCoupon_WhenExists() {
        // Given
        Coupon coupon = createSampleCoupon();
        given(couponRepository.findByCode("SAVE20")).willReturn(Optional.of(coupon));

        // When
        Optional<Coupon> result = couponService.getCouponByCode("SAVE20");

        // Then
        assertTrue(result.isPresent());
        assertEquals("SAVE20", result.get().getCode());
    }

    @Test
    void getCouponByCode_ShouldReturnEmpty_WhenNotExists() {
        // Given
        given(couponRepository.findByCode("INVALID")).willReturn(Optional.empty());

        // When
        Optional<Coupon> result = couponService.getCouponByCode("INVALID");

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    void updateCoupon_ShouldThrowException_WhenCouponNotExists() {
        // Given
        CouponUpdateRequest updateRequest = new CouponUpdateRequest();
        updateRequest.setId("non-exist-id");
        updateRequest.setCode("NEWCODE");
        given(couponRepository.findById("non-exist-id")).willReturn(Optional.empty());

        // When & Then
        assertThrows(AppException.class, () -> 
            couponService.updateCoupon(updateRequest));
    }

    private Coupon createSampleCoupon() {
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