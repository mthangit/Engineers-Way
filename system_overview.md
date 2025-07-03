# RULE ENGINE - MAPPING VÀ HANDLER SYSTEM

## TỔNG QUAN SOLUTION

**Rule Engine System** được thiết kế với **Strategy Pattern** kết hợp **Spring Dependency Injection** để xử lý các quy tắc business logic một cách linh hoạt và có thể mở rộng. Hệ thống này cho phép:

- **Dynamic Rule Processing**: Xử lý các loại quy tắc khác nhau mà không cần modify core logic
- **Annotation-Based Mapping**: Sử dụng `@HandlesRule` annotation để tự động map RuleType với Handler
- **Spring Auto-Discovery**: Tự động detect và register các handler mới
- **Fail-Fast Validation**: Dừng xử lý ngay khi có rule nào fail để tối ưu performance
- **Chain Processing**: Các rule có thể modify discount amount theo chain pattern

**Lợi ích chính:**
- **Extensibility**: Thêm rule type mới chỉ cần 3 bước đơn giản
- **Maintainability**: Mỗi rule được encapsulate trong handler riêng biệt
- **Performance**: O(1) lookup time với HashMap mapping
- **Reliability**: Comprehensive error handling và graceful degradation

## DATABASE STRUCTURE

### Tổng quan quan hệ Database

Database được thiết kế theo mô hình **Many-to-Many** giữa Coupon và Rules thông qua bảng junction `coupon_rule`. Điều này cho phép:

- **Một coupon có thể có nhiều rules** (ví dụ: vừa có rule minimum order, vừa có rule maximum discount)
- **Một rule có thể được áp dụng cho nhiều coupons** (ví dụ: rule "minimum order 100K" có thể dùng cho nhiều coupon khác nhau)
- **Flexible configuration**: Mỗi coupon có thể override rule configuration riêng biệt

**Quan hệ chính:**
- `coupon` (1) ←→ (n) `coupon_rule` ←→ (n) `rules` (1)

### Chi tiết các bảng Database

#### 1. Bảng `coupon` - Thông tin Coupon
Bảng này lưu trữ thông tin chính của các coupon với các trường quan trọng:
- **id**: Primary key duy nhất cho mỗi coupon
- **code**: Mã coupon unique mà user nhập (ví dụ: SUMMER2024, SAVE50K...)
- **title và description**: Tiêu đề và mô tả hiển thị cho user
- **discount_type**: Loại giảm giá - PERCENTAGE_DISCOUNT hoặc FIXED_DISCOUNT
- **value**: Giá trị giảm (phần trăm cho percentage, số tiền cho fixed)
- **start_date, expire_date**: Thời gian có hiệu lực của coupon
- **is_active**: Trạng thái hoạt động (có thể tắt coupon mà không xóa)
- **created_at, updated_at**: Timestamp để audit

**Các loại coupon phổ biến:**
- Percentage coupons: Giảm 15% mùa hè, giảm 30% flash sale
- Fixed amount coupons: Giảm ngay 50,000 VND, tiết kiệm 100,000 VND

#### 2. Bảng `rules` - Master Rules Definition
Bảng này lưu trữ định nghĩa các rule types có thể áp dụng cho coupons:
- **id**: Primary key unique cho mỗi rule definition
- **type**: Loại rule tương ứng với RuleType enum (MIN_ORDER, MAX_DISCOUNT, NOT_EXPIRE)
- **config**: JSON configuration chứa parameters cụ thể cho rule
- **description**: Mô tả rule dễ hiểu cho business users
- **created_at, updated_at**: Timestamp để tracking changes

**Các rule types hiện có:**
- **MIN_ORDER rules**: Quy định đơn hàng tối thiểu (100K, 200K, 500K, 1M VND)
- **MAX_DISCOUNT rules**: Giới hạn số tiền giảm tối đa (50K, 100K, 200K, 500K VND)
- **NOT_EXPIRE rules**: Kiểm tra thời gian hết hạn của coupon với custom expiry time

**JSON Configuration format:**
- Min order rules: chứa field "minOrderAmount" với giá trị VND
- Max discount rules: chứa field "maxDiscountAmount" với giá trị VND  
- Not expire rules: chứa field "expiryDateTime" với format ISO_LOCAL_DATE_TIME

