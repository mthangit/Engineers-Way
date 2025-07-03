# Tài Liệu API - Hệ Thống Quản Lý Coupon

## Tổng quan
Hệ thống quản lý coupon cung cấp các API để quản lý và áp dụng coupon cho đơn hàng. Hệ thống hỗ trợ hai loại giảm giá: giảm giá theo phần trăm và giảm giá theo số tiền cố định.

## Base URL
```
http://localhost:8080
```

## Cấu trúc Response chung

### ApiResponse
```json
{
  "message": "string",
  "code": 200,
  "data": "object"
}
```

### Response Codes
- `200` - Success (RESPONSE_OK)
- `400` - Invalid input (INVALID_INPUT)
- `404` - Coupon not found (COUPON_NOT_FOUND)

---

## 1. API Quản Lý Coupon

### 1.1 Lấy tất cả coupon hoạt động

**GET** `/api/coupons`

**Mô tả:** Lấy danh sách tất cả coupon đang hoạt động

**Response:**
```json
[
  {
    "id": "string",
    "code": "string",
    "title": "string",
    "description": "string",
    "discountType": "PERCENTAGE_DISCOUNT | FIXED_DISCOUNT",
    "value": 0.0,
    "startDate": "2024-01-01T00:00:00",
    "expireDate": "2024-12-31T23:59:59",
    "isActive": true,
    "createdAt": "2024-01-01T00:00:00",
    "updatedAt": "2024-01-01T00:00:00"
  }
]
```

### 1.2 Lấy coupon với phân trang

**GET** `/api/coupons/paged`

**Mô tả:** Lấy danh sách coupon với phân trang và sắp xếp

**Query Parameters:**
| Tham số | Kiểu | Mặc định | Mô tả |
|---------|------|----------|-------|
| page | int | 0 | Số trang (bắt đầu từ 0) |
| size | int | 10 | Số lượng item trên một trang |
| sortBy | string | "id" | Trường để sắp xếp |
| sortDir | string | "asc" | Hướng sắp xếp (asc/desc) |

**Response:**
```json
{
  "content": [
    {
      "id": "string",
      "code": "string",
      "title": "string",
      "description": "string",
      "discountType": "PERCENTAGE_DISCOUNT | FIXED_DISCOUNT",
      "value": 0.0,
      "startDate": "2024-01-01T00:00:00",
      "expireDate": "2024-12-31T23:59:59",
      "isActive": true,
      "createdAt": "2024-01-01T00:00:00",
      "updatedAt": "2024-01-01T00:00:00"
    }
  ],
  "page": 0,
  "size": 10,
  "totalPages": 5,
  "totalElements": 50,
  "first": true,
  "last": false,
  "hasNext": true,
  "hasPrevious": false
}
```

### 1.3 Lấy coupon theo mã

**GET** `/api/coupons/{couponCode}`

**Mô tả:** Lấy thông tin chi tiết của một coupon theo mã

**Path Parameters:**
| Tham số | Kiểu | Mô tả |
|---------|------|-------|
| couponCode | string | Mã coupon cần tìm |

**Response:**
```json
{
  "message": "Success",
  "code": 200,
  "data": {
    "id": "string",
    "code": "string",
    "title": "string",
    "description": "string",
    "discountType": "PERCENTAGE_DISCOUNT | FIXED_DISCOUNT",
    "value": 0.0,
    "startDate": "2024-01-01T00:00:00",
    "expireDate": "2024-12-31T23:59:59",
    "isActive": true,
    "createdAt": "2024-01-01T00:00:00",
    "updatedAt": "2024-01-01T00:00:00"
  }
}
```

### 1.4 Cập nhật coupon

**PATCH** `/api/coupons`

**Mô tả:** Cập nhật thông tin coupon (chỉ cập nhật các trường được gửi)

**Request Body:**
```json
{
  "id": "string",
  "title": "string",
  "code": "string",
  "description": "string",
  "startDate": "2024-01-01T00:00:00",
  "expireDate": "2024-12-31T23:59:59",
  "isActive": true
}
```

**Validation Rules:**
- `title`: Tối đa 255 ký tự, không chứa `<>\"'&`
- `code`: Tối đa 50 ký tự, chỉ chứa chữ cái, số, dấu gạch dưới và gạch ngang
- `description`: Tối đa 2000 ký tự, không chứa `<>\"'&`
- `expireDate`: Phải sau `startDate`

**Response:**
```json
{
  "message": "Success",
  "code": 200,
  "data": {
    "id": "string",
    "code": "string",
    "title": "string",
    "description": "string",
    "discountType": "PERCENTAGE_DISCOUNT | FIXED_DISCOUNT",
    "value": 0.0,
    "startDate": "2024-01-01T00:00:00",
    "expireDate": "2024-12-31T23:59:59",
    "isActive": true,
    "createdAt": "2024-01-01T00:00:00",
    "updatedAt": "2024-01-01T00:00:00"
  }
}
```

