package zalopay.fresher.CouponManagement.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import zalopay.fresher.CouponManagement.dto.OrderCreateRequest;
import zalopay.fresher.CouponManagement.dto.OrderResponse;
import zalopay.fresher.CouponManagement.service.OrderService;
import zalopay.fresher.CouponManagement.util.ValidationUtils;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping("/apply-coupon")
    public ResponseEntity<OrderResponse> applyCouponToOrder(@Valid @RequestBody OrderCreateRequest request) {
        if (request.getCouponCode() != null && !request.getCouponCode().isBlank()) {
            request.setCouponCode(ValidationUtils.validateAndSanitizeCouponCode(request.getCouponCode()));
        }
        
        OrderResponse response = orderService.applyCouponToOrder(request);
        return ResponseEntity.ok(response);
    }
}
