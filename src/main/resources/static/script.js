// API endpoints
const API_BASE = '/api';
const ENDPOINTS = {
    coupons: `${API_BASE}/coupons`,
    orders: `${API_BASE}/orders/apply-coupon`
};

// HTML Sanitization utility - CRITICAL for XSS prevention
function sanitizeHtml(str) {
    if (!str) return '';
    
    const temp = document.createElement('div');
    temp.textContent = str;
    return temp.innerHTML;
}

function sanitizeAndTruncate(str, maxLength = 200) {
    if (!str) return '';
    
    // First sanitize to prevent XSS
    const sanitized = sanitizeHtml(str);
    
    // Then truncate if needed
    if (sanitized.length > maxLength) {
        return sanitized.substring(0, maxLength) + '...';
    }
    
    return sanitized;
}

// Validate input to prevent malicious content
function validateInput(input, fieldName, maxLength = 500) {
    if (!input) return '';
    
    // Check for common XSS patterns
    const xssPatterns = [
        /<script\b[^<]*(?:(?!<\/script>)<[^<]*)*<\/script>/gi,
        /<iframe\b[^<]*(?:(?!<\/iframe>)<[^<]*)*<\/iframe>/gi,
        /javascript:/gi,
        /on\w+\s*=/gi,
        /<object\b[^<]*(?:(?!<\/object>)<[^<]*)*<\/object>/gi,
        /<embed\b[^<]*(?:(?!<\/embed>)<[^<]*)*<\/embed>/gi
    ];
    
    for (const pattern of xssPatterns) {
        if (pattern.test(input)) {
            throw new Error(`Nội dung ${fieldName} chứa mã không an toàn!`);
        }
    }
    
    if (input.length > maxLength) {
        throw new Error(`${fieldName} không được vượt quá ${maxLength} ký tự!`);
    }
    
    return sanitizeHtml(input);
}

// Global state
let allCoupons = [];
let filteredCoupons = [];
let paginationData = {
    currentPage: 0,
    pageSize: 10,
    totalPages: 0,
    totalElements: 0,
    first: true,
    last: true,
    hasNext: false,
    hasPrevious: false
};

// DOM elements
const elements = {
    tabs: document.querySelectorAll('.tab-btn'),
    tabContents: document.querySelectorAll('.tab-content'),
    searchInput: document.getElementById('searchInput'),
    couponGrid: document.getElementById('couponGrid'),
    editForm: document.getElementById('editCouponForm'),
    orderForm: document.getElementById('orderForm'),
    orderResult: document.getElementById('orderResult'),
    modal: document.getElementById('couponModal'),
    loadingOverlay: document.getElementById('loadingOverlay'),
    toastContainer: document.getElementById('toastContainer'),
    paginationContainer: document.getElementById('paginationContainer'),
    paginationInfo: document.getElementById('paginationInfo'),
    paginationPages: document.getElementById('paginationPages'),
    firstPageBtn: document.getElementById('firstPageBtn'),
    prevPageBtn: document.getElementById('prevPageBtn'),
    nextPageBtn: document.getElementById('nextPageBtn'),
    lastPageBtn: document.getElementById('lastPageBtn'),
    pageSize: document.getElementById('pageSize')
};

// Clean URL from unwanted query parameters
function cleanURL() {
    if (window.location.search) {
        console.log('Cleaning URL query parameters:', window.location.search);
        // Remove all query parameters from URL without page reload
        const url = window.location.protocol + "//" + window.location.host + window.location.pathname;
        window.history.replaceState({ path: url }, '', url);
    }
}

// Initialize app
document.addEventListener('DOMContentLoaded', function() {
    cleanURL(); // Clean URL first
    initializeTabs();
    initializeEventListeners();
    loadCoupons();
    setDefaultDateTime();
});

// Tab functionality
function initializeTabs() {
    elements.tabs.forEach(tab => {
        tab.addEventListener('click', function() {
            const targetTab = this.dataset.tab;
            switchTab(targetTab);
        });
    });
}

