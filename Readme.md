# RULE ENGINE - MAPPING VÀ HANDLER SYSTEM

## TỔNG QUAN RULE ENGINE

Rule Engine được thiết kế với **Strategy Pattern** để xử lý các loại quy tắc khác nhau một cách linh hoạt và có thể mở rộng.

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
- **type**: Loại rule tương ứng với TypeRule enum (MIN_ORDER, MAX_DISCOUNT...)
- **config**: JSON configuration chứa parameters cụ thể cho rule
- **description**: Mô tả rule dễ hiểu cho business users
- **created_at, updated_at**: Timestamp để tracking changes

**Các rule types hiện có:**
- **MIN_ORDER rules**: Quy định đơn hàng tối thiểu (100K, 200K, 500K, 1M VND)
- **MAX_DISCOUNT rules**: Giới hạn số tiền giảm tối đa (50K, 100K, 200K, 500K VND)

**JSON Configuration format:**
- Min order rules: chứa field "minOrderAmount" với giá trị VND
- Max discount rules: chứa field "maxDiscountAmount" với giá trị VND

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
Hệ thống tuân theo pattern: TypeRule Enum → RuleHandler Interface → Concrete Handler Component

## PHÂN LOẠI RULES

### 1. KindRule (Enum) - Phân loại theo mục đích
**QUALIFICATION**: Quy tắc điều kiện kiểm tra đủ điều kiện hay không
- Dùng để validate xem coupon có thể áp dụng cho order này không
- Nếu có rule QUALIFICATION nào fail thì toàn bộ coupon bị reject
- Ví dụ: minimum order amount, user level requirements

**ADJUSTMENT**: Quy tắc điều chỉnh thay đổi giá trị discount
- Dùng để modify discount amount sau khi đã pass qualification
- Apply theo chain, output của rule này là input của rule tiếp theo
- Ví dụ: maximum discount cap, progressive discount rates

### 2. TypeRule (Enum) - Định nghĩa loại rule cụ thể
**MIN_ORDER**: Quy tắc đơn hàng tối thiểu
- Thuộc nhóm QUALIFICATION
- Kiểm tra order amount có đạt minimum requirement không

**MAX_DISCOUNT**: Quy tắc giảm giá tối đa  
- Thuộc nhóm ADJUSTMENT
- Giới hạn discount amount ở mức tối đa cho phép

## RULE HANDLER INTERFACE

### Contract chính
Interface RuleHandler định nghĩa 4 methods cơ bản:

**getType()**: Xác định loại rule này xử lý (MIN_ORDER, MAX_DISCOUNT...)

**getKindRule()**: Xác định nhóm rule (QUALIFICATION hoặc ADJUSTMENT)

**validate()**: Method cho QUALIFICATION rules, nhận config và context, return RuleResult

**adjust()**: Method cho ADJUSTMENT rules, nhận config, context và baseDiscount, return modified discount

### Nguyên tắc thiết kế
**Default methods**: Handlers chỉ override method cần thiết
- QUALIFICATION rules chỉ override validate() method
- ADJUSTMENT rules chỉ override adjust() method
- Không cần implement tất cả methods, tránh boilerplate code

**Flexible configuration**: Sử dụng JsonNode cho cấu hình linh hoạt
- Cho phép complex JSON structures
- Type-safe access với JsonNode methods
- Runtime parsing từ database JSON strings

## MAPPING SYSTEM

### Auto-Discovery và Registration
RuleEngineConfig class có responsibility tạo mapping giữa TypeRule và Handler instances.

Spring tự động inject tất cả RuleHandler beans vào một List, sau đó convert thành Map với:
- **Key**: TypeRule enum (result của getType() method)
- **Value**: Handler instance (concrete implementation)

### Cơ chế hoạt động
**Spring Component Scan**: Tìm tất cả class implements RuleHandler với @Component annotation

**Auto-Injection**: Inject List<RuleHandler> vào RuleEngineConfig constructor hoặc method parameter

