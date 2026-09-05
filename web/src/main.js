/**
 * Scanly × TheKubics — Official Website Script
 * Shimmer loader, theme switcher, reveals, counters, interactive filter engine
 */

// ─── SHIMMER LOADER ──────────────────────────────────────
window.addEventListener('load', () => {
  const loader = document.getElementById('loader');
  if (loader) {
    setTimeout(() => {
      loader.classList.add('hide');
    }, 200);
  }
});

// ─── THEME ───────────────────────────────────────────────
const THEME_KEY = 'scanly-theme';

function getSystemTheme() {
  return window.matchMedia('(prefers-color-scheme: light)').matches ? 'light' : 'dark';
}

function applyTheme(choice) {
  const resolved = choice === 'system' ? getSystemTheme() : choice;
  document.documentElement.setAttribute('data-theme', resolved);
  document.querySelector('meta[name="theme-color"]')?.setAttribute('content', resolved === 'dark' ? '#000000' : '#FAFAF9');

  document.querySelectorAll('.theme-btn').forEach(btn => {
    const isActive = btn.dataset.theme === choice;
    btn.setAttribute('aria-pressed', isActive ? 'true' : 'false');
    btn.classList.toggle('active', isActive);
  });
}

function initTheme() {
  const saved = localStorage.getItem(THEME_KEY) || 'dark';
  applyTheme(saved);

  document.querySelectorAll('.theme-btn').forEach(btn => {
    btn.addEventListener('click', () => {
      const choice = btn.dataset.theme;
      localStorage.setItem(THEME_KEY, choice);
      applyTheme(choice);
    });
  });

  window.matchMedia('(prefers-color-scheme: light)').addEventListener('change', () => {
    if (localStorage.getItem(THEME_KEY) === 'system') {
      applyTheme('system');
    }
  });
}

// ─── SCROLL REVEAL ───────────────────────────────────────
function initReveals() {
  const prefersReducedMotion = window.matchMedia('(prefers-reduced-motion: reduce)').matches;

  if (prefersReducedMotion) {
    document.querySelectorAll('.reveal').forEach(el => el.classList.add('revealed'));
    return;
  }

  const observer = new IntersectionObserver((entries) => {
    entries.forEach(entry => {
      if (entry.isIntersecting) {
        entry.target.classList.add('revealed');
        observer.unobserve(entry.target);
      }
    });
  }, { threshold: 0.1, rootMargin: '0px 0px -40px 0px' });

  document.querySelectorAll('.reveal').forEach(el => observer.observe(el));
}

// ─── COUNTER ANIMATION ──────────────────────────────────
function animateCounters() {
  const counters = document.querySelectorAll('[data-target]');

  const observer = new IntersectionObserver((entries) => {
    entries.forEach(entry => {
      if (entry.isIntersecting) {
        const el = entry.target;
        const target = parseInt(el.dataset.target, 10);
        const duration = 1200;
        const start = performance.now();

        function step(now) {
          const progress = Math.min((now - start) / duration, 1);
          const eased = 1 - Math.pow(1 - progress, 3);
          el.textContent = Math.round(eased * target);
          if (progress < 1) requestAnimationFrame(step);
        }

        requestAnimationFrame(step);
        observer.unobserve(el);
      }
    });
  }, { threshold: 0.3 });

  counters.forEach(c => observer.observe(c));
}

// ─── SCROLL PROGRESS & NAV ───────────────────────────────
function initNav() {
  const nav = document.getElementById('nav');
  const toggle = document.querySelector('.mobile-toggle');
  const links = document.getElementById('nav-links');
  const progressFill = document.querySelector('.scroll-progress-fill');

  window.addEventListener('scroll', () => {
    const scrollY = window.scrollY;
    
    // Navbar elevation on scroll
    if (nav) {
      nav.classList.toggle('scrolled', scrollY > 24);
    }

    // Progress bar
    if (progressFill) {
      const docHeight = document.documentElement.scrollHeight - window.innerHeight;
      const pct = docHeight > 0 ? (scrollY / docHeight) * 100 : 0;
      progressFill.style.width = pct + '%';
    }
  }, { passive: true });

  // Mobile navigation toggle
  toggle?.addEventListener('click', () => {
    const isOpen = toggle.getAttribute('aria-expanded') === 'true';
    toggle.setAttribute('aria-expanded', !isOpen);
    links?.classList.toggle('open', !isOpen);
    document.body.style.overflow = !isOpen ? 'hidden' : '';
  });

  // Close mobile navigation on link click
  links?.querySelectorAll('a').forEach(a => {
    a.addEventListener('click', () => {
      toggle?.setAttribute('aria-expanded', 'false');
      links.classList.remove('open');
      document.body.style.overflow = '';
    });
  });

  // Highlight active section on scroll
  const sections = document.querySelectorAll('section[id]');
  const navAnchors = links?.querySelectorAll('a[href^="#"]');

  const sectionObserver = new IntersectionObserver((entries) => {
    entries.forEach(entry => {
      if (entry.isIntersecting) {
        const id = entry.target.id;
        navAnchors?.forEach(link => {
          link.classList.toggle('active', link.getAttribute('href') === '#' + id);
        });
      }
    });
  }, { threshold: 0.25, rootMargin: '-60px 0px -40% 0px' });

  sections.forEach(s => sectionObserver.observe(s));
}

