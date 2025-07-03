package zalopay.fresher.CouponManagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import zalopay.fresher.CouponManagement.dto.CouponUpdateRequest;
import zalopay.fresher.CouponManagement.dto.ResponseCode;
import zalopay.fresher.CouponManagement.exception.AppException;
import zalopay.fresher.CouponManagement.model.Coupon;
import zalopay.fresher.CouponManagement.model.DiscountType;
import zalopay.fresher.CouponManagement.service.CouponService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest({CouponController.class, zalopay.fresher.CouponManagement.exception.GlobalExceptionHandler.class})
class CouponControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CouponService couponService;

    @Autowired
    private ObjectMapper objectMapper;

    private Coupon sampleCoupon;
    private List<Coupon> sampleCoupons;

    @BeforeEach
    void setUp() {
        sampleCoupon = new Coupon();
        sampleCoupon.setId("coupon-123");
        sampleCoupon.setCode("SAVE20");
        sampleCoupon.setTitle("Giảm giá 20%");
        sampleCoupon.setDescription("Mã giảm giá 20% cho đơn hàng từ 100k");
        sampleCoupon.setDiscountType(DiscountType.PERCENTAGE_DISCOUNT);
        sampleCoupon.setValue(20.0);
        sampleCoupon.setStartDate(LocalDateTime.now().minusDays(1));
        sampleCoupon.setExpireDate(LocalDateTime.now().plusDays(30));
        sampleCoupon.setIsActive(true);

        sampleCoupons = List.of(sampleCoupon);
    }

    @Test
    void getAllCoupons_ShouldReturnCouponsList() throws Exception {
        given(couponService.getAllActiveCoupons()).willReturn(sampleCoupons);

        mockMvc.perform(get("/api/coupons"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].code").value("SAVE20"))
                .andExpect(jsonPath("$[0].title").value("Giảm giá 20%"));

        verify(couponService).getAllActiveCoupons();
    }

    @Test
    void getCouponByCode_WhenCouponExists_ShouldReturnCoupon() throws Exception {
        // Given
        String code = "SAVE20";
        given(couponService.getCouponByCode(code)).willReturn(Optional.of(sampleCoupon));

        // When & Then
        mockMvc.perform(get("/api/coupons/{code}", code))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Success"))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").exists());

        verify(couponService).getCouponByCode(code);
    }

    @Test
    void getCouponByCode_WhenCouponNotExists_ShouldReturnError() throws Exception {
        // Given
        String code = "INVALID";
        given(couponService.getCouponByCode(code))
                .willThrow(new AppException(ResponseCode.COUPON_NOT_FOUND));

        // When & Then
        MvcResult result = mockMvc.perform(get("/api/coupons/{code}", code))
                .andExpect(status().isNotFound()).andReturn();
        
        System.out.println("Response status: " + result.getResponse().getStatus());
        System.out.println("Response body: " + result.getResponse().getContentAsString());

        verify(couponService).getCouponByCode(code);
    }

    @Test
    void updateCoupon_WhenValidRequest_ShouldReturnUpdatedCoupon() throws Exception {
        // Given
        String code = "SAVE20";
        CouponUpdateRequest updateRequest = new CouponUpdateRequest();
        updateRequest.setCode(code);
        updateRequest.setTitle("Mã giảm giá mới");
        updateRequest.setDescription("Mô tả mới");

        Coupon updatedCoupon = new Coupon();
        updatedCoupon.setCode(code);
        updatedCoupon.setTitle("Mã giảm giá mới");
        updatedCoupon.setDescription("Mô tả mới");

        given(couponService.updateCoupon(any(CouponUpdateRequest.class)))
                .willReturn(updatedCoupon);

        // When & Then
        mockMvc.perform(patch("/api/coupons/{code}", code)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Success"))
                .andExpect(jsonPath("$.code").value(200));

        verify(couponService).updateCoupon(any(CouponUpdateRequest.class));
    }

    @Test
    void updateCoupon_WhenCouponNotExists_ShouldReturnError() throws Exception {
        // Given
        String code = "INVALID";
        CouponUpdateRequest updateRequest = new CouponUpdateRequest();
        updateRequest.setCode(code);
        updateRequest.setTitle("Mã giảm giá mới");

        given(couponService.updateCoupon(any(CouponUpdateRequest.class)))
                .willThrow(new AppException(ResponseCode.COUPON_NOT_FOUND));

        // When & Then
        mockMvc.perform(patch("/api/coupons/{code}", code)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound());

        verify(couponService).updateCoupon(any(CouponUpdateRequest.class));
    }
} 