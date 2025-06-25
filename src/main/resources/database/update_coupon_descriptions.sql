-- Update detailed descriptions for all coupons with conditions
-- Percentage discount coupons

-- Seasonal coupons
UPDATE `coupon` SET `description` = 'Khuyến mãi mùa hè 2024 - Giảm 15% cho tất cả đơn hàng. Điều kiện: Đơn hàng tối thiểu 100,000 VND, giảm tối đa 50,000 VND. Thời gian áp dụng: 01/01/2024 - 31/12/2024.' WHERE `code` = 'SUMMER2024';

UPDATE `coupon` SET `description` = 'Chào đón năm mới 2025 - Giảm 20% cho đơn hàng đầu năm. Điều kiện: Đơn hàng tối thiểu 200,000 VND, giảm tối đa 100,000 VND. Thời gian áp dụng: 01/01/2024 - 31/01/2025.' WHERE `code` = 'NEWYEAR25';

UPDATE `coupon` SET `description` = 'Mùa xuân tươi mới - Giảm 25% cho đơn hàng mùa xuân. Điều kiện: Đơn hàng tối thiểu 500,000 VND, giảm tối đa 200,000 VND. Thời gian áp dụng: 01/03/2024 - 31/05/2024.' WHERE `code` = 'SPRING25';

UPDATE `coupon` SET `description` = 'Thu về lãng mạn - Giảm 22% cho đơn hàng mùa thu. Điều kiện: Đơn hàng tối thiểu 200,000 VND, giảm tối đa 100,000 VND. Thời gian áp dụng: 01/09/2024 - 30/11/2024.' WHERE `code` = 'AUTUMN22';

UPDATE `coupon` SET `description` = 'Đông ấm áp - Giảm 35% cho đơn hàng mùa đông. Điều kiện: Đơn hàng tối thiểu 500,000 VND, giảm tối đa 200,000 VND. Thời gian áp dụng: 01/12/2024 - 28/02/2025.' WHERE `code` = 'WINTER35';

-- Customer segment coupons
UPDATE `coupon` SET `description` = 'Ưu đãi đặc biệt cho sinh viên - Giảm 10% cho tất cả đơn hàng. Điều kiện: Đơn hàng tối thiểu 100,000 VND, giảm tối đa 50,000 VND. Cần xuất trình thẻ sinh viên hợp lệ.' WHERE `code` = 'STUDENT10';

UPDATE `coupon` SET `description` = 'Khách hàng thân thiết VIP - Giảm 20% dành riêng cho khách VIP. Điều kiện: Đơn hàng tối thiểu 200,000 VND, giảm tối đa 100,000 VND. Chỉ áp dụng cho thành viên VIP.' WHERE `code` = 'LOYAL20';

UPDATE `coupon` SET `description` = 'Ưu đãi gia đình hạnh phúc - Giảm 40% cho đơn hàng gia đình. Điều kiện: Đơn hàng tối thiểu 500,000 VND, giảm tối đa 200,000 VND. Áp dụng khi mua từ 3 sản phẩm trở lên.' WHERE `code` = 'FAMILY40';

UPDATE `coupon` SET `description` = 'Sinh nhật vàng đặc biệt - Giảm 50% trong tháng sinh nhật. Điều kiện: Đơn hàng tối thiểu 1,000,000 VND, giảm tối đa 500,000 VND. Chỉ áp dụng trong tháng sinh nhật của khách hàng.' WHERE `code` = 'BIRTHDAY50';

-- Time-based coupons
UPDATE `coupon` SET `description` = 'Cuối tuần vui vẻ - Giảm 15% cho đơn hàng cuối tuần. Điều kiện: Đơn hàng tối thiểu 100,000 VND, giảm tối đa 50,000 VND. Chỉ áp dụng thứ 7 và chủ nhật.' WHERE `code` = 'WEEKEND15';

UPDATE `coupon` SET `description` = 'Flash sale giờ vàng - Giảm 30% trong khung giờ flash sale. Điều kiện: Đơn hàng tối thiểu 1,000,000 VND, giảm tối đa 500,000 VND. Áp dụng từ 20:00-22:00 hàng ngày.' WHERE `code` = 'FLASH30';

UPDATE `coupon` SET `description` = 'Giữa tuần tiết kiệm - Giảm 12% cho đơn hàng giữa tuần. Điều kiện: Đơn hàng tối thiểu 100,000 VND, giảm tối đa 50,000 VND. Áp dụng từ thứ 2 đến thứ 5.' WHERE `code` = 'MIDWEEK12';