#### 3. Bảng `coupon_rule` - Many-to-Many Junction
Bảng junction này kết nối coupons với rules, cho phép áp dụng nhiều rules cho một coupon:
- **id**: Primary key của mapping record
- **coupon_id**: Foreign key trỏ đến coupon cụ thể
- **rule_id**: Foreign key trỏ đến rule definition
- **config**: JSON override config (optional) để customize rule cho coupon cụ thể

**Mapping patterns:**
- Percentage coupons thường có cả MIN_ORDER và MAX_DISCOUNT rules
- Fixed amount coupons chỉ cần MIN_ORDER rules
- High-value coupons được pair với higher minimum order requirements

**Configuration override:**
- Nếu config field empty: sử dụng default config từ rules table
- Nếu có config: override specific parameters cho coupon này
- Ví dụ: rule default min order 100K, nhưng coupon VIP có thể override thành 50K

### Kiến trúc cốt lõi
Hệ thống tuân theo pattern: RuleType Enum → @HandlesRule Annotation → RuleHandler Implementation

## PHÂN LOẠI RULES

### RuleType (Enum) - Định nghĩa loại rule cụ thể

**MIN_ORDER**: Quy tắc đơn hàng tối thiểu
- Kiểm tra order amount có đạt minimum requirement không
- Nếu fail thì toàn bộ coupon bị reject
- Config: `{"minOrderAmount": 100000}` (VND)

**MAX_DISCOUNT**: Quy tắc giảm giá tối đa  
- Giới hạn discount amount ở mức tối đa cho phép
- Apply sau khi đã pass validation
- Config: `{"maxDiscountAmount": 50000}` (VND)

**NOT_EXPIRE**: Quy tắc kiểm tra hết hạn
- Kiểm tra coupon có còn hiệu lực tại thời điểm order không
- Cho phép custom expiry time khác với coupon.expire_date
- Config: `{"expiryDateTime": "2024-12-31T23:59:59"}` (ISO format)

## RULE HANDLER INTERFACE

### Contract chính
Interface RuleHandler định nghĩa contract đơn giản với 3 methods:

**getType()**: Tự động extract RuleType từ @HandlesRule annotation
- Sử dụng reflection để đọc annotation value
- Throw exception nếu handler không có annotation
- Đảm bảo type safety cho mapping system

**createResult()**: Helper method để tạo RuleResult standardized
- Tự động set ruleType từ getType()
- Parameters: isValid, message, discountAmount
- Consistent result format cho tất cả handlers

**applyRule()**: Main business logic method
- Input: JsonNode config, OrderContext context
- Output: RuleResult với evaluation result
- Handlers implement business logic cụ thể trong method này

### Nguyên tắc thiết kế
**Single Responsibility**: Mỗi handler chỉ xử lý một loại rule duy nhất

**Annotation-Based Mapping**: Sử dụng @HandlesRule thay vì manual registration

**Flexible JSON Config**: JsonNode cho phép complex configurations mà không cần predefined classes

## MAPPING SYSTEM

### Annotation-Based Auto-Discovery
RuleEngineConfig class sử dụng Spring DI để tự động tạo mapping giữa RuleType và Handler instances.

### Cơ chế hoạt động
**Spring Component Scan**: Tìm tất cả class implements RuleHandler với @Component annotation

**Auto-Injection**: Spring inject List<RuleHandler> vào RuleEngineConfig.ruleHandlerMap() method

**Stream-Based Map Creation**: 
```java
return ruleHandlers.stream()
    .collect(Collectors.toMap(RuleHandler::getType, handler -> handler));
```

**Runtime Lookup**: RuleProcessor sử dụng map để tìm handler với O(1) performance

### Configuration Bean Creation
RuleEngineConfig tạo 2 Spring beans chính:
- **ruleHandlerMap**: Map<RuleType, RuleHandler> cho handler lookup
- **ruleProcessor**: RuleProcessor instance với injected dependencies

## CONCRETE HANDLERS

### 1. MinOrderHandler (Validation Rule)

Handler kiểm tra đơn hàng tối thiểu:
- **Annotation**: `@HandlesRule(RuleType.MIN_ORDER)`
- **Purpose**: Validate order amount đạt minimum requirement

