# CouponManagement System

## Giới thiệu

CouponManagement là hệ thống quản lý và áp dụng coupon/mã giảm giá được xây dựng trên Spring Boot. Hệ thống sử dụng **Rule Engine** linh hoạt để xử lý các rule trong coupon

## Tính năng chính

### 🎫 Quản lý Coupon
- **Percentage Discount**: Giảm theo phần trăm (15%, 20%, 30%...)
- **Fixed Discount**: Giảm số tiền cố định (50K, 100K, 200K...)
- **Thời gian hiệu lực**: Start date, expire date
- **Trạng thái**: Active/Inactive coupons

### 🔧 Rule Engine
- **JSON Configuration**: Flexible business rules từ database
- **Auto-Discovery**: Tự động phát hiện và đăng ký rule handlers

### 📱 Coupon Usage
- **Manual Coupon**: User nhập mã coupon
- **Auto Coupon**: Hệ thống tự tìm coupon tốt nhất

## Kiến trúc hệ thống

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Controller    │    │    Service      │    │   Repository    │
│                 │───▶│                 │───▶│                 │
│ CouponController│    │ CouponService   │    │CouponRepository │
│ OrderController │    │ OrderService    │    │                 │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                                │
                                ▼
                       ┌─────────────────┐
                       │  Rule Engine    │
                       │                 │
                       │ RuleProcessor   │
                       │ RuleHandlers    │
                       │   │
                       └─────────────────┘
```

## Database Schema

### Bảng chính
- **coupon**: Thông tin coupon (code, title, discount_type, value...)
- **rules**: Định nghĩa các rule types (MIN_ORDER, MAX_DISCOUNT...)
- **coupon_rule**: Many-to-many mapping giữa coupons và rules

### Rule Types hiện có
- **MIN_ORDER**: Đơn hàng tối thiểu (100K, 200K, 500K, 1M VND)
- **MAX_DISCOUNT**: Giảm giá tối đa (50K, 100K, 200K, 500K VND)
- **EXPIRY_TIME**: Thời gian expire của coupon

## Công nghệ sử dụng

- **Java 21**: Programming language
- **Spring Boot 3.5.0**: Framework chính
- **MySQL**: Database
- **Maven**: Build tool
- **Jackson**: JSON processing

## Sample Data

Tập 100 sample coupons:
- **70 Percentage coupons**: SUMMER2024 (15%), FLASH30 (30%), BIRTHDAY50 (50%)...
- **30 Fixed coupons**: SAVE50K, SAVE100K, MEGA10M...


### Extensible Design
- Thêm rule type mới chỉ cần: Enum + Handler + Database record