UPDATE `coupon` SET `description` = 'Sáng sớm tiết kiệm - Giảm 18% cho đơn hàng buổi sáng. Điều kiện: Đơn hàng tối thiểu 200,000 VND, giảm tối đa 100,000 VND. Áp dụng từ 6:00-10:00 sáng.' WHERE `code` = 'MORNING18';

UPDATE `coupon` SET `description` = 'Hoàng hôn lãng mạn - Giảm 16% cho đơn hàng buổi chiều. Điều kiện: Đơn hàng tối thiểu 100,000 VND, giảm tối đa 50,000 VND. Áp dụng từ 16:00-19:00.' WHERE `code` = 'SUNSET16';

-- Category-specific coupons
UPDATE `coupon` SET `description` = 'Ưu đãi công nghệ - Giảm 28% cho sản phẩm công nghệ. Điều kiện: Đơn hàng tối thiểu 200,000 VND, giảm tối đa 100,000 VND. Chỉ áp dụng cho danh mục điện tử, máy tính.' WHERE `code` = 'TECH28';

UPDATE `coupon` SET `description` = 'Thời trang sành điệu - Giảm 35% cho sản phẩm thời trang. Điều kiện: Đơn hàng tối thiểu 500,000 VND, giảm tối đa 200,000 VND. Áp dụng cho quần áo, giày dép, phụ kiện.' WHERE `code` = 'FASHION35';

UPDATE `coupon` SET `description` = 'Ẩm thực ngon miệng - Giảm 45% cho đơn hàng ẩm thực. Điều kiện: Đơn hàng tối thiểu 500,000 VND, giảm tối đa 200,000 VND. Áp dụng cho thực phẩm, đồ uống.' WHERE `code` = 'FOOD45';

UPDATE `coupon` SET `description` = 'Sách vở tri thức - Giảm 17% cho sản phẩm giáo dục. Điều kiện: Đơn hàng tối thiểu 100,000 VND, giảm tối đa 50,000 VND. Áp dụng cho sách, văn phòng phẩm.' WHERE `code` = 'BOOK17';

UPDATE `coupon` SET `description` = 'Thể thao khỏe mạnh - Giảm 23% cho dụng cụ thể thao. Điều kiện: Đơn hàng tối thiểu 200,000 VND, giảm tối đa 100,000 VND. Áp dụng cho đồ thể thao, gym.' WHERE `code` = 'SPORT23';

UPDATE `coupon` SET `description` = 'Gia dụng tiện nghi - Giảm 26% cho đồ gia dụng. Điều kiện: Đơn hàng tối thiểu 200,000 VND, giảm tối đa 100,000 VND. Áp dụng cho nội thất, điện gia dụng.' WHERE `code` = 'HOME26';

UPDATE `coupon` SET `description` = 'Làm đẹp rạng ngời - Giảm 31% cho sản phẩm làm đẹp. Điều kiện: Đơn hàng tối thiểu 200,000 VND, giảm tối đa 100,000 VND. Áp dụng cho mỹ phẩm, skincare.' WHERE `code` = 'BEAUTY31';

UPDATE `coupon` SET `description` = 'Du lịch khám phá - Giảm 38% cho dịch vụ du lịch. Điều kiện: Đơn hàng tối thiểu 500,000 VND, giảm tối đa 200,000 VND. Áp dụng cho tour, khách sạn.' WHERE `code` = 'TRAVEL38';

-- Fixed discount coupons
UPDATE `coupon` SET `description` = 'Tiết kiệm ngay 50,000 VND cho mọi đơn hàng. Điều kiện: Đơn hàng tối thiểu 100,000 VND. Không giới hạn danh mục sản phẩm.' WHERE `code` = 'SAVE50K';

UPDATE `coupon` SET `description` = 'Tiết kiệm ngay 100,000 VND cho đơn hàng lớn. Điều kiện: Đơn hàng tối thiểu 200,000 VND. Áp dụng cho tất cả sản phẩm.' WHERE `code` = 'SAVE100K';

UPDATE `coupon` SET `description` = 'Tiết kiệm ngay 200,000 VND cho đơn hàng cao cấp. Điều kiện: Đơn hàng tối thiểu 500,000 VND. Ưu đãi đặc biệt cho khách hàng thân thiết.' WHERE `code` = 'SAVE200K';

UPDATE `coupon` SET `description` = 'Tiết kiệm ngay 30,000 VND cho đơn hàng nhỏ. Điều kiện: Đơn hàng tối thiểu 100,000 VND. Phù hợp cho lần mua hàng đầu tiên.' WHERE `code` = 'SAVE30K';

