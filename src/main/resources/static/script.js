// API endpoints
const API_BASE = '/api';
const ENDPOINTS = {
    coupons: `${API_BASE}/coupons`,
    orders: `${API_BASE}/orders/apply-coupon`
};

// Global state
let allCoupons = [];
let filteredCoupons = [];

// DOM elements
const elements = {
    tabs: document.querySelectorAll('.tab-btn'),
    tabContents: document.querySelectorAll('.tab-content'),
    searchInput: document.getElementById('searchInput'),
    couponGrid: document.getElementById('couponGrid'),
    createForm: document.getElementById('createCouponForm'),
    editForm: document.getElementById('editCouponForm'),
    orderForm: document.getElementById('orderForm'),
    orderResult: document.getElementById('orderResult'),
    modal: document.getElementById('couponModal'),
    loadingOverlay: document.getElementById('loadingOverlay'),
    toastContainer: document.getElementById('toastContainer')
};

// Initialize app
document.addEventListener('DOMContentLoaded', function() {
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
    elements.createForm.addEventListener('submit', handleCreateCoupon);
    elements.editForm.addEventListener('submit', handleEditCoupon);
    elements.orderForm.addEventListener('submit', handleOrderCalculation);
    
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
}

// Set default date/time values
function setDefaultDateTime() {
    const now = new Date();
    const nextWeek = new Date(now.getTime() + 7 * 24 * 60 * 60 * 1000);
    
    // Set default start date to now
    document.getElementById('startDate').value = formatDateTimeLocal(now);
    document.getElementById('orderDate').value = formatDateTimeLocal(now);
    
    // Set default expire date to one week from now
    document.getElementById('expireDate').value = formatDateTimeLocal(nextWeek);
}

// Utility functions
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
        
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        return await response.json();
    } catch (error) {
        console.error('API call failed:', error);
        showToast('Có lỗi xảy ra khi gọi API: ' + error.message, 'error');
        throw error;
    } finally {
        hideLoading();
    }
}

// Coupon management
async function loadCoupons() {
    try {
        const coupons = await apiCall(ENDPOINTS.coupons);
        allCoupons = coupons;
        filteredCoupons = coupons;
        renderCoupons();
    } catch (error) {
        console.error('Failed to load coupons:', error);
        renderEmptyState('Không thể tải danh sách coupon');
    }
}

function renderCoupons() {
    if (filteredCoupons.length === 0) {
        renderEmptyState('Không tìm thấy coupon nào');
        return;
    }
    
    elements.couponGrid.innerHTML = filteredCoupons.map(coupon => `
        <div class="coupon-card" onclick="openCouponModal('${coupon.code}')">
            <div class="coupon-header">
                <div>
                    <div class="coupon-code">${coupon.code}</div>
                    <div class="coupon-title">${coupon.title || 'Không có tiêu đề'}</div>
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

function getCouponStatusClass(coupon) {
    const now = new Date();
    const startDate = new Date(coupon.startDate);
    const expireDate = new Date(coupon.expireDate);
    
    if (now > expireDate) return 'status-expired';
    if (!coupon.isActive) return 'status-inactive';
    return 'status-active';
}

function getCouponStatusText(coupon) {
    const now = new Date();
    const startDate = new Date(coupon.startDate);
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

// Search functionality
function handleSearch(event) {
    const searchTerm = event.target.value.toLowerCase().trim();
    
    if (searchTerm === '') {
        filteredCoupons = allCoupons;
    } else {
        filteredCoupons = allCoupons.filter(coupon => 
            coupon.code.toLowerCase().includes(searchTerm) ||
            (coupon.title && coupon.title.toLowerCase().includes(searchTerm)) ||
            (coupon.description && coupon.description.toLowerCase().includes(searchTerm))
        );
    }
    
    renderCoupons();
}

// Modal functionality
async function openCouponModal(couponCode) {
    try {
        const coupon = allCoupons.find(c => c.code === couponCode);
        if (!coupon) {
            return;
        }
        
        // Populate form fields
        document.getElementById('editCouponId').value = coupon.id || '';
        document.getElementById('editCode').value = coupon.code;
        document.getElementById('editTitle').value = coupon.title || '';
        document.getElementById('editDescription').value = coupon.description || '';
        document.getElementById('editValue').value = coupon.value;
        document.getElementById('editStartDate').value = formatDateTimeLocal(new Date(coupon.startDate));
        document.getElementById('editExpireDate').value = formatDateTimeLocal(new Date(coupon.expireDate));
        document.getElementById('editIsActive').checked = coupon.isActive;
        
        elements.modal.style.display = 'block';
    } catch (error) {
        console.error('Failed to open coupon modal:', error);
    }
}

function closeCouponModal() {
    elements.modal.style.display = 'none';
}

// Form handlers
async function handleCreateCoupon(event) {
    event.preventDefault();
    
    const formData = new FormData(event.target);
    const couponData = {
        code: formData.get('code'),
        title: formData.get('title'),
        description: formData.get('description'),
        discountType: formData.get('discountType'),
        value: parseFloat(formData.get('value')),
        startDate: formData.get('startDate'),
        expireDate: formData.get('expireDate'),
        isActive: formData.get('isActive') === 'on'
    };
    
    try {
        await apiCall(ENDPOINTS.coupons, {
            method: 'POST',
            body: JSON.stringify(couponData)
        });
        
        event.target.reset();
        setDefaultDateTime();
        loadCoupons();
        switchTab('coupons');
    } catch (error) {
        console.error('Failed to create coupon:', error);
    }
}

async function handleEditCoupon(event) {
    event.preventDefault();
    
    const formData = new FormData(event.target);
    const couponData = {
        id: formData.get('id'),
        code: formData.get('code'),
        title: formData.get('title'),
        description: formData.get('description'),
        startDate: formData.get('startDate'),
        expireDate: formData.get('expireDate'),
        isActive: formData.get('isActive') === 'on'
    };
    
    try {
        await apiCall(`${ENDPOINTS.coupons}/${couponData.code}`, {
            method: 'PATCH',
            body: JSON.stringify(couponData)
        });
        
        closeCouponModal();
        loadCoupons();
    } catch (error) {
        console.error('Failed to update coupon:', error);
    }
}

async function handleOrderCalculation(event) {
    event.preventDefault();
    
    const formData = new FormData(event.target);
    const orderData = {
        totalAmount: parseFloat(formData.get('totalAmount')),
        orderDate: formData.get('orderDate'),
        couponCode: formData.get('couponCode') || null
    };
    
    try {
        const result = await apiCall(ENDPOINTS.orders, {
            method: 'POST',
            body: JSON.stringify(orderData)
        });
        
        renderOrderResult(result);
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
                        <div class="coupon-code-display">${coupon.couponCode}</div>
                        ${coupon.title ? `<div class="coupon-title-display">${coupon.title}</div>` : ''}
                        ${coupon.description ? `<div class="coupon-description-display">${coupon.description}</div>` : ''}
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
            <span class="toast-message">${message}</span>
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