**Map Creation**: Stream through list để tạo Map<TypeRule, RuleHandler> cho O(1) lookup

**Runtime Lookup**: RuleProcessor sử dụng map để tìm handler phù hợp với rule type

## CONCRETE HANDLERS

### 1. MinOrderHandler (QUALIFICATION)

Handler này xử lý rule kiểm tra đơn hàng tối thiểu:
- **Type**: MIN_ORDER
- **Kind**: QUALIFICATION  
- **Chức năng**: Validate order amount có đạt minimum requirement không

**Logic xử lý:**
- Parse "minOrderAmount" từ JSON config
- So sánh với order amount từ OrderContext
- Return RuleResult với status pass/fail và message tương ứng
- Nếu order amount >= minOrderAmount thì pass, ngược lại fail

**Error handling:**
- Nếu config thiếu "minOrderAmount": return fail với message "Missing configuration"
- Nếu order amount null: return fail
- Graceful handling cho tất cả edge cases

**Config format**: JSON object chứa field "minOrderAmount" với giá trị numeric (VND)

### 2. MaxDiscountHandler (ADJUSTMENT)

Handler này xử lý rule giới hạn discount tối đa:
- **Type**: MAX_DISCOUNT
- **Kind**: ADJUSTMENT
- **Chức năng**: Cap discount amount ở mức tối đa cho phép

**Logic xử lý:**
- Parse "maxDiscountAmount" từ JSON config  
- So sánh với baseDiscount được truyền vào
- Nếu baseDiscount > maxDiscount thì return maxDiscount
- Ngược lại return baseDiscount không đổi

**Chain processing:**
- Nhận baseDiscount từ step trước (có thể là original discount hoặc đã adjusted)
- Apply cap logic
- Return modified discount cho step tiếp theo

**Config format**: JSON object chứa field "maxDiscountAmount" với giá trị numeric (VND)

## RULE PROCESSOR - ENGINE CORE

### Workflow xử lý rules
RuleProcessor là core component thực hiện business logic xử lý rules theo 2-phase approach:

**Input**: List<Rule> rules, OrderContext context, Double baseDiscount
**Output**: CouponApplyResponse với success/failure status và final discount amount

**Main Flow:**
1. Phase 1 - QUALIFICATION: Validate tất cả qualification rules
2. Nếu có rule nào fail → return failure response ngay lập tức (fail-fast)
3. Phase 2 - ADJUSTMENT: Apply chain of adjustments lên discount amount
4. Return success response với final discount

### Phase 1: Qualification Processing
**Mục đích**: Kiểm tra xem coupon có đủ điều kiện áp dụng cho order này không

**Process flow:**
- Iterate through tất cả rules
- Filter chỉ những rules có KindRule = QUALIFICATION
- Với mỗi qualification rule:
  - Lookup handler từ ruleHandlerMap using rule.getType()
  - Parse JSON config từ rule.getConfig()
  - Call handler.validate(config, context)
  - Collect RuleResult

**Fail-fast logic:**
- Nếu có bất kỳ rule nào return isValid = false
- Immediately set allPassed = false
- Continue checking remaining rules để collect full diagnostic info
- Return overall false nếu có any failures

**Error handling:**
- JsonProcessingException từ config parsing → mark as failed
- Missing handler → skip rule với warning log
- Handler exception → graceful handling với detailed error message

### Phase 2: Adjustment Processing
**Mục đích**: Modify discount amount dựa trên adjustment rules

**Chain processing:**
- Start với baseDiscount (từ coupon.value)
- Iterate through tất cả rules với KindRule = ADJUSTMENT
- Với mỗi adjustment rule:
  - Lookup handler từ map
  - Parse config
  - Call handler.adjust(config, context, currentDiscount)
  - Update currentDiscount với return value

**Sequential application:**
- Output của rule này = input của rule tiếp theo
- Cho phép complex business logic với multiple adjustments
- Maintain audit trail của từng adjustment step