function switchTab(tabName) {
    // Update tab buttons
    elements.tabs.forEach(tab => {
        tab.classList.toggle('active', tab.dataset.tab === tabName);
    });
    
    // Update tab contents
    elements.tabContents.forEach(content => {
        content.classList.toggle('active', content.id === tabName);
    });
    
    // Load data if switching to coupons tab
    if (tabName === 'coupons') {
        loadCoupons();
    }
}

// Event listeners
function initializeEventListeners() {
    // Search functionality
    elements.searchInput.addEventListener('input', debounce(handleSearch, 300));
    
    // Form submissions
    elements.editForm.addEventListener('submit', handleEditCoupon);
    elements.orderForm.addEventListener('submit', handleOrderCalculation);
    
    // Global form submission prevention - prevent any form from submitting normally
    document.addEventListener('submit', function(e) {
        console.log('Form submission attempted:', e.target.id);
        // Allow only specific forms that we handle with JavaScript
        if (e.target.id !== 'editCouponForm' && e.target.id !== 'orderForm') {
            console.log('Preventing form submission for:', e.target.id);
            e.preventDefault();
            return false;
        }
    });
    
    // Modal functionality
    document.querySelector('.close').addEventListener('click', closeCouponModal);
    elements.modal.addEventListener('click', function(e) {
        if (e.target === elements.modal) {
            closeCouponModal();
        }
    });
    
    // Escape key to close modal
    document.addEventListener('keydown', function(e) {
        if (e.key === 'Escape') {
            closeCouponModal();
        }
    });
    
    // Handle browser back/forward navigation
    window.addEventListener('popstate', function(e) {
        console.log('Popstate event triggered');
        cleanURL();
    });
    
    // Pagination event listeners
    elements.firstPageBtn.addEventListener('click', () => goToPage(0));
    elements.prevPageBtn.addEventListener('click', () => goToPage(paginationData.currentPage - 1));
    elements.nextPageBtn.addEventListener('click', () => goToPage(paginationData.currentPage + 1));
    elements.lastPageBtn.addEventListener('click', () => goToPage(paginationData.totalPages - 1));
    elements.pageSize.addEventListener('change', (e) => {
        paginationData.pageSize = parseInt(e.target.value);
        paginationData.currentPage = 0; // Reset to first page when changing page size
        loadCoupons();
    });
}

// Set default date/time values
function setDefaultDateTime() {
    const now = new Date();
    
    // Set default order date to now
    const orderDateElement = document.getElementById('orderDate');
    if (orderDateElement) {
        orderDateElement.value = formatDateTimeLocal(now);
    }
}

// Utility functions
function extractMessage(response, defaultMessage = '') {
    // Lấy message từ response theo priority
    return response?.message || response?.data?.message || defaultMessage;
}

function extractData(response) {
    // Lấy data từ response, ưu tiên data field trước
    return response?.data !== undefined ? response.data : response;
}

function formatDateTimeLocal(date) {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    
    return `${year}-${month}-${day}T${hours}:${minutes}`;
}

function formatCurrency(amount) {
    return new Intl.NumberFormat('vi-VN', {
        style: 'currency',
        currency: 'VND'
    }).format(amount);
}

function formatDateTime(dateString) {
    return new Date(dateString).toLocaleString('vi-VN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
    });
}

function debounce(func, wait) {
    let timeout;
    return function executedFunction(...args) {
        const later = () => {
            clearTimeout(timeout);
            func(...args);
        };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
    };
}

// API functions
async function apiCall(url, options = {}) {
    showLoading();
    try {
        const response = await fetch(url, {
            headers: {
                'Content-Type': 'application/json',
                ...options.headers
            },
            ...options
        });
        
        const data = await response.json();
        
        if (!response.ok) {
            const errorMessage = data.message || `HTTP error! status: ${response.status}`;
            throw new Error(errorMessage);
        }
        
        if (data.message) {
            console.log('API Response Message:', data.message);
        }
        
        return data;
    } catch (error) {
        console.error('API call failed:', error);
        throw error;
    } finally {
        hideLoading();
    }
}