**Business Logic:**
- Parse "minOrderAmount" từ JSON config
- Compare với order amount từ OrderContext  
- Return success nếu orderAmount >= minOrderAmount
- Return failure với descriptive message nếu không đạt

**Error Handling:**
- Invalid config → return failure với message "Min order rule configuration is invalid"
- Missing orderAmount → return failure
- Graceful handling cho all edge cases

### 2. MaxDiscountHandler (Adjustment Rule)

Handler giới hạn discount tối đa:
- **Annotation**: `@HandlesRule(RuleType.MAX_DISCOUNT)`  
- **Purpose**: Cap discount amount ở mức maximum allowed

**Business Logic:**
- Parse "maxDiscountAmount" từ JSON config
- Compare với baseDiscountAmount từ context
- Return Math.min(baseDiscount, maxDiscount)
- Preserve original discount nếu đã nhỏ hơn max

**Chain Integration:**
- Nhận discount amount từ previous processing steps
- Apply capping logic
- Return adjusted amount cho subsequent rules

### 3. NotExpireHandler (Validation Rule)

Handler kiểm tra thời gian hết hạn:
- **Annotation**: `@HandlesRule(RuleType.NOT_EXPIRE)`
- **Purpose**: Validate coupon chưa hết hạn tại thời điểm order

**Business Logic:**
- Parse "expiryDateTime" từ config với ISO_LOCAL_DATE_TIME format
- Get current time từ OrderContext.orderDate hoặc LocalDateTime.now()
- Return success nếu currentTime <= expiryDateTime
- Return failure với detailed expiry information

**Advanced Features:**
- Support custom expiry time khác với coupon.expire_date
- Comprehensive datetime parsing với proper error handling
- Detailed failure messages với actual vs expected times

## RULE PROCESSOR - ENGINE CORE

### Unified Processing Workflow
RuleProcessor thực hiện single-phase processing thay vì separate qualification/adjustment phases:

**Input**: List<Rule> rules, OrderContext context, Double baseDiscount
**Output**: CouponApplyResponse với success/failure status và final discount amount

**Simplified Flow:**
1. Iterate through tất cả rules sequentially
2. Với mỗi rule: lookup handler → parse config → execute applyRule()
3. Collect results và process theo business logic
4. Return appropriate response type

### Core Processing Methods

**processRules()**: Main entry point cho rule evaluation
- Handle empty rules case với buildNoRuleResponse()
- Delegate actual processing cho processAllRules()
- Build final response based on processing results

**processAllRules()**: Sequential rule processing
- Iterate through rules với error handling
- Lookup handler từ ruleHandlerMap
- Parse JSON config với JsonProcessingException handling
- Execute handler.applyRule() với comprehensive error catching
- Update discount amount based on rule results

**Response Building Methods:**
- **buildNoRuleResponse()**: Cho coupons không có additional rules
- **buildFailedQualificationResponse()**: Cho orders không meet requirements  
- **buildSuccessResponse()**: Cho successful coupon applications

### Error Handling Strategy
**Graceful Degradation**: Rule processing errors không crash entire flow

**Detailed Error Messages**: Specific error information trong RuleResult

**Fail-Fast for Critical Rules**: Stop processing nếu validation rules fail

**Chain Continuation**: Adjustment rules continue processing even nếu có non-critical errors

## CÁCH THÊM RULE TYPE MỚI

Hệ thống được thiết kế để việc thêm rule type mới trở nên đơn giản với chỉ 3 bước:

### Bước 1: Thêm RuleType Enum Value
Thêm constant mới vào enum `RuleType`. Ví dụ cho rule kiểm tra user level:

```java
public enum RuleType {
    MIN_ORDER,
    MAX_DISCOUNT,
    NOT_EXPIRE,
    USER_LEVEL  // New rule type
}
```

### Bước 2: Implement Handler Class
Tạo class mới implement RuleHandler với annotation:

```java
@Component
@HandlesRule(RuleType.USER_LEVEL)
public class UserLevelHandler implements RuleHandler {
    
    @Override
    public RuleResult applyRule(JsonNode config, OrderContext context) {
        // Parse required user level từ config
        String requiredLevel = config.get("requiredLevel").asText();
        
        // Get user level từ context (cần extend OrderContext)
        String userLevel = context.getUserLevel();
        
        // Business logic validation
        if (isValidLevel(userLevel, requiredLevel)) {
            return createResult(true, "User level requirement met", 0.0);
        } else {
            return createResult(false, "User level insufficient", 0.0);
        }
    }
    
    private boolean isValidLevel(String userLevel, String requiredLevel) {
        // Implementation logic cho level comparison
        return /* business logic */;
    }
}
```

