package zalopay.fresher.CouponManagement.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import zalopay.fresher.CouponManagement.model.Coupon;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, String> {
    
    Optional<Coupon> findByCode(String code);
    
    boolean existsByCode(String code);
    
    List<Coupon> findByIsActiveTrue();
    
    Page<Coupon> findByIsActiveTrue(Pageable pageable);
    

    
    @Query("SELECT c FROM Coupon c WHERE c.isActive = true AND c.startDate <= :currentTime AND c.expireDate >= :currentTime")
    List<Coupon> findValidCoupons(@Param("currentTime") LocalDateTime currentTime);
    
    @Query("SELECT c FROM Coupon c WHERE c.expireDate < :currentTime")
    List<Coupon> findExpiredCoupons(@Param("currentTime") LocalDateTime currentTime);

}