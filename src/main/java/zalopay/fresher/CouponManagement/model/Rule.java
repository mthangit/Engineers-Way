package zalopay.fresher.CouponManagement.model;

import jakarta.persistence.*;
import lombok.Data;
import zalopay.fresher.CouponManagement.egine.rule.TypeRule;

import java.time.LocalDateTime;

@Entity
@Table(name = "rules")
@Data
public class Rule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    String id;

    @Column(name = "type", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    TypeRule type;

    @Column(name = "config", columnDefinition = "JSON")
    String config;

    @Column(name = "description", columnDefinition = "TEXT")
    String description;

    @Column(name = "created_at", updatable = false)
    LocalDateTime createdAt;

    @Column(name = "updated_at")
    LocalDateTime updatedAt;
}
