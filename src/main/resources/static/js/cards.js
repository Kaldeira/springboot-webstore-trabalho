
(function () {
    var _state = new WeakMap();

    function _slides(card) {
        return card.querySelectorAll('.card-img-slide');
    }
    function _dots(card) {
        return card.querySelectorAll('.card-dot');
    }

    function _goTo(card, idx) {
        var slides = _slides(card);
        var dots   = _dots(card);
        if (!slides.length) return;

        var n = slides.length;
        idx = ((idx % n) + n) % n;
        _state.set(card, idx);

        slides.forEach(function (s, i) { s.classList.toggle('active', i === idx); });
        dots.forEach(function (d, i)   { d.classList.toggle('active', i === idx); });
    }

    window.cardNav = function (e, card, dir) {
        e.preventDefault();
        e.stopPropagation();
        var cur = _state.get(card) || 0;
        _goTo(card, cur + dir);
    };

    document.addEventListener('DOMContentLoaded', function () {
        document.querySelectorAll('.product-card').forEach(function (card) {
            _state.set(card, 0);

            var startX = null;
            var wrap = card.querySelector('.product-img-wrap');
            if (!wrap) return;

            wrap.addEventListener('touchstart', function (e) {
                startX = e.touches[0].clientX;
            }, { passive: true });

            wrap.addEventListener('touchend', function (e) {
                if (startX === null) return;
                var dx = e.changedTouches[0].clientX - startX;
                if (Math.abs(dx) > 40) {
                    var cur = _state.get(card) || 0;
                    _goTo(card, cur + (dx < 0 ? 1 : -1));
                }
                startX = null;
            }, { passive: true });
        });
    });
})();