### Bước 3: Database Configuration
Insert rule definition vào database:

```sql
INSERT INTO rules (id, type, config, description) VALUES 
('rule-user-level-vip', 'USER_LEVEL', 
 '{"requiredLevel": "VIP"}', 
 'Require VIP user level');
```

### Bước 4: Automatic Integration
**Zero Configuration Required:**
- Spring auto-detect UserLevelHandler do có @Component
- Auto-inject vào List<RuleHandler> trong RuleEngineConfig
- Tự động add entry vào ruleHandlerMap: USER_LEVEL → UserLevelHandler
- RuleProcessor immediate có thể sử dụng rule type mới

## QUY TRÌNH XỬ LÝ CHI TIẾT

### 1. Luồng xử lý Order (Manual vs Auto Coupon)

**Bước 1: Phân loại request**
Khi có một order request, hệ thống sẽ xác định đây là **Manual Coupon** (user nhập mã) hay **Auto Coupon** (hệ thống tự tìm):

**Manual Coupon Flow:**
- User cung cấp coupon code cụ thể (ví dụ: "SUMMER2024")
- Hệ thống gọi `CouponService.getCouponByCode()` để tìm coupon chính xác
- Nếu tìm thấy và coupon còn active, tiến hành validate rules

**Auto Coupon Flow:**  
- Hệ thống gọi `CouponService.getValidCoupons()` để lấy tất cả coupon còn hiệu lực
- Iterate qua từng coupon một cách tuần tự
- Với mỗi coupon, kiểm tra xem có thể áp dụng được không
- Chọn coupon tốt nhất (thường là cho discount cao nhất)

**Bước 2: Load Rules từ Database**
Sau khi có coupon, hệ thống sẽ:
- Query bảng `coupon_rule` để lấy tất cả rule_id liên kết với coupon này
- Load thông tin chi tiết của từng rule từ bảng `rules`
- Parse JSON configuration của mỗi rule
- Tạo List<Rule> để truyền vào RuleProcessor

**Bước 3: Rule Processing**
`RuleProcessor.processRules()` sẽ thực hiện unified processing

### 2. Chi tiết Rule Processing Engine

**Unified Processing Approach:**
RuleProcessor xử lý tất cả rules trong single pass thay vì separate phases:

- Iterate through tất cả rules theo thứ tự
- Với mỗi rule:
  - Lookup handler từ `ruleHandlerMap` using rule.getType()
  - Parse JSON config từ rule.getConfig()  
  - Execute `handler.applyRule(config, context)`
  - Process RuleResult based on business logic

**Processing Logic:**
- Nếu có rule nào return isValid = false → fail toàn bộ coupon
- Adjustment rules có thể modify discount amount
- Continue processing để collect diagnostic information
- Final discount = max(GlobalConfig.MIN_DISCOUNT_AMOUNT, processedDiscount)

**Error Handling:**
- JsonProcessingException → mark rule as failed
- Missing handler → skip với error message
- Handler exceptions → graceful handling với detailed logging

### 3. Rule Handler Mapping System

**Annotation-Based Discovery:**
Quá trình mapping diễn ra tại application startup:

**Bước 1: Component Scanning**
- Spring scan package để tìm classes implement RuleHandler
- Filter classes có @Component annotation
- Tạo instances của các handler

**Bước 2: Dependency Injection**  
- Spring inject List<RuleHandler> vào RuleEngineConfig
- List chứa tất cả handler instances

**Bước 3: Map Creation**
- Method `ruleHandlerMap()` sử dụng Stream API:
```java
return ruleHandlers.stream()
    .collect(Collectors.toMap(RuleHandler::getType, handler -> handler));
```

**Bước 4: Bean Registration**
- Map được register như Spring Bean
- Inject vào RuleProcessor để sử dụng

### 4. JSON Configuration Processing

**Database Storage:**
- Rule configurations được lưu dưới dạng JSON string
- Flexible structure cho different rule types

