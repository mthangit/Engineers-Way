package zalopay.fresher.CouponManagement.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import zalopay.fresher.CouponManagement.util.GlobalConfig;
import java.time.LocalDateTime;
import java.util.ArrayList;
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

    @Column(name = "title", length = GlobalConfig.TITLE_MAX_LENGTH)
    String title;

    @Column(name = "description", columnDefinition = "TEXT")
    String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type", nullable = false, length = GlobalConfig.DISCOUNT_TYPE_MAX_LENGTH)
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
    private List<CouponRule> couponRules = new ArrayList<>();

    public boolean isValid(LocalDateTime currentTime) {
        return isActive && 
               currentTime.isAfter(startDate) && 
               currentTime.isBefore(expireDate);
    }

    private double calculatePercentageBaseDiscount(double orderAmount) {
        return Math.max(orderAmount * (value / GlobalConfig.PERCENTAGE_DIVISOR), GlobalConfig.MIN_DISCOUNT_AMOUNT);
    }

    private double calculateFixedAmountDiscount(double orderAmount) {
        return Math.max(Math.min(value, orderAmount), GlobalConfig.MIN_DISCOUNT_AMOUNT);
    }

    public double calculateDiscount(double orderAmount) {
        switch (discountType){
            case PERCENTAGE_DISCOUNT -> {
                return calculatePercentageBaseDiscount(orderAmount);
            }
            case FIXED_DISCOUNT -> {
                return calculateFixedAmountDiscount(orderAmount);
            }
        }
        return GlobalConfig.DEFAULT_DISCOUNT;
    }
}