UPDATE `coupon` SET `description` = 'Tiết kiệm ngay 500,000 VND cho đơn hàng VIP. Điều kiện: Đơn hàng tối thiểu 1,000,000 VND. Dành cho khách hàng VIP và đơn hàng cao giá trị.' WHERE `code` = 'SAVE500K';

UPDATE `coupon` SET `description` = 'Tiết kiệm ngay 1,000,000 VND - Ưu đãi khủng. Điều kiện: Đơn hàng tối thiểu 1,000,000 VND. Số lượng có hạn, áp dụng cho đơn hàng lớn.' WHERE `code` = 'SAVE1M';

UPDATE `coupon` SET `description` = 'Siêu tiết kiệm 10,000,000 VND - Ưu đãi độc quyền. Điều kiện: Đơn hàng tối thiểu 1,000,000 VND. Chỉ dành cho khách hàng Enterprise và đơn hàng đặc biệt lớn.' WHERE `code` = 'MEGA10M';

-- Weather-themed coupons
UPDATE `coupon` SET `description` = 'Ngày mưa ấm áp - Giảm 36% khi trời mưa. Điều kiện: Đơn hàng tối thiểu 200,000 VND, giảm tối đa 100,000 VND. Tự động áp dụng khi thời tiết có mưa.' WHERE `code` = 'RAINY36';

UPDATE `coupon` SET `description` = 'Ngày nắng rạng rỡ - Giảm 39% khi trời nắng đẹp. Điều kiện: Đơn hàng tối thiểu 200,000 VND, giảm tối đa 100,000 VND. Áp dụng trong những ngày nắng.' WHERE `code` = 'SUNNY39';

UPDATE `coupon` SET `description` = 'Ngày se lạnh - Giảm 44% khi thời tiết lạnh. Điều kiện: Đơn hàng tối thiểu 200,000 VND, giảm tối đa 100,000 VND. Áp dụng khi nhiệt độ dưới 20°C.' WHERE `code` = 'COLD44';

UPDATE `coupon` SET `description` = 'Ngày nóng bức - Giảm 46% khi thời tiết nóng. Điều kiện: Đơn hàng tối thiểu 200,000 VND, giảm tối đa 100,000 VND. Áp dụng khi nhiệt độ trên 32°C.' WHERE `code` = 'HOT46';

-- Activity-based coupons  
UPDATE `coupon` SET `description` = 'Giờ cao điểm bận rộn - Giảm 48% trong giờ cao điểm. Điều kiện: Đơn hàng tối thiểu 200,000 VND, giảm tối đa 100,000 VND. Áp dụng 7:00-9:00 và 17:00-19:00.' WHERE `code` = 'RUSH48';

UPDATE `coupon` SET `description` = 'Giờ yên tĩnh thư giãn - Giảm 49% trong giờ ít người. Điều kiện: Đơn hàng tối thiểu 200,000 VND, giảm tối đa 100,000 VND. Áp dụng 14:00-16:00.' WHERE `code` = 'QUIET49';

UPDATE `coupon` SET `description` = 'Giờ làm việc năng suất - Giảm 53% trong giờ hành chính. Điều kiện: Đơn hàng tối thiểu 200,000 VND, giảm tối đa 100,000 VND. Áp dụng 8:00-17:00 các ngày làm việc.' WHERE `code` = 'WORK53';

UPDATE `coupon` SET `description` = 'Giờ nghỉ ngơi thư giãn - Giảm 54% trong giờ nghỉ trưa. Điều kiện: Đơn hàng tối thiểu 200,000 VND, giảm tối đa 100,000 VND. Áp dụng 12:00-14:00.' WHERE `code` = 'REST54';

-- Special occasion coupons
UPDATE `coupon` SET `description` = 'May mắn số 8 - Giảm 8% mang lại may mắn. Điều kiện: Đơn hàng tối thiểu 100,000 VND, giảm tối đa 50,000 VND. Áp dụng vào các ngày có số 8.' WHERE `code` = 'LUCKY8';

UPDATE `coupon` SET `description` = 'Tình yêu ngọt ngào - Giảm 21% cho đôi lứa. Điều kiện: Đơn hàng tối thiểu 200,000 VND, giảm tối đa 100,000 VND. Đặc biệt dành cho các cặp đôi.' WHERE `code` = 'LOVE21';

