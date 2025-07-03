package zalopay.fresher.CouponManagement.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "coupon_rule")
@Data
public class CouponRule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id", nullable = false)
    Coupon coupon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rule_id", nullable = false)
    Rule rule;
}
