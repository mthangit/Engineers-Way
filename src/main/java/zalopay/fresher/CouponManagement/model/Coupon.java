package zalopay.fresher.CouponManagement.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;


@Entity
@Table(name = "coupon")
@Data
@ToString(exclude = "couponRules")
public class Coupon {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Column(nullable = false, unique = true)
    String code;

    @Column(name = "title", length = 255)
    String title;

    @Column(name = "description", columnDefinition = "TEXT")
    String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type", nullable = false, length = 50)
    DiscountType discountType;

    @Column(name = "value", nullable = false)
    Double value;

    @Column(name = "start_date", nullable = false)
    LocalDateTime startDate;

    @Column(name = "expire_date", nullable = false)
    LocalDateTime expireDate;

    @Column (name = "is_active", nullable = false)
    Boolean isActive;

    @Column(name = "created_at", updatable = false)
    LocalDateTime createdAt;

    @Column(name = "updated_at")
    LocalDateTime updatedAt;

    @OneToMany(mappedBy = "coupon", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    List<CouponRule> couponRules;

    public boolean isValid(LocalDateTime currentTime) {
        return isActive && 
               currentTime.isAfter(startDate) && 
               currentTime.isBefore(expireDate);
    }

    public double calculateDiscount(double orderAmount) {
        switch (discountType){
            case PERCENTAGE_DISCOUNT -> {
                return orderAmount * (value / 100);
            }
            case FIXED_DISCOUNT -> {
                return Math.max(Math.min(value, orderAmount), 0);
            }
        }
        return orderAmount;
    }
}