// ─── INTERACTIVE FILTER & ADJUSTMENT ENGINE ──────────────
function initEnhanceDemo() {
  const modePills = document.querySelectorAll('.mode-pill');
  const doc = document.getElementById('interactiveDoc');
  const brightnessSlider = document.getElementById('brightnessSlider');
  const contrastSlider = document.getElementById('contrastSlider');
  const brightnessVal = document.getElementById('brightnessVal');
  const contrastVal = document.getElementById('contrastVal');
  const resetBtn = document.getElementById('resetFiltersBtn');

  let currentMode = 'original';

  function applyFilters() {
    if (!doc) return;
    const b = parseInt(brightnessSlider?.value || 0, 10);
    const c = parseInt(contrastSlider?.value || 0, 10);

    const bPct = 100 + b;
    const cPct = 100 + c * 1.5;

    let filterStr = `brightness(${bPct}%) contrast(${cPct}%)`;

    switch (currentMode) {
      case 'auto':
        filterStr += ' saturate(130%) contrast(115%)';
        break;
      case 'grayscale':
        filterStr += ' grayscale(100%) contrast(120%)';
        break;
      case 'bw':
        filterStr += ' grayscale(100%) contrast(250%) brightness(110%)';
        break;
      case 'contrast':
        filterStr += ' contrast(190%)';
        break;
      case 'original':
      default:
        break;
    }

    doc.style.filter = filterStr;
  }

  modePills.forEach(pill => {
    pill.addEventListener('click', () => {
      modePills.forEach(p => p.classList.remove('active'));
      pill.classList.add('active');
      currentMode = pill.dataset.mode;
      applyFilters();
    });
  });

  brightnessSlider?.addEventListener('input', () => {
    if (brightnessVal) brightnessVal.textContent = brightnessSlider.value;
    applyFilters();
  });

  contrastSlider?.addEventListener('input', () => {
    if (contrastVal) contrastVal.textContent = contrastSlider.value;
    applyFilters();
  });

  resetBtn?.addEventListener('click', () => {
    if (brightnessSlider) brightnessSlider.value = 0;
    if (contrastSlider) contrastSlider.value = 0;
    if (brightnessVal) brightnessVal.textContent = '0';
    if (contrastVal) contrastVal.textContent = '0';
    modePills.forEach(p => p.classList.toggle('active', p.dataset.mode === 'original'));
    currentMode = 'original';
    applyFilters();
  });
}

// ─── COMPOSER INTERACTION ────────────────────────────────
function initComposerDemo() {
  const pages = document.querySelectorAll('.composer-page');
  const footerIndicator = document.querySelector('.composer-footer span');
  const pageTitles = [
    'PAGE 01 OF 04 • A4 PORTRAIT • 300 DPI (COVER SHEET)',
    'PAGE 02 OF 04 • A4 PORTRAIT • 300 DPI (EXECUTIVE SUMMARY)',
    'PAGE 03 OF 04 • A4 PORTRAIT • 300 DPI (FINANCIAL STATEMENTS)',
    'PAGE 04 OF 04 • A4 PORTRAIT • 300 DPI (APPENDIX & NOTES)'
  ];

  pages.forEach((page, index) => {
    page.addEventListener('click', () => {
      pages.forEach(p => p.classList.remove('active'));
      page.classList.add('active');
      if (footerIndicator && pageTitles[index]) {
        footerIndicator.textContent = pageTitles[index];
      }
    });
  });
}

// ─── SMOOTH SCROLL ───────────────────────────────────────
function initSmoothScroll() {
  document.querySelectorAll('a[href^="#"]').forEach(a => {
    a.addEventListener('click', (e) => {
      const href = a.getAttribute('href');
      if (href === '#' || href === '') return;
      const target = document.querySelector(href);
      if (target) {
        e.preventDefault();
        const offset = 85;
        const top = target.getBoundingClientRect().top + window.scrollY - offset;
        window.scrollTo({ top, behavior: 'smooth' });
        history.replaceState(null, '', href);
      }
    });
  });
}

// ─── INITIALIZATION ──────────────────────────────────────
document.addEventListener('DOMContentLoaded', () => {
  initTheme();
  initReveals();
  animateCounters();
  initNav();
  initEnhanceDemo();
  initComposerDemo();
  initSmoothScroll();
});
