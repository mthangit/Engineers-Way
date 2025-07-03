package zalopay.fresher.CouponManagement.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zalopay.fresher.CouponManagement.dto.CouponUpdateRequest;
import zalopay.fresher.CouponManagement.dto.PagedResponse;
import zalopay.fresher.CouponManagement.exception.AppException;
import zalopay.fresher.CouponManagement.dto.ResponseCode;
import zalopay.fresher.CouponManagement.model.Coupon;
import zalopay.fresher.CouponManagement.repository.CouponRepository;
import zalopay.fresher.CouponManagement.util.ErrorMessages;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CouponService {
    
    private final CouponRepository couponRepository;

    @Transactional(readOnly = true)
    public List<Coupon> getAllActiveCoupons() {
        return couponRepository.findByIsActiveTrue();
    }

    @Transactional(readOnly = true)
    public List<Coupon> getValidCoupons(LocalDateTime currentTime) {
        return couponRepository.findValidCoupons(currentTime);
    }

    @Transactional(readOnly = true)
    public PagedResponse<Coupon> getAllActiveCoupons(Pageable pageable) {
        Page<Coupon> page = couponRepository.findByIsActiveTrue(pageable);
        return createPagedResponse(page);
    }

    private PagedResponse<Coupon> createPagedResponse(Page<Coupon> page) {
        PagedResponse<Coupon> response = new PagedResponse<>();
        response.setContent(page.getContent());
        response.setPage(page.getNumber());
        response.setSize(page.getSize());
        response.setTotalPages(page.getTotalPages());
        response.setTotalElements(page.getTotalElements());
        response.setFirst(page.isFirst());
        response.setLast(page.isLast());
        response.setHasNext(page.hasNext());
        response.setHasPrevious(page.hasPrevious());
        return response;
    }

    @Transactional(readOnly = true)
    public Optional<Coupon> getCouponByCode(String code) {
        return couponRepository.findByCode(code);
    }

    @Transactional(readOnly = true)
    public Optional<Coupon> getCouponById(String id) {
        return couponRepository.findById(id);
    }

    @Transactional
    public Coupon updateCoupon(CouponUpdateRequest updatedCoupon) {
        Optional<Coupon> existingCoupon = getCouponById(updatedCoupon.getId());
        if (existingCoupon.isPresent()) {
            Coupon coupon = existingCoupon.get();
            
            if (updatedCoupon.getCode() != null && !updatedCoupon.getCode().isBlank()) {
                coupon.setCode(updatedCoupon.getCode());
            }
            
            if (updatedCoupon.getTitle() != null) {
                coupon.setTitle(updatedCoupon.getTitle());
            }
            
            if (updatedCoupon.getDescription() != null) {
                coupon.setDescription(updatedCoupon.getDescription());
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
        throw new AppException(ResponseCode.COUPON_NOT_FOUND, ErrorMessages.COUPON_NOT_FOUNT);
    }
}