// Coupon management
async function loadCoupons() {
    try {
        showLoading();
        
        const url = `${ENDPOINTS.coupons}/paged?page=${paginationData.currentPage}&size=${paginationData.pageSize}&sortBy=id&sortDir=asc`;
        const response = await apiCall(url);
        
        if (response.message) {
            console.log('Load Coupons Message:', response.message);
        }
        
        const pagedResponse = response.data || response;
        allCoupons = pagedResponse.content || [];
        filteredCoupons = allCoupons;
        
        paginationData = {
            currentPage: pagedResponse.page || 0,
            pageSize: pagedResponse.size || 10,
            totalPages: pagedResponse.totalPages || 0,
            totalElements: pagedResponse.totalElements || 0,
            first: pagedResponse.first !== false,
            last: pagedResponse.last !== false,
            hasNext: pagedResponse.hasNext === true,
            hasPrevious: pagedResponse.hasPrevious === true
        };
        
        renderCoupons();
        renderPagination();
        hideLoading();
    } catch (error) {
        console.error('Failed to load coupons:', error);
        renderEmptyState('Không thể tải danh sách coupon: ' + error.message);
        hidePagination();
        hideLoading();
    }
}

function renderCoupons() {
    if (filteredCoupons.length === 0) {
        renderEmptyState('Không tìm thấy coupon nào');
        return;
    }
    
    elements.couponGrid.innerHTML = filteredCoupons.map(coupon => `
        <div class="coupon-card" onclick="openCouponModal('${sanitizeHtml(coupon.code)}')">
            <div class="coupon-header">
                <div>
                    <div class="coupon-code">${sanitizeHtml(coupon.code)}</div>
                    <div class="coupon-title">${sanitizeAndTruncate(coupon.title || 'Không có tiêu đề', 100)}</div>
                </div>
                <div class="coupon-status ${getCouponStatusClass(coupon)}">
                    ${getCouponStatusText(coupon)}
                </div>
            </div>
            
            <div class="coupon-info">
                <div class="coupon-type">
                    ${coupon.discountType === 'PERCENTAGE_DISCOUNT' ? 'Giảm theo %' : 'Giảm cố định'}
                </div>
                <div class="coupon-value">
                    ${formatDiscountValue(coupon.value, coupon.discountType)}
                </div>     
                <div class="coupon-dates">
                    <strong>Từ:</strong> ${formatDateTime(coupon.startDate)}<br>
                    <strong>Đến:</strong> ${formatDateTime(coupon.expireDate)}
                </div>
            </div>
        </div>
    `).join('');
}

function renderEmptyState(message) {
    elements.couponGrid.innerHTML = `
        <div class="empty-state">
            <div>📋</div>
            <h3>Trống</h3>
            <p>${message}</p>
        </div>
    `;
}

function renderPagination() {
    if (paginationData.totalElements === 0) {
        hidePagination();
        return;
    }
    
    showPagination();
    
    // Update pagination info
    const startItem = paginationData.currentPage * paginationData.pageSize + 1;
    const endItem = Math.min((paginationData.currentPage + 1) * paginationData.pageSize, paginationData.totalElements);
    elements.paginationInfo.textContent = `Hiển thị ${startItem} - ${endItem} trong tổng số ${paginationData.totalElements} coupon`;
    
    // Update navigation buttons
    elements.firstPageBtn.disabled = paginationData.first;
    elements.prevPageBtn.disabled = !paginationData.hasPrevious;
    elements.nextPageBtn.disabled = !paginationData.hasNext;
    elements.lastPageBtn.disabled = paginationData.last;
    
    // Update page size selector
    elements.pageSize.value = paginationData.pageSize;
    
    // Render page numbers
    renderPageNumbers();
}

function renderPageNumbers() {
    const maxVisiblePages = 5;
    let startPage = Math.max(0, paginationData.currentPage - Math.floor(maxVisiblePages / 2));
    let endPage = Math.min(paginationData.totalPages - 1, startPage + maxVisiblePages - 1);
    
    // Adjust start page if we're near the end
    if (endPage - startPage < maxVisiblePages - 1) {
        startPage = Math.max(0, endPage - maxVisiblePages + 1);
    }
    
    let pagesHtml = '';
    
    for (let i = startPage; i <= endPage; i++) {
        const isActive = i === paginationData.currentPage;
        pagesHtml += `
            <button class="page-btn ${isActive ? 'active' : ''}" 
                    onclick="goToPage(${i})"
                    ${isActive ? 'disabled' : ''}>
                ${i + 1}
            </button>
        `;
    }
    
    elements.paginationPages.innerHTML = pagesHtml;
}