UPDATE `coupon` SET `description` = 'Sức khỏe vàng - Giảm 13% cho sản phẩm sức khỏe. Điều kiện: Đơn hàng tối thiểu 100,000 VND, giảm tối đa 50,000 VND. Áp dụng cho thực phẩm chức năng, y tế.' WHERE `code` = 'HEALTH13';

UPDATE `coupon` SET `description` = 'Bạn bè thân thiết - Giảm 19% khi mua cùng bạn. Điều kiện: Đơn hàng tối thiểu 200,000 VND, giảm tối đa 100,000 VND. Áp dụng khi có ít nhất 2 khách hàng cùng đặt hàng.' WHERE `code` = 'FRIEND19';

-- Lifestyle coupons
UPDATE `coupon` SET `description` = 'Âm nhạc sôi động - Giảm 24% cho sản phẩm âm nhạc. Điều kiện: Đơn hàng tối thiểu 200,000 VND, giảm tối đa 100,000 VND. Áp dụng cho nhạc cụ, phụ kiện âm nhạc.' WHERE `code` = 'MUSIC24';

UPDATE `coupon` SET `description` = 'Game thú vị - Giảm 33% cho sản phẩm game. Điều kiện: Đơn hàng tối thiểu 200,000 VND, giảm tối đa 100,000 VND. Áp dụng cho game, phụ kiện gaming.' WHERE `code` = 'GAME33';

UPDATE `coupon` SET `description` = 'Ô tô đẳng cấp - Giảm 37% cho phụ kiện ô tô. Điều kiện: Đơn hàng tối thiểu 500,000 VND, giảm tối đa 200,000 VND. Áp dụng cho phụ tụng, phụ kiện xe hơi.' WHERE `code` = 'CAR37';

UPDATE `coupon` SET `description` = 'Thú cưng đáng yêu - Giảm 27% cho sản phẩm thú cưng. Điều kiện: Đơn hàng tối thiểu 200,000 VND, giảm tối đa 100,000 VND. Áp dụng cho thức ăn, đồ chơi thú cưng.' WHERE `code` = 'PET27';

UPDATE `coupon` SET `description` = 'Em bé yêu thương - Giảm 32% cho sản phẩm em bé. Điều kiện: Đơn hàng tối thiểu 200,000 VND, giảm tối đa 100,000 VND. Áp dụng cho đồ dùng, quần áo trẻ em.' WHERE `code` = 'BABY32';

-- More fixed discount coupons with specific descriptions
UPDATE `coupon` SET `description` = 'Tiết kiệm 350,000 VND cho đơn hàng cao cấp. Điều kiện: Đơn hàng tối thiểu 1,000,000 VND. Ưu đãi đặc biệt cho sản phẩm cao cấp.' WHERE `code` = 'SAVE350K';

UPDATE `coupon` SET `description` = 'Tiết kiệm 2,000,000 VND - Ưu đãi lớn. Điều kiện: Đơn hàng tối thiểu 1,000,000 VND. Dành cho khách hàng doanh nghiệp và đơn hàng số lượng lớn.' WHERE `code` = 'SAVE2M';

UPDATE `coupon` SET `description` = 'Tiết kiệm 5,000,000 VND - Ưu đãi khủng. Điều kiện: Đơn hàng tối thiểu 1,000,000 VND. Chương trình đặc biệt cho đối tác chiến lược.' WHERE `code` = 'SAVE5M';

-- Small value coupons
UPDATE `coupon` SET `description` = 'Tiết kiệm 20,000 VND cho lần mua đầu tiên. Điều kiện: Đơn hàng tối thiểu 100,000 VND. Chào mừng khách hàng mới, chỉ sử dụng 1 lần.' WHERE `code` = 'SAVE20K';

UPDATE `coupon` SET `description` = 'Tiết kiệm 15,000 VND cho đơn hàng nhỏ. Điều kiện: Đơn hàng tối thiểu 100,000 VND. Phù hợp cho việc mua sắm hàng ngày.' WHERE `code` = 'SAVE15K';

UPDATE `coupon` SET `description` = 'Tiết kiệm 10,000 VND cho mọi đơn hàng. Điều kiện: Đơn hàng tối thiểu 100,000 VND. Voucher cơ bản cho tất cả khách hàng.' WHERE `code` = 'SAVE10K';

UPDATE `coupon` SET `description` = 'Tiết kiệm 5,000 VND - Ưu đãi nhỏ. Điều kiện: Đơn hàng tối thiểu 100,000 VND. Voucher nhỏ để khuyến khích mua hàng.' WHERE `code` = 'SAVE5K'; 