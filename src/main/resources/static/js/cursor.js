const cursor = document.getElementById('cursor');
document.addEventListener('mousemove', e => {
    cursor.style.left = e.clientX + 'px';
    cursor.style.top = e.clientY + 'px';
});

function bindCursor() {
    document.querySelectorAll('a, button, .product-card, .category-card, .qty-btn, .cart-close, .variante-opt').forEach(el => {
        el.addEventListener('mouseenter', () => cursor.classList.add('big'));
        el.addEventListener('mouseleave', () => cursor.classList.remove('big'));
    });
}

bindCursor();

// ── Scroll reveal ──
const observer = new IntersectionObserver(entries => {
    entries.forEach(e => {
        if (e.isIntersecting) e.target.classList.add('visible');
    });
}, {threshold: 0.1});
document.querySelectorAll('.reveal').forEach(el => observer.observe(el));