function showPagination() {
    elements.paginationContainer.style.display = 'flex';
}

function hidePagination() {
    elements.paginationContainer.style.display = 'none';
}

function goToPage(page) {
    if (page >= 0 && page < paginationData.totalPages && page !== paginationData.currentPage) {
        paginationData.currentPage = page;
        loadCoupons();
    }
}

function getCouponStatusClass(coupon) {
    const now = new Date();
    const expireDate = new Date(coupon.expireDate);
    
    if (now > expireDate) return 'status-expired';
    if (!coupon.isActive) return 'status-inactive';
    return 'status-active';
}

function getCouponStatusText(coupon) {
    const now = new Date();
    const expireDate = new Date(coupon.expireDate);
    
    if (now > expireDate) return 'Hết hạn';
    if (!coupon.isActive) return 'Không hoạt động';
    return 'Hoạt động';
}

function formatDiscountValue(value, type) {
    if (type === 'PERCENTAGE_DISCOUNT') {
        return `${value}%`;
    }
    return formatCurrency(value);
}

function handleSearch(event) {
    const searchTerm = event.target.value.toLowerCase().trim();
    
    if (searchTerm === '') {
        loadCoupons();
    } else {
        filteredCoupons = allCoupons.filter(coupon => 
            coupon.code.toLowerCase().includes(searchTerm) ||
            (coupon.title && coupon.title.toLowerCase().includes(searchTerm)) ||
            (coupon.description && coupon.description.toLowerCase().includes(searchTerm))
        );
        renderCoupons();
        
        hidePagination();
    }
}

// Modal functionality
async function openCouponModal(couponCode) {
    try {
        const coupon = allCoupons.find(c => c.code === couponCode);
        if (!coupon) {
            return;
        }
        
        document.getElementById('editCouponId').value = coupon.id || '';
        document.getElementById('editCode').value = coupon.code;
        document.getElementById('editTitle').value = coupon.title || '';
        document.getElementById('editDescription').value = coupon.description || '';
        document.getElementById('editStartDate').value = formatDateTimeLocal(new Date(coupon.startDate));
        document.getElementById('editExpireDate').value = formatDateTimeLocal(new Date(coupon.expireDate));
        document.getElementById('editIsActive').checked = coupon.isActive;
        
        document.getElementById('editDiscountType').value = coupon.discountType;
        document.getElementById('editValue').value = coupon.value;
        
        const discountTypeText = coupon.discountType === 'PERCENTAGE_DISCOUNT' ? 'Theo phần trăm (%)' : 'Số tiền cố định (VNĐ)';
        document.getElementById('editDiscountTypeDisplay').textContent = discountTypeText;
        
        const discountValueText = coupon.discountType === 'PERCENTAGE_DISCOUNT' 
            ? `${coupon.value}%` 
            : formatCurrency(coupon.value);
        document.getElementById('editValueDisplay').textContent = discountValueText;
        
        elements.modal.style.display = 'block';
    } catch (error) {
        console.error('Failed to open coupon modal:', error);
    }
}

function closeCouponModal() {
    elements.modal.style.display = 'none';
    
    setTimeout(() => {
        cleanURL();
    }, 100);
}

