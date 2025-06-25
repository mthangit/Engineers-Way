package zalopay.fresher.CouponManagement.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import zalopay.fresher.CouponManagement.dto.OrderCreateRequest;
import zalopay.fresher.CouponManagement.dto.OrderResponse;
import zalopay.fresher.CouponManagement.service.OrderService;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping("/apply-coupon")
    public ResponseEntity<OrderResponse> applyCouponToOrder(@RequestBody OrderCreateRequest request) {
        OrderResponse response = orderService.applyCouponToOrder(request);
        return ResponseEntity.ok(response);
    }
}
