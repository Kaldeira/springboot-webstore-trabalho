const filtrosTamanho =
    document.querySelectorAll('.filter-tamanho');

const filtrosColecao =
    document.querySelectorAll('.filter-colecao');

const produtos =
    document.querySelectorAll('.product-card');

function aplicarFiltros() {

    const tamanhosSelecionados =
        [...filtrosTamanho]
            .filter(c => c.checked)
            .map(c => c.value);

    const colecoesSelecionadas =
        [...filtrosColecao]
            .filter(c => c.checked)
            .map(c => c.value);

    let visiveis = 0;

    produtos.forEach(produto => {

        const tamanhos =
            produto.dataset.tamanhos
                ? produto.dataset.tamanhos.split(',')
                : [];

        const colecao =
            produto.dataset.colecao;

        const matchTamanho =
            tamanhosSelecionados.length === 0
            || tamanhosSelecionados.some(t =>
                tamanhos.includes(t)
            );

        const matchColecao =
            colecoesSelecionadas.length === 0
            || colecoesSelecionadas.includes(colecao);

        if (matchTamanho && matchColecao) {

            produto.style.display = '';
            visiveis++;

        } else {

            produto.style.display = 'none';

        }

    });

    const contador =
        document.querySelector('.categoria-count span');

    if (contador) {
        contador.textContent =
            visiveis + ' produtos';
    }
}

filtrosTamanho.forEach(el => {
    el.addEventListener('change', aplicarFiltros);
});

filtrosColecao.forEach(el => {
    el.addEventListener('change', aplicarFiltros);
});

function toggleGroup(titleEl) {
    titleEl.closest('.filter-group').classList.toggle('collapsed');
}

function closeSidebar() {
    document.querySelector('.filters-sidebar').classList.remove('open');
}
function openSidebar() {
    document.querySelector('.filters-sidebar').classList.add('open');
}