// Form handlers
async function handleEditCoupon(event) {
    event.preventDefault();
    
    const formData = new FormData(event.target);
    
    try {
        // Validate and sanitize input data
        const couponData = {
            id: formData.get('id'),
            code: validateInput(formData.get('code'), 'Mã giảm giá', 50),
            title: validateInput(formData.get('title'), 'Tiêu đề', 200),
            description: validateInput(formData.get('description'), 'Mô tả', 1000),
            startDate: formData.get('startDate'),
            expireDate: formData.get('expireDate'),
            isActive: formData.get('isActive') === 'on'
        };
        
        // Additional validation
        if (!couponData.code.trim()) {
            throw new Error('Mã giảm giá không được để trống!');
        }
        
        if (!couponData.title.trim()) {
            throw new Error('Tiêu đề không được để trống!');
        }
        
        const result = await apiCall(`${ENDPOINTS.coupons}`, {
            method: 'PATCH',
            body: JSON.stringify(couponData)
        });
        
        // Lấy message từ API response hoặc dùng mặc định
        const successMessage = result.message || 'Cập nhật coupon thành công!';
        showToast(successMessage, 'success');
        closeCouponModal();
        loadCoupons();
        
        // Clean URL in case any query parameters were added
        setTimeout(() => {
            cleanURL();
        }, 100);
        
    } catch (error) {
        console.error('Failed to update coupon:', error);
        showToast(error.message || 'Có lỗi xảy ra khi cập nhật coupon!', 'error');
    }
}

async function handleOrderCalculation(event) {
    event.preventDefault();
    
    const formData = new FormData(event.target);
    const orderData = {
        orderTotalAmount: parseFloat(formData.get('totalAmount')),
        orderDate: formData.get('orderDate'),
        couponCode: formData.get('couponCode') || null
    };
    
    try {
        const result = await apiCall(ENDPOINTS.orders, {
            method: 'POST',
            body: JSON.stringify(orderData)
        });
        
        renderOrderResult(result);
        
        setTimeout(() => {
            cleanURL();
        }, 100);
    } catch (error) {
        console.error('Failed to calculate order:', error);
    }
}

function renderOrderResult(result) {
    const resultHtml = `
        <div class="result-title">
            🧮 Kết quả tính toán
        </div>
        
        ${result.message ? `
            <div class="result-message">
                <div class="message-content">
                    💬 ${result.message}
                </div>
            </div>
        ` : ''}
        
        <div class="result-grid">
            <div class="result-item">
                <span class="result-label">Tổng tiền gốc:</span>
                <span class="result-value">${formatCurrency(result.totalAmount)}</span>
            </div>
            <div class="result-item">
                <span class="result-label">Số tiền giảm:</span>
                <span class="result-value discount">-${formatCurrency(result.discountAmount)}</span>
            </div>
            <div class="result-item">
                <span class="result-label">Số tiền phải trả:</span>
                <span class="result-value final">${formatCurrency(result.finalAmount)}</span>
            </div>
        </div>
        ${result.coupons && result.coupons.length > 0 ? `
            <div class="result-title">
                🎫 Coupon áp dụng
            </div>
            <div class="applied-coupons">
                ${result.coupons.map(coupon => `
                    <div class="applied-coupon-item">
                        <div class="coupon-code-display">${sanitizeHtml(coupon.couponCode)}</div>
                        ${coupon.title ? `<div class="coupon-title-display">${sanitizeAndTruncate(coupon.title, 150)}</div>` : ''}
                        ${coupon.description ? `<div class="coupon-description-display">${sanitizeAndTruncate(coupon.description, 200)}</div>` : ''}
                    </div>
                `).join('')}
            </div>
        ` : ''}
    `;
    
    elements.orderResult.innerHTML = resultHtml;
    elements.orderResult.style.display = 'block';
}

// Loading và Toast functions
function showLoading() {
    elements.loadingOverlay.style.display = 'block';
}

function hideLoading() {
    elements.loadingOverlay.style.display = 'none';
}

function showToast(message, type = 'info') {
    const toast = document.createElement('div');
    toast.className = `toast ${type}`;
    
    const icons = {
        success: '✅',
        error: '❌', 
        warning: '⚠️',
        info: 'ℹ️'
    };
    
    toast.innerHTML = `
        <div class="toast-content">
            <span class="toast-icon">${icons[type] || icons.info}</span>
            <span class="toast-message">${sanitizeHtml(message)}</span>
        </div>
    `;
    
    elements.toastContainer.appendChild(toast);
    
    // Auto remove after 5 seconds
    setTimeout(() => {
        if (toast.parentNode) {
            toast.parentNode.removeChild(toast);
        }
    }, 5000);
}

// Export functions for global access
window.openCouponModal = openCouponModal;
window.closeCouponModal = closeCouponModal; 