**Runtime Parsing:**
- `ObjectMapper.readTree()` parse JSON thành JsonNode
- Type-safe access với JsonNode methods
- Support nested JSON structures

**Configuration Priority:**
1. **coupon_rule.config** (Override cao nhất)
2. **rules.config** (Default configuration)
3. **Handler defaults** (Fallback values)

**Error Handling:**
- Invalid JSON → graceful failure với descriptive message
- Missing required fields → rule fails với specific error
- Type mismatches → proper error reporting

## SAMPLE DATA TRONG DATABASE

### Rules Master Data (3 rule types)
Database hiện có rules covering 3 rule types chính:

**Min Order Rules (4 variations):**
- rule-min-order-1: Minimum order 100,000 VND
- rule-min-order-2: Minimum order 200,000 VND  
- rule-min-order-3: Minimum order 500,000 VND
- rule-min-order-4: Minimum order 1,000,000 VND

**Max Discount Rules (4 variations):**
- rule-max-discount-1: Maximum discount 50,000 VND
- rule-max-discount-2: Maximum discount 100,000 VND
- rule-max-discount-3: Maximum discount 200,000 VND
- rule-max-discount-4: Maximum discount 500,000 VND

**Not Expire Rules (Custom expiry validation):**
- rule-not-expire-1: Custom expiry times cho special campaigns
- rule-not-expire-2: Extended validity periods
- rule-not-expire-3: Holiday-specific expiry rules

### Coupons Sample (100 coupons)
Database chứa 100 sample coupons được phân bổ như sau:

**Percentage Discount Coupons (70 coupons):**
- SUMMER2024: 15% discount với min order 100K và max discount 50K
- NEWYEAR25: 20% discount với min order 200K và max discount 100K
- FLASH30: 30% discount với min order 1M và max discount 500K
- BIRTHDAY50: 50% discount với min order 1M và max discount 500K

**Fixed Discount Coupons (30 coupons):**
- SAVE50K: Giảm ngay 50,000 VND với min order 100K
- SAVE100K: Giảm ngay 100,000 VND với min order 200K
- SAVE500K: Giảm ngay 500,000 VND với min order 1M
- MEGA10M: Giảm ngay 10,000,000 VND với min order 1M

### Coupon-Rule Mapping Patterns
**Percentage coupons**: Thường có cả MIN_ORDER + MAX_DISCOUNT + NOT_EXPIRE rules
**Fixed coupons**: Có MIN_ORDER + NOT_EXPIRE rules (không cần max discount)
**Special campaign coupons**: Sử dụng NOT_EXPIRE rules với custom expiry times
**High-value coupons**: Require higher minimum orders và multiple validation rules

## TÓM TẮT SOLUTION

### Kiến trúc và Thiết kế
**Strategy Pattern + Spring DI**: Kết hợp Strategy Pattern với Spring Dependency Injection để tạo ra hệ thống rule processing linh hoạt và có thể mở rộng.

**Annotation-Driven Mapping**: Sử dụng `@HandlesRule` annotation để tự động map RuleType với Handler implementation, giảm thiểu boilerplate code và manual configuration.

**Unified Processing Model**: Thay vì phân chia phức tạp thành qualification/adjustment phases, hệ thống sử dụng single-pass processing với flexible business logic.

### Lợi ích Kinh doanh
**Rapid Feature Development**: Thêm rule type mới chỉ cần 3 bước đơn giản (enum + handler + database), cho phép business team nhanh chóng implement new promotions.

**Data-Driven Configuration**: Rule parameters được lưu trong database dưới dạng JSON, cho phép business users modify rules mà không cần deploy code.

**Scalable Architecture**: Hệ thống handle được volume lớn với O(1) handler lookup và efficient rule processing.

### Tính năng Kỹ thuật
**Comprehensive Error Handling**: Graceful degradation với detailed error messages, đảm bảo system stability ngay cả khi có invalid configurations.

**Type Safety**: Enum-based rule types và annotation-driven mapping đảm bảo compile-time safety và runtime reliability.

**Spring Integration**: Full integration với Spring ecosystem cho dependency injection, transaction management, và configuration management.

**Maintainable Code**: Clear separation of concerns với mỗi rule type có dedicated handler, dễ dàng debug và maintain.
