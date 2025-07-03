package zalopay.fresher.CouponManagement.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import zalopay.fresher.CouponManagement.dto.ApiResponse;
import zalopay.fresher.CouponManagement.dto.CouponUpdateRequest;
import zalopay.fresher.CouponManagement.dto.PagedResponse;
import zalopay.fresher.CouponManagement.dto.ResponseCode;
import zalopay.fresher.CouponManagement.exception.AppException;
import zalopay.fresher.CouponManagement.model.Coupon;
import zalopay.fresher.CouponManagement.service.CouponService;
import zalopay.fresher.CouponManagement.util.ErrorMessages;
import zalopay.fresher.CouponManagement.util.ValidationUtils;

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

    @GetMapping("/paged")
    public ResponseEntity<PagedResponse<Coupon>> getAllCouponsPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? 
            Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        
        PagedResponse<Coupon> pagedCoupons = couponService.getAllActiveCoupons(pageable);
        return ResponseEntity.ok(pagedCoupons);
    }
    
    @GetMapping("/{couponCode}")
    public ResponseEntity<?> getCouponByCode(@PathVariable String couponCode) {
        String sanitizedCode = ValidationUtils.validateAndSanitizeCouponCode(couponCode);
        Optional<Coupon> coupon = couponService.getCouponByCode(sanitizedCode);
        return ResponseEntity.ok().body(new ApiResponse(
                ResponseCode.RESPONSE_OK,
                coupon
        ));
    }
    
    @PatchMapping
    public ResponseEntity<?> updateCoupon(
            @Valid @RequestBody CouponUpdateRequest updateCouponRequest) {

        if (updateCouponRequest.getCode() != null) {
            updateCouponRequest.setCode(ValidationUtils.validateAndSanitizeCouponCode(updateCouponRequest.getCode()));
        }
        if (updateCouponRequest.getTitle() != null) {
            updateCouponRequest.setTitle(ValidationUtils.sanitizeInput(updateCouponRequest.getTitle()));
        }
        if (updateCouponRequest.getDescription() != null) {
            updateCouponRequest.setDescription(ValidationUtils.sanitizeInput(updateCouponRequest.getDescription()));
        }
        
        if (updateCouponRequest.getExpireDate() != null && updateCouponRequest.getStartDate() != null) {
            if (updateCouponRequest.getExpireDate().isBefore(updateCouponRequest.getStartDate())) {
                throw new AppException(ResponseCode.INVALID_INPUT, ErrorMessages.INVALID_END_DATE);
            }
        }
        
        Coupon updatedCoupon = couponService.updateCoupon(updateCouponRequest);

        return ResponseEntity.ok(new ApiResponse(
                ResponseCode.RESPONSE_OK,
                updatedCoupon
        ));
    }
}