---

## 2. API Quản Lý Đơn Hàng

### 2.1 Áp dụng coupon cho đơn hàng

**POST** `/api/orders/apply-coupon`

**Mô tả:** Áp dụng coupon vào đơn hàng và tính toán số tiền giảm giá

**Request Body:**
```json
{
  "orderTotalAmount": 100.0,
  "couponCode": "DISCOUNT20",
  "orderDate": "2024-01-15T10:30:00"
}
```

**Validation Rules:**
- `orderTotalAmount`: Bắt buộc, tối thiểu 1.0
- `couponCode`: Tùy chọn, tối đa 50 ký tự, chỉ chứa chữ cái, số, dấu gạch dưới và gạch ngang
- `orderDate`: Bắt buộc

**Response:**
```json
{
  "coupons": [
    {
      "couponCode": "DISCOUNT20",
      "title": "Giảm giá 20%",
      "description": "Giảm giá 20% cho đơn hàng từ 100k",
      "startDate": "2024-01-01T00:00:00",
      "expireDate": "2024-12-31T23:59:59"
    }
  ],
  "totalAmount": 100.0,
  "discountAmount": 20.0,
  "finalAmount": 80.0,
  "orderDate": "2024-01-15T10:30:00",
  "message": "Áp dụng coupon thành công"
}
```

---

## 3. Models và Data Types

### 3.1 Coupon Model
```json
{
  "id": "string",
  "code": "string (unique, max 50 chars)",
  "title": "string (max 255 chars)",
  "description": "string (max 2000 chars)",
  "discountType": "PERCENTAGE_DISCOUNT | FIXED_DISCOUNT",
  "value": "double",
  "startDate": "LocalDateTime",
  "expireDate": "LocalDateTime",
  "isActive": "boolean",
  "createdAt": "LocalDateTime",
  "updatedAt": "LocalDateTime"
}
```

### 3.2 DiscountType Enum
- `PERCENTAGE_DISCOUNT`: Giảm giá theo phần trăm
- `FIXED_DISCOUNT`: Giảm giá số tiền cố định

### 3.3 CouponResponse
```json
{
  "couponCode": "string",
  "title": "string",
  "description": "string",
  "startDate": "LocalDateTime",
  "expireDate": "LocalDateTime"
}
```

---

## 4. Error Handling

### 4.1 Validation Errors
Khi dữ liệu đầu vào không hợp lệ, API sẽ trả về:
```json
{
  "message": "Invalid input",
  "code": 400,
  "data": null
}
```

### 4.2 Coupon Not Found
Khi không tìm thấy coupon:
```json
{
  "message": "Coupon not found",
  "code": 404,
  "data": null
}
```

---

## 5. Quy tắc Business Logic

### 5.1 Tính toán giảm giá
- **PERCENTAGE_DISCOUNT**: `giảm giá = số tiền đơn hàng × (giá trị / 100)`
- **FIXED_DISCOUNT**: `giảm giá = min(giá trị coupon, số tiền đơn hàng)`

### 5.2 Validation Coupon
- Coupon phải đang active (`isActive = true`)
- Thời gian đặt hàng phải nằm trong khoảng `startDate` và `expireDate`
- Mã coupon phải tồn tại trong hệ thống

### 5.3 Input Sanitization
- Tất cả input đều được sanitize để tránh XSS
- Mã coupon chỉ được chứa ký tự alphanumeric, dấu gạch dưới và gạch ngang
- Title và description không được chứa các ký tự đặc biệt nguy hiểm

---

## 6. Ví dụ sử dụng

### 6.1 Lấy danh sách coupon
```bash
curl -X GET "http://localhost:8080/api/coupons" \
     -H "Accept: application/json"
```

### 6.2 Áp dụng coupon
```bash
curl -X POST "http://localhost:8080/api/orders/apply-coupon" \
     -H "Content-Type: application/json" \
     -d '{
       "orderTotalAmount": 150.0,
       "couponCode": "SAVE20",
       "orderDate": "2024-01-15T14:30:00"
     }'
```

### 6.3 Cập nhật coupon
```bash
curl -X PATCH "http://localhost:8080/api/coupons" \
     -H "Content-Type: application/json" \
     -d '{
       "id": "coupon-004",
       "title": "Khuyến mãi mới",
       "isActive": true
     }'
```

---

## 7. Lưu ý quan trọng

1. **Security**: Tất cả input đều được validate và sanitize
2. **Performance**: Sử dụng pagination cho danh sách lớn
3. **Error Handling**: Luôn check response code trước khi xử lý data
4. **Date Format**: Sử dụng ISO 8601 format cho datetime
5. **Validation**: Tuân thủ các quy tắc validation để tránh lỗi 400

---

*Tài liệu này được tạo cho phiên bản API hiện tại của hệ thống Coupon Management* 