const CART_KEY = 'nemesis_cart_v1';

const $ = (s) => document.querySelector(s);
const $$ = (s) => document.querySelectorAll(s);

const getCart = () =>
    JSON.parse(localStorage.getItem(CART_KEY) || '[]');

const saveCart = (cart) =>
    localStorage.setItem(CART_KEY, JSON.stringify(cart));

const money = (v) =>
    'R$ ' + Number(v).toFixed(2).replace('.', ',');

const esc = (s) =>
    String(s)
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;');

function atualizarContador() {
    const total = getCart().reduce((s, i) => s + i.quantidade, 0);

    $$('#cart-count').forEach(el => {
        el.textContent = total;
    });
}

function renderizarCarrinho() {
    const cart = getCart();

    const body = $('#cart-body');
    const foot = $('#cart-foot');

    if (!body) return;

    if (!cart.length) {
        body.innerHTML = `
            <div class="cart-empty">
                <div class="cart-empty-icon">
                    <i class="fa-solid fa-cart-shopping"></i>
                </div>
                <div class="cart-empty-text">
                    Seu carrinho está vazio
                </div>
            </div>
        `;

        if (foot) foot.style.display = 'none';
        return;
    }

    if (foot) foot.style.display = '';

    let subtotal = 0;

    body.innerHTML = cart.map((item, idx) => {

        subtotal += item.preco * item.quantidade;

        return `
            <div class="cart-item">

                <div class="cart-item-img">
                    ${
            item.img
                ? `<img src="${esc(item.img)}" alt="${esc(item.nome)}">`
                : `<div class="no-img">${esc(item.nome.slice(0,3).toUpperCase())}</div>`
        }
                </div>

                <div class="cart-item-info">

                    <div class="cart-item-nome">
                        ${esc(item.nome)}
                    </div>

                    <div class="cart-item-variante">
                        ${esc(item.variante)}
                    </div>

                    <div class="cart-item-qty">
                        <button class="qty-btn" onclick="alterarQtd(${idx}, -1)">−</button>

                        <div class="qty-num">
                            ${item.quantidade}
                        </div>

                        <button class="qty-btn" onclick="alterarQtd(${idx}, 1)">+</button>
                    </div>

                    <button class="cart-item-rm" onclick="removerItem(${idx})">
                        <i class="fa-solid fa-xmark"></i>
                        Remover
                    </button>

                </div>

                <div class="cart-item-preco">
                    ${money(item.preco * item.quantidade)}
                </div>

            </div>
        `;
    }).join('');

    $('#cart-total-val').textContent = money(subtotal);

    if (typeof bindCursor === 'function') {
        bindCursor();
    }
}

function alterarQtd(idx, delta) {
    const cart = getCart();

    if (!cart[idx]) return;

    cart[idx].quantidade = Math.max(
        1,
        cart[idx].quantidade + delta
    );

    saveCart(cart);
    atualizarContador();
    renderizarCarrinho();
}

function removerItem(idx) {
    const cart = getCart();

    cart.splice(idx, 1);

    saveCart(cart);
    atualizarContador();
    renderizarCarrinho();
}

function abrirCarrinho() {
    renderizarCarrinho();

    $('#cart-overlay')?.classList.add('open');
    $('#cart-drawer')?.classList.add('open');
}

function fecharCarrinho() {
    $('#cart-overlay')?.classList.remove('open');
    $('#cart-drawer')?.classList.remove('open');
}

function adicionarAoCarrinho(produto) {

    const cart = getCart();

    const existing = cart.find(i => i.key === produto.key);

    if (existing) {
        existing.quantidade++;
    } else {
        cart.push({
            ...produto,
            quantidade: 1
        });
    }

    saveCart(cart);

    atualizarContador();
    abrirCarrinho();
}

function inicializarCarrinho() {

    atualizarContador();
    renderizarCarrinho();

    $('#cart-btn-nav')?.addEventListener(
        'click',
        abrirCarrinho
    );

    $('#quick-modal')?.addEventListener('click', function(e) {
        if (e.target === this) {
            this.classList.remove('open');
        }
    });
}

document.addEventListener(
    'DOMContentLoaded',
    inicializarCarrinho
);