**Safety guarantees:**
- Final discount luôn >= 0 (không cho phép negative discount)
- Config errors được handle gracefully mà không crash process
- Invalid adjustments không break chain

## CÁCH THÊM RULE TYPE MỚI

Hệ thống được thiết kế để việc thêm rule type mới trở nên đơn giản và không ảnh hưởng đến code hiện tại. Quá trình này chỉ cần 4 bước:

### Bước 1: Mở rộng TypeRule Enum
Thêm constant mới vào enum `TypeRule` để định nghĩa loại rule mới. Ví dụ muốn thêm rule kiểm tra thời gian và level user:

- `TIME_BASED`: Rule kiểm tra coupon chỉ áp dụng trong khung giờ nhất định
- `USER_LEVEL`: Rule kiểm tra level của user (VIP, Gold, Silver...)

Enum này đóng vai trò như identifier duy nhất cho mỗi loại rule.

### Bước 2: Implement Handler Class
Tạo class mới implement interface `RuleHandler` và annotate với `@Component`:

**Thiết kế TimeBasedHandler:**
- Implement method `getType()` return `TIME_BASED`
- Implement method `getKindRule()` return `QUALIFICATION` (vì đây là rule kiểm tra điều kiện)
- Override method `validate()` để implement business logic:
  - Parse startTime và endTime từ JSON config
  - So sánh với thời gian order hiện tại
  - Return RuleResult với trạng thái pass/fail

**Business Logic Example:**
- Lấy thời gian order từ OrderContext
- Parse config JSON để get startTime="09:00", endTime="17:00"  
- Check xem order time có nằm trong khoảng [09:00, 17:00] không
- Return appropriate message: "Trong khung giờ áp dụng" hoặc "Ngoài khung giờ áp dụng"

### Bước 3: Thêm Rule Definition vào Database
Insert record mới vào bảng `rules` với:
- `id`: unique identifier (ví dụ: "rule-time-business-hours")
- `type`: "TIME_BASED" (phải match với enum)
- `config`: JSON string chứa business parameters
- `description`: Mô tả dễ hiểu cho business users

**Ví dụ JSON config:**
- `{"startTime": "09:00", "endTime": "17:00"}` cho giờ hành chính
- `{"startTime": "18:00", "endTime": "22:00"}` cho giờ tối
- `{"startTime": "00:00", "endTime": "06:00"}` cho giờ đêm khuya

### Bước 4: Auto-Discovery tự động hoạt động

**Startup Process:**
1. Spring Component Scan tự động detect `TimeBasedHandler` do có `@Component`
2. Spring auto-inject handler này vào `List<RuleHandler>` trong RuleEngineConfig
3. Method `ruleHandlerMap()` tự động add entry: `TIME_BASED → TimeBasedHandler instance`
4. RuleProcessor có thể immediate sử dụng rule type mới

**Runtime Behavior:**
- Khi có rule với type="TIME_BASED" từ database
- RuleProcessor lookup `ruleHandlerMap.get(TIME_BASED)`
- Tìm thấy TimeBasedHandler instance
- Gọi appropriate method (validate/adjust) dựa trên KindRule
- Business logic execute và return result

**Zero Configuration:**
- Không cần modify existing code
- Không cần restart application (nếu hot reload enabled)
- Existing coupons không bị ảnh hưởng
- New rule type immediate available cho tất cả flows


## QUY TRÌNH XỬ LÝ CHI TIẾT

### 1. Luồng xử lý Order (Manual vs Auto Coupon)

**Bước 1: Phân loại request**
Khi có một order request, hệ thống sẽ xác định đây là **Manual Coupon** (user nhập mã) hay **Auto Coupon** (hệ thống tự tìm):

**Manual Coupon Flow:**
- User cung cấp coupon code cụ thể (ví dụ: "SUMMER2024")
- Hệ thống gọi `CouponService.getCouponByCode()` để tìm coupon chính xác
- Nếu tìm thấy và coupon còn active, tiến hành validate rules

**Auto Coupon Flow:**  
- Hệ thống gọi `CouponService.getAllActiveCoupons()` để lấy tất cả coupon đang hoạt động
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
`RuleProcessor.processRules()` sẽ thực hiện xử lý 2 phases tuần tự

### 2. Chi tiết Rule Processing Engine

**Phase 1: QUALIFICATION (Kiểm tra điều kiện)**
Đây là phase quan trọng nhất, quyết định coupon có được áp dụng hay không:

- Hệ thống duyệt qua tất cả rules của coupon
- Chỉ xử lý những rules có `KindRule = QUALIFICATION`  
- Với mỗi qualification rule:
  - Lookup handler tương ứng từ `ruleHandlerMap`
  - Parse JSON config từ database
  - Gọi method `validate()` của handler
  - Nhận về `RuleResult` với trạng thái pass/fail

**Fail-Fast Logic:**
- Nếu có bất kỳ qualification rule nào fail → toàn bộ coupon bị reject
- Không cần kiểm tra các rule còn lại
- Return ngay failure response với message cụ thể

**Phase 2: ADJUSTMENT (Điều chỉnh giá trị)**
Chỉ chạy khi tất cả qualification rules đã pass:

- Bắt đầu với base discount value (từ coupon.value)
- Duyệt qua tất cả rules có `KindRule = ADJUSTMENT`
- Với mỗi adjustment rule:
  - Lookup handler từ map
  - Parse JSON config  
  - Gọi method `adjust()` với current discount value
  - Nhận về modified discount value
  - Update discount value cho lần iteration tiếp theo

**Chain Processing:**
- Adjustment rules được apply theo thứ tự chain
- Output của rule này = input của rule tiếp theo
- Đảm bảo final discount >= 0

### 3. Rule Handler Mapping System

**Auto-Discovery Process:**
Quá trình này diễn ra khi application startup:

**Bước 1: Component Scanning**
- Spring scan tất cả classes trong package
- Tìm các class implement `RuleHandler` interface và có annotation `@Component`
- Tự động tạo instance của các handler này

**Bước 2: Dependency Injection**
- Spring inject tất cả RuleHandler instances vào `List<RuleHandler>`
- List này được truyền vào `RuleEngineConfig.ruleHandlerMap()` method

**Bước 3: Map Creation**
- Method `ruleHandlerMap()` tạo `Map<TypeRule, RuleHandler>`
- Key = result của `handler.getType()` (ví dụ: MIN_ORDER)
- Value = handler instance (ví dụ: MinOrderHandler instance)
- Map này được register như một Spring Bean

**Bước 4: Runtime Usage**
- Khi cần xử lý rule, RuleProcessor lookup handler từ map
- O(1) lookup time với HashMap
- Type-safe với enum keys

### 4. JSON Configuration Processing

**Database Storage:**
- Rule configurations được lưu dưới dạng JSON string trong database
- Cho phép flexible business rules mà không cần change code

**Runtime Parsing:**
- Khi cần sử dụng, `ObjectMapper.readTree()` parse JSON string thành `JsonNode`
- JsonNode cung cấp methods để extract values type-safe
- Handler có thể access nested JSON properties

**Configuration Priority:**
1. **coupon_rule.config** (Override cao nhất)
2. **rules.config** (Default của rule type)  
3. **Handler defaults** (Hard-coded trong code)

**Error Handling:**
- Nếu JSON invalid → graceful degradation
- Nếu required fields missing → rule fails safely
- Detailed error messages trong RuleResult

## SAMPLE DATA TRONG DATABASE

### Rules Master Data (8 rules)
Database hiện có 8 rule definitions covering 2 rule types chính:

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
**Percentage coupons**: Thường có both MIN_ORDER + MAX_DISCOUNT rules để kiểm soát cả input và output
**Fixed coupons**: Chỉ có MIN_ORDER rules vì không cần giới hạn discount amount (đã fix sẵn)
**High-value coupons**: Require higher minimum orders để protect business margin
