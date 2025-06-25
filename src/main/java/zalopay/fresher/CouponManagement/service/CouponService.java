package zalopay.fresher.CouponManagement.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zalopay.fresher.CouponManagement.dto.CouponModify;
import zalopay.fresher.CouponManagement.exception.AppException;
import zalopay.fresher.CouponManagement.dto.ResponseCode;
import zalopay.fresher.CouponManagement.model.Coupon;
import zalopay.fresher.CouponManagement.model.DiscountType;
import zalopay.fresher.CouponManagement.repository.CouponRepository;
import zalopay.fresher.CouponManagement.egine.processor.RuleProcessor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CouponService {
    
    private final CouponRepository couponRepository;

    @Transactional
    public List<Coupon> getAllActiveCoupons() {
        return couponRepository.findByIsActiveTrue();
    }

    public List<Coupon> getValidCoupons(LocalDateTime currentTime) {
        return couponRepository.findValidCoupons(currentTime);
    }

    public List<Coupon> getCouponsByDiscountType(String discountType) {
        return couponRepository.findByDiscountType(discountType);
    }
    
    public Optional<Coupon> getCouponByCode(String code) {
        try {
            Optional<Coupon> coupon = couponRepository.findByCode(code);
            if (coupon.isEmpty()) {
                throw new AppException(ResponseCode.COUPON_NOT_FOUND);
            }
            return coupon;
        } catch (Exception e) {
            throw new AppException(ResponseCode.INTERNAL_SERVER_ERROR);
        }

    }

    public Coupon updateCoupon(String code, CouponModify updatedCoupon) {
        Optional<Coupon> existingCoupon = getCouponByCode(code);
        if (existingCoupon.isPresent()) {
            Coupon coupon = existingCoupon.get();
            if (updatedCoupon.getCode() != null){
                coupon.setCode(updatedCoupon.getCode());
            }
            if (updatedCoupon.getTitle() != null) {
                coupon.setTitle(updatedCoupon.getTitle());
            }
            if (updatedCoupon.getDescription() != null) {
                coupon.setDescription(updatedCoupon.getDescription());
            }
            if (updatedCoupon.getDiscountType() != null) {
                coupon.setDiscountType(DiscountType.valueOf(updatedCoupon.getDiscountType()));
            }
            if (updatedCoupon.getStartDate() != null) {
                coupon.setStartDate(updatedCoupon.getStartDate());
            }
            if (updatedCoupon.getExpireDate() != null) {
                coupon.setExpireDate(updatedCoupon.getExpireDate());
            }
            if (updatedCoupon.getIsActive() != null) {
                coupon.setIsActive(updatedCoupon.getIsActive());
            }
            return couponRepository.save(coupon);
        }
        throw new AppException(ResponseCode.COUPON_NOT_FOUND);
    }


    // Tính discount cho coupon
}