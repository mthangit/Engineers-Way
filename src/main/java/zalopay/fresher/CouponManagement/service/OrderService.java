package zalopay.fresher.CouponManagement.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zalopay.fresher.CouponManagement.dto.CouponResponse;
import zalopay.fresher.CouponManagement.dto.OrderCreateRequest;
import zalopay.fresher.CouponManagement.dto.OrderResponse;
import zalopay.fresher.CouponManagement.model.OrderContext;
import zalopay.fresher.CouponManagement.engine.processor.RuleProcessor;
import zalopay.fresher.CouponManagement.engine.model.CouponApplyResponse;
import zalopay.fresher.CouponManagement.engine.model.CouponEvaluation;
import zalopay.fresher.CouponManagement.model.Coupon;
import zalopay.fresher.CouponManagement.model.CouponRule;
import zalopay.fresher.CouponManagement.model.Rule;
import zalopay.fresher.CouponManagement.util.GlobalConfig;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Comparator;

@Service
@RequiredArgsConstructor
public class OrderService {
    
    private final CouponService couponService;
    private final RuleProcessor ruleProcessor;

    @Transactional(readOnly = true)
    public OrderResponse applyCouponToOrder(OrderCreateRequest request) {
        OrderContext context = new OrderContext();
        
        context.setCouponCode(request.getCouponCode());
        context.setOrderDate(request.getOrderDate());
        context.setOrderAmount(request.getOrderTotalAmount());

        if (request.getCouponCode() != null && !request.getCouponCode().isBlank()) {
            return processManualCoupon(context);
        }

        return processAutomaticCoupon(context);
    }
    
    private OrderResponse processManualCoupon(OrderContext context) {
        Optional<Coupon> couponOptional = couponService.getCouponByCode(context.getCouponCode());

        if (couponOptional.isEmpty() || !couponOptional.get().isValid(context.getOrderDate())) {
            return buildFailureOrderResponse(context.getOrderAmount(), "Coupon không tồn tại hoặc đã hết hạn");
        }
        Coupon coupon = couponOptional.get();
        CouponEvaluation evaluation = evaluateSingleCoupon(coupon, context);
        
        if (!evaluation.isValid()) {
            return buildFailureOrderResponse(context.getOrderAmount(), evaluation.getApplyResponse().getMessage());
        }

        return buildSuccessOrderResponse(context, evaluation.getCoupon(), evaluation.getDiscountAmount(), "Áp dụng coupon thành công!");
    }
    
    private OrderResponse processAutomaticCoupon(OrderContext context) {
        List<Coupon> validCoupons = couponService.getValidCoupons(context.getOrderDate());
        Optional<CouponEvaluation> bestEvaluation = findBestValidCoupon(validCoupons, context);
        
        if (bestEvaluation.isEmpty()) {
            return buildFailureOrderResponse(context.getOrderAmount(), "Không có coupon nào phù hợp với đơn hàng này");
        }
        CouponEvaluation bestCouponEvaluation = bestEvaluation.get();
        return buildSuccessOrderResponse(context, bestCouponEvaluation.getCoupon(), bestCouponEvaluation.getDiscountAmount(), "Áp dụng coupon tự động thành công!");
    }
    
    private CouponEvaluation evaluateSingleCoupon(Coupon coupon, OrderContext context) {
        List<Rule> rules = coupon.getCouponRules().stream()
                .map(CouponRule::getRule)
                .toList();

        double baseDiscount = coupon.calculateDiscount(context.getOrderAmount());
        context.setBaseDiscountAmount(baseDiscount);
        CouponApplyResponse applyResponse = ruleProcessor.processRules(rules, context, baseDiscount);

        return new CouponEvaluation(coupon, applyResponse);
    }

    @Transactional(readOnly = true)
    private Optional<CouponEvaluation> findBestValidCoupon(List<Coupon> coupons, OrderContext context) {
        return coupons.stream()
                .map(coupon -> evaluateSingleCoupon(coupon, context))
                .filter(CouponEvaluation::isValid)
                .max(Comparator.comparingDouble(CouponEvaluation::getDiscountAmount));
    }
    
    private OrderResponse buildSuccessOrderResponse(OrderContext context, Coupon coupon, double discountAmount, String messagePrefix) {
        double finalAmount = context.getOrderAmount() - discountAmount;
        
        CouponResponse couponResponse = createCouponResponse(coupon);
        OrderResponse response = new OrderResponse();
        response.setTotalAmount(context.getOrderAmount());
        response.setDiscountAmount(discountAmount);
        response.setFinalAmount(finalAmount);
        response.setCoupons(List.of(couponResponse));
        response.setMessage(messagePrefix + " Giảm " + formatCurrency(discountAmount) + " VND");
        response.setOrderDate(context.getOrderDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
        
        return response;
    }
    
    private OrderResponse buildFailureOrderResponse(double orderAmount, String message) {
        OrderResponse response = new OrderResponse();
        response.setDiscountAmount(GlobalConfig.MIN_DISCOUNT_AMOUNT);
        response.setFinalAmount(orderAmount);
        response.setTotalAmount(orderAmount);
        response.setMessage(message);
        return response;
    }
    
    private CouponResponse createCouponResponse(Coupon coupon) {
        CouponResponse couponResponse = new CouponResponse();
        couponResponse.setCouponCode(coupon.getCode());
        couponResponse.setTitle(coupon.getTitle());
        couponResponse.setDescription(coupon.getDescription());
        couponResponse.setStartDate(coupon.getStartDate());
        couponResponse.setExpireDate(coupon.getExpireDate());
        return couponResponse;
    }
    
    private String formatCurrency(double amount) {
        return String.format("%,.0f", amount);
    }

    @Transactional(readOnly = true)
    public List<Coupon> findValidCouponByRule(List<Coupon> coupons, OrderContext context) {
        return coupons.stream()
                .filter(coupon -> {
                    CouponEvaluation evaluation = evaluateSingleCoupon(coupon, context);
                    return evaluation.isValid();
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public Coupon getMaxDiscountCoupon(List<Coupon> coupons, OrderContext context) {
        return findBestValidCoupon(coupons, context)
                .map(CouponEvaluation::getCoupon)
                .orElse(null);
    }

    @Transactional(readOnly = true)
    public List<CouponEvaluation> evaluateMultipleCoupons(List<Coupon> coupons, OrderContext context) {
        return coupons.stream()
                .map(coupon -> evaluateSingleCoupon(coupon, context))
                .toList();
    }
}
