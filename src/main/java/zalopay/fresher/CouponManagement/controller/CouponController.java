package zalopay.fresher.CouponManagement.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import zalopay.fresher.CouponManagement.dto.ApiResponse;
import zalopay.fresher.CouponManagement.dto.CouponModify;
import zalopay.fresher.CouponManagement.dto.ResponseCode;
import zalopay.fresher.CouponManagement.model.Coupon;
import zalopay.fresher.CouponManagement.service.CouponService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/coupons")
@RequiredArgsConstructor
public class CouponController {
    
    private final CouponService couponService;
    
    @GetMapping
    public ResponseEntity<List<Coupon>> getAllCoupons() {
        List<Coupon> coupons = couponService.getAllActiveCoupons();

        return ResponseEntity.ok(coupons);
    }
    
    @GetMapping("/type/{discountType}")
    public ResponseEntity<List<Coupon>> getCouponsByType(@PathVariable String discountType) {
        List<Coupon> coupons = couponService.getCouponsByDiscountType(discountType);
        return ResponseEntity.ok(coupons);
    }
    
    @GetMapping("/{code}")
    public ResponseEntity<?> getCouponByCode(@PathVariable String code) {
        Optional<Coupon> coupon = couponService.getCouponByCode(code);
        return ResponseEntity.ok().body(new ApiResponse(
                ResponseCode.RESPONSE_OK,
                coupon
        ));
    }
    
    
    @PatchMapping("/{code}")
    public ResponseEntity<?> updateCoupon(
            @PathVariable String code, 
            @RequestBody CouponModify request) {
        Coupon updatedCoupon = couponService.updateCoupon(code, request);

        return ResponseEntity.ok(new ApiResponse(
                ResponseCode.RESPONSE_OK,
                updatedCoupon
        ));
    }
}