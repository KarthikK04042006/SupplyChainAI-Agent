/* ============================================================
   Supply Chain AI Agent - Main JavaScript
   Charts, interactions, search, dark mode, sidebar
   ============================================================ */

(function () {
    'use strict';

    /* ------------------------------------------------------------------ */
    /*  Theme                                                               */
    /* ------------------------------------------------------------------ */
    function applyTheme(theme) {
        document.documentElement.setAttribute('data-theme', theme);
        var icon = document.getElementById('themeIcon');
        if (!icon) return;
        if (theme === 'dark') {
            icon.classList.replace('fa-moon', 'fa-sun');
        } else {
            icon.classList.replace('fa-sun', 'fa-moon');
        }
    }

    var savedTheme = localStorage.getItem('theme') || 'light';
    applyTheme(savedTheme);

    document.addEventListener('DOMContentLoaded', function () {

        /* --- Theme Toggle --- */
        var themeBtn = document.getElementById('themeToggle');
        if (themeBtn) {
            themeBtn.addEventListener('click', function () {
                var current = document.documentElement.getAttribute('data-theme');
                var next = current === 'dark' ? 'light' : 'dark';
                applyTheme(next);
                localStorage.setItem('theme', next);
            });
        }

        /* ------------------------------------------------------------------ */
        /*  Sidebar Toggle                                                      */
        /* ------------------------------------------------------------------ */
        var wrapper   = document.querySelector('.app-wrapper');
        var sidebarBtn = document.getElementById('sidebarToggle');
        var sidebar   = document.getElementById('sidebar');
        var isMobile  = function () { return window.innerWidth <= 768; };

        if (sidebarBtn && wrapper) {
            sidebarBtn.addEventListener('click', function () {
                if (isMobile()) {
                    sidebar && sidebar.classList.toggle('mobile-open');
                } else {
                    wrapper.classList.toggle('sidebar-collapsed');
                    localStorage.setItem('sidebarCollapsed', wrapper.classList.contains('sidebar-collapsed'));
                }
            });

            // Restore sidebar state
            if (!isMobile() && localStorage.getItem('sidebarCollapsed') === 'true') {
                wrapper.classList.add('sidebar-collapsed');
            }

            // Close sidebar on mobile overlay click
            document.addEventListener('click', function (e) {
                if (isMobile() && sidebar && sidebar.classList.contains('mobile-open')) {
                    if (!sidebar.contains(e.target) && e.target !== sidebarBtn) {
                        sidebar.classList.remove('mobile-open');
                    }
                }
            });
        }

        /* ------------------------------------------------------------------ */
        /*  Active Sidebar Link                                                 */
        /* ------------------------------------------------------------------ */
        if (typeof currentPage !== 'undefined') {
            document.querySelectorAll('.sidebar-link').forEach(function (link) {
                var href = link.getAttribute('href') || '';
                if (href.includes('/' + currentPage)) {
                    link.classList.add('active');
                }
            });
        }

        /* ------------------------------------------------------------------ */
        /*  Notification Dropdown                                               */
        /* ------------------------------------------------------------------ */
        var notifBtn      = document.getElementById('notifBtn');
        var notifDropdown = document.getElementById('notifDropdown');
        var markAllRead   = document.getElementById('markAllRead');
        var notifBadge    = document.getElementById('notifBadge');

        if (notifBtn && notifDropdown) {
            notifBtn.addEventListener('click', function (e) {
                e.stopPropagation();
                notifDropdown.classList.toggle('open');
                document.getElementById('userDropdown') && document.getElementById('userDropdown').classList.remove('open');
            });
        }
        if (markAllRead && notifBadge) {
            markAllRead.addEventListener('click', function () {
                document.querySelectorAll('.notif-item.unread').forEach(function (el) {
                    el.classList.remove('unread');
                });
                notifBadge.style.display = 'none';
            });
        }

        /* ------------------------------------------------------------------ */
        /*  User Dropdown                                                       */
        /* ------------------------------------------------------------------ */
        var userMenuBtn  = document.getElementById('userMenuBtn');
        var userDropdown = document.getElementById('userDropdown');
        if (userMenuBtn && userDropdown) {
            userMenuBtn.addEventListener('click', function (e) {
                e.stopPropagation();
                userDropdown.classList.toggle('open');
                notifDropdown && notifDropdown.classList.remove('open');
            });
        }

        /* Close dropdowns on outside click */
        document.addEventListener('click', function () {
            if (notifDropdown) notifDropdown.classList.remove('open');
            if (userDropdown)  userDropdown.classList.remove('open');
        });

        /* ------------------------------------------------------------------ */
        /*  Counter Animation                                                   */
        /* ------------------------------------------------------------------ */
        function animateCounter(el) {
            var target = parseFloat(el.textContent.replace(/[^0-9.]/g, '')) || 0;
            var start  = 0;
            var duration = 1600;
            var startTime = null;

            function step(timestamp) {
                if (!startTime) startTime = timestamp;
                var progress = Math.min((timestamp - startTime) / duration, 1);
                var eased = 1 - Math.pow(1 - progress, 3);
                var current = Math.floor(eased * target);
                el.textContent = current.toLocaleString();
                if (progress < 1) requestAnimationFrame(step);
                else el.textContent = target.toLocaleString();
            }
            requestAnimationFrame(step);
        }

        var counters = document.querySelectorAll('.counter');
        if ('IntersectionObserver' in window) {
            var obs = new IntersectionObserver(function (entries) {
                entries.forEach(function (entry) {
                    if (entry.isIntersecting) {
                        animateCounter(entry.target);
                        obs.unobserve(entry.target);
                    }
                });
            }, { threshold: 0.3 });
            counters.forEach(function (c) { obs.observe(c); });
        } else {
            counters.forEach(animateCounter);
        }

        /* ------------------------------------------------------------------ */
        /*  File Upload (drag & drop + preview)                                 */
        /* ------------------------------------------------------------------ */
        var dropZone   = document.getElementById('dropZone');
        var fileInput  = document.getElementById('fileInput');
        var filePreview = document.getElementById('filePreview');
        var fileName   = document.getElementById('fileName');
        var fileSize   = document.getElementById('fileSize');
        var uploadBtn  = document.getElementById('uploadBtn');
        var clearFile  = document.getElementById('clearFile');
        var uploadForm = document.getElementById('uploadForm');
        var progressWrap = document.getElementById('uploadProgress');
        var progressBar  = document.getElementById('progressBar');
        var progressText = document.getElementById('progressText');
        var progressPct  = document.getElementById('progressPct');

        function showFile(file) {
            if (!file) return;
            if (fileName) fileName.textContent = file.name;
            if (fileSize) fileSize.textContent = (file.size / 1024).toFixed(1) + ' KB (' + (file.size / (1024*1024)).toFixed(2) + ' MB)';
            if (filePreview) filePreview.classList.remove('d-none');
            if (uploadBtn)  uploadBtn.disabled = false;
        }

        if (fileInput) {
            fileInput.addEventListener('change', function () {
                if (this.files[0]) showFile(this.files[0]);
            });
        }
        if (clearFile) {
            clearFile.addEventListener('click', function () {
                if (fileInput) fileInput.value = '';
                if (filePreview) filePreview.classList.add('d-none');
                if (uploadBtn) uploadBtn.disabled = true;
            });
        }
        if (dropZone) {
            ['dragenter','dragover','dragleave','drop'].forEach(function (ev) {
                dropZone.addEventListener(ev, function (e) { e.preventDefault(); e.stopPropagation(); });
            });
            ['dragenter','dragover'].forEach(function (ev) {
                dropZone.addEventListener(ev, function () { dropZone.classList.add('highlight'); });
            });
            ['dragleave','drop'].forEach(function (ev) {
                dropZone.addEventListener(ev, function () { dropZone.classList.remove('highlight'); });
            });
            dropZone.addEventListener('drop', function (e) {
                var files = e.dataTransfer.files;
                if (files.length && fileInput) {
                    fileInput.files = files;
                    showFile(files[0]);
                }
            });
        }

        // Upload progress simulation
        if (uploadForm) {
            uploadForm.addEventListener('submit', function () {
                if (progressWrap) progressWrap.classList.remove('d-none');
                var pct = 0;
                var interval = setInterval(function () {
                    pct = Math.min(pct + Math.random() * 15, 92);
                    if (progressBar) progressBar.style.width = pct + '%';
                    if (progressPct) progressPct.textContent = Math.floor(pct) + '%';
                    if (progressText) progressText.textContent = pct < 40 ? 'Uploading...' : pct < 80 ? 'Processing rows...' : 'Finalizing...';
                }, 400);
                setTimeout(function () { clearInterval(interval); }, 6000);
            });
        }

        /* ------------------------------------------------------------------ */
        /*  Global Search                                                       */
        /* ------------------------------------------------------------------ */
        var globalSearch   = document.getElementById('globalSearch');
        var searchResults  = document.getElementById('searchResults');

        var searchIndex = [
            { label: 'Executive Dashboard', icon: 'fa-th-large', url: '/dashboard' },
            { label: 'Upload Data',          icon: 'fa-cloud-upload-alt', url: '/upload' },
            { label: 'Analytics Center',     icon: 'fa-chart-bar', url: '/analytics' },
            { label: 'AI Predictions',       icon: 'fa-robot', url: '/ai' },
            { label: 'Report Center',        icon: 'fa-file-chart-line', url: '/reports' },
            { label: 'Upload History',       icon: 'fa-history', url: '/history' },
            { label: 'My Profile',           icon: 'fa-user-circle', url: '/profile' },
            { label: 'Settings',             icon: 'fa-cog', url: '/settings' },
            { label: 'Download PDF Report',  icon: 'fa-file-pdf', url: '/reports/pdf' },
            { label: 'Download Excel',       icon: 'fa-file-excel', url: '/reports/excel' },
            { label: 'Admin Panel',          icon: 'fa-user-shield', url: '/admin' }
        ];

        if (globalSearch && searchResults) {
            globalSearch.addEventListener('input', function () {
                var q = this.value.trim().toLowerCase();
                if (q.length < 1) {
                    searchResults.classList.add('d-none');
                    return;
                }
                var hits = searchIndex.filter(function (item) {
                    return item.label.toLowerCase().includes(q);
                });
                if (hits.length === 0) {
                    searchResults.innerHTML = '<div class="search-result-item text-muted"><i class="fas fa-search-minus"></i>No results found</div>';
                } else {
                    searchResults.innerHTML = hits.map(function (h) {
                        return '<a href="' + h.url + '" class="search-result-item">' +
                               '<i class="fas ' + h.icon + '"></i>' + h.label + '</a>';
                    }).join('');
                }
                searchResults.classList.remove('d-none');
            });
            document.addEventListener('click', function (e) {
                if (!globalSearch.contains(e.target) && !searchResults.contains(e.target)) {
                    searchResults.classList.add('d-none');
                }
            });
            globalSearch.addEventListener('keydown', function (e) {
                if (e.key === 'Escape') {
                    searchResults.classList.add('d-none');
                    this.blur();
                }
            });
        }

        /* ------------------------------------------------------------------ */
        /*  Dashboard Charts                                                    */
        /* ------------------------------------------------------------------ */
        var COLORS = ['#4e73df','#1cc88a','#36b9cc','#f6c23e','#e74a3b','#858796','#6f42c1','#fd7e14','#20c997','#0dcaf0'];

        function buildChart(id, config) {
            var canvas = document.getElementById(id);
            if (!canvas) return null;
            return new Chart(canvas, config);
        }

        function safeData(obj) {
            try {
                if (!obj || typeof obj !== 'object') return { labels: [], values: [] };
                var labels = Object.keys(obj);
                var values = Object.values(obj).map(Number);
                return { labels: labels, values: values };
            } catch (e) {
                return { labels: [], values: [] };
            }
        }

        var isDark = function () { return document.documentElement.getAttribute('data-theme') === 'dark'; };
        var gridColor = function () { return isDark() ? 'rgba(255,255,255,0.08)' : 'rgba(0,0,0,0.06)'; };
        var textColor = function () { return isDark() ? '#94a3b8' : '#64748b'; };

        Chart.defaults.font.family = "'Inter', sans-serif";
        Chart.defaults.font.size   = 12;
        Chart.defaults.color       = textColor();

        /* -- Sales Trend (Dashboard) -- */
        if (typeof monthlySalesData !== 'undefined') {
            var d = safeData(monthlySalesData);
            buildChart('salesTrendChart', {
                type: 'line',
                data: {
                    labels: d.labels,
                    datasets: [{
                        label: 'Monthly Sales',
                        data: d.values,
                        borderColor: '#4e73df',
                        backgroundColor: 'rgba(78,115,223,0.08)',
                        borderWidth: 2.5,
                        fill: true,
                        tension: 0.45,
                        pointRadius: 4,
                        pointBackgroundColor: '#4e73df',
                        pointBorderColor: '#fff',
                        pointBorderWidth: 2,
                        pointHoverRadius: 6
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    interaction: { mode: 'index', intersect: false },
                    plugins: {
                        legend: { display: false },
                        tooltip: {
                            callbacks: {
                                label: function (ctx) { return ' $' + ctx.raw.toLocaleString(); }
                            }
                        }
                    },
                    scales: {
                        x: { grid: { color: gridColor() }, ticks: { color: textColor() } },
                        y: {
                            grid: { color: gridColor() },
                            ticks: {
                                color: textColor(),
                                callback: function (v) { return '$' + (v >= 1000 ? (v/1000).toFixed(0) + 'k' : v); }
                            }
                        }
                    }
                }
            });
        }

        /* -- Region Doughnut -- */
        if (typeof regionSalesData !== 'undefined') {
            var rd = safeData(regionSalesData);
            buildChart('regionChart', {
                type: 'doughnut',
                data: {
                    labels: rd.labels,
                    datasets: [{
                        data: rd.values,
                        backgroundColor: COLORS,
                        borderWidth: 2,
                        borderColor: isDark() ? '#1e293b' : '#fff',
                        hoverOffset: 8
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    cutout: '65%',
                    plugins: {
                        legend: { position: 'bottom', labels: { padding: 14, usePointStyle: true, pointStyleWidth: 10 } },
                        tooltip: {
                            callbacks: {
                                label: function (ctx) {
                                    var total = ctx.dataset.data.reduce(function (a,b) { return a+b; }, 0);
                                    return ' $' + ctx.raw.toLocaleString() + ' (' + (ctx.raw/total*100).toFixed(1) + '%)';
                                }
                            }
                        }
                    }
                }
            });
        }

        /* -- Category Bar -- */
        if (typeof categorySalesData !== 'undefined') {
            var cd = safeData(categorySalesData);
            buildChart('categoryChart', {
                type: 'bar',
                data: {
                    labels: cd.labels,
                    datasets: [{
                        label: 'Sales',
                        data: cd.values,
                        backgroundColor: COLORS.slice(0, cd.labels.length).map(function (c) { return c + 'cc'; }),
                        borderColor: COLORS.slice(0, cd.labels.length),
                        borderWidth: 1.5,
                        borderRadius: 8
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    plugins: { legend: { display: false }, tooltip: {
                        callbacks: { label: function (ctx) { return ' $' + ctx.raw.toLocaleString(); } }
                    } },
                    scales: {
                        x: { grid: { display: false }, ticks: { color: textColor() } },
                        y: {
                            grid: { color: gridColor() },
                            ticks: { color: textColor(), callback: function (v) { return '$' + (v/1000).toFixed(0) + 'k'; } }
                        }
                    }
                }
            });
        }

        /* -- Radar Chart -- */
        buildChart('radarChart', {
            type: 'radar',
            data: {
                labels: ['Reliability', 'Speed', 'Cost Efficiency', 'Quality', 'Flexibility', 'Sustainability'],
                datasets: [{
                    label: 'Current',
                    data: [85, 72, 88, 79, 74, 67],
                    backgroundColor: 'rgba(78,115,223,0.15)',
                    borderColor: '#4e73df',
                    borderWidth: 2,
                    pointBackgroundColor: '#4e73df',
                    pointRadius: 4
                }, {
                    label: 'Target',
                    data: [90, 85, 90, 88, 82, 80],
                    backgroundColor: 'rgba(28,200,138,0.1)',
                    borderColor: '#1cc88a',
                    borderWidth: 2,
                    borderDash: [5, 5],
                    pointBackgroundColor: '#1cc88a',
                    pointRadius: 4
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: { position: 'bottom', labels: { usePointStyle: true, padding: 14 } }
                },
                scales: {
                    r: {
                        angleLines: { color: gridColor() },
                        grid: { color: gridColor() },
                        ticks: { color: textColor(), backdropColor: 'transparent' },
                        pointLabels: { color: textColor(), font: { size: 11 } },
                        min: 0, max: 100
                    }
                }
            }
        });

        /* ------------------------------------------------------------------ */
        /*  Analytics Page Charts                                               */
        /* ------------------------------------------------------------------ */
        if (document.getElementById('monthlyCompareChart') && typeof monthlySalesData !== 'undefined') {
            var md = safeData(monthlySalesData);
            buildChart('monthlyCompareChart', {
                type: 'bar',
                data: {
                    labels: md.labels,
                    datasets: [
                        {
                            label: 'Sales',
                            data: md.values,
                            backgroundColor: 'rgba(78,115,223,0.8)',
                            borderRadius: 6,
                            order: 2
                        },
                        {
                            label: 'Profit',
                            data: md.values.map(function (v) { return v * 0.18 + Math.random() * v * 0.05; }),
                            type: 'line',
                            borderColor: '#1cc88a',
                            backgroundColor: 'rgba(28,200,138,0.1)',
                            borderWidth: 2.5,
                            fill: true,
                            tension: 0.4,
                            pointRadius: 3,
                            order: 1
                        }
                    ]
                },
                options: {
                    responsive: true, maintainAspectRatio: false,
                    interaction: { mode: 'index', intersect: false },
                    plugins: { legend: { position: 'top', labels: { usePointStyle: true } },
                        tooltip: { callbacks: { label: function (ctx) { return ' ' + ctx.dataset.label + ': $' + ctx.raw.toLocaleString(); } } }
                    },
                    scales: {
                        x: { grid: { display: false } },
                        y: { grid: { color: gridColor() }, ticks: { callback: function (v) { return '$' + (v/1000).toFixed(0) + 'k'; } } }
                    }
                }
            });
        }

        if (document.getElementById('segmentChart') && typeof segmentSalesData !== 'undefined') {
            var sd = safeData(segmentSalesData);
            buildChart('segmentChart', {
                type: 'pie',
                data: {
                    labels: sd.labels.length ? sd.labels : ['Consumer', 'Corporate', 'Home Office'],
                    datasets: [{
                        data: sd.values.length ? sd.values : [52, 32, 16],
                        backgroundColor: ['#4e73df','#1cc88a','#f6c23e'],
                        borderWidth: 2,
                        borderColor: isDark() ? '#1e293b' : '#fff',
                        hoverOffset: 8
                    }]
                },
                options: {
                    responsive: true, maintainAspectRatio: false,
                    plugins: {
                        legend: { position: 'bottom', labels: { usePointStyle: true, padding: 14 } },
                        tooltip: { callbacks: { label: function (ctx) {
                            var total = ctx.dataset.data.reduce(function (a,b) { return a+b; }, 0);
                            return ' ' + ctx.label + ': ' + (ctx.raw/total*100).toFixed(1) + '%';
                        } } }
                    }
                }
            });
        }

        if (document.getElementById('categoryProfitChart') && typeof categorySalesData !== 'undefined') {
            var cpd = safeData(categorySalesData);
            buildChart('categoryProfitChart', {
                type: 'bar',
                data: {
                    labels: cpd.labels,
                    datasets: [
                        { label: 'Sales', data: cpd.values, backgroundColor: 'rgba(78,115,223,0.8)', borderRadius: 6 },
                        { label: 'Profit (est.)', data: cpd.values.map(function (v) { return v * 0.2; }), backgroundColor: 'rgba(28,200,138,0.8)', borderRadius: 6 }
                    ]
                },
                options: {
                    responsive: true, maintainAspectRatio: false,
                    plugins: { legend: { position: 'top', labels: { usePointStyle: true } } },
                    scales: {
                        x: { stacked: false },
                        y: { grid: { color: gridColor() }, ticks: { callback: function (v) { return '$' + (v/1000).toFixed(0) + 'k'; } } }
                    }
                }
            });
        }

        if (document.getElementById('regionalBarChart') && typeof regionSalesData !== 'undefined') {
            var rbd = safeData(regionSalesData);
            buildChart('regionalBarChart', {
                type: 'bar',
                data: {
                    labels: rbd.labels,
                    datasets: [{
                        label: 'Sales by Region',
                        data: rbd.values,
                        backgroundColor: COLORS.slice(0, rbd.labels.length).map(function(c){return c+'cc';}),
                        borderColor:  COLORS.slice(0, rbd.labels.length),
                        borderWidth: 1.5,
                        borderRadius: 8
                    }]
                },
                options: {
                    indexAxis: 'y',
                    responsive: true, maintainAspectRatio: false,
                    plugins: { legend: { display: false } },
                    scales: {
                        x: { grid: { color: gridColor() }, ticks: { callback: function (v) { return '$' + (v/1000).toFixed(0) + 'k'; } } },
                        y: { grid: { display: false } }
                    }
                }
            });
        }

        /* ------------------------------------------------------------------ */
        /*  AI Page Charts                                                      */
        /* ------------------------------------------------------------------ */
        /* -- Risk Gauge -- */
        var riskScore = (typeof aiData !== 'undefined' && aiData.riskScore) ? Number(aiData.riskScore) : 0;
        buildChart('riskGaugeChart', {
            type: 'doughnut',
            data: {
                datasets: [{
                    data: [riskScore, 100 - riskScore],
                    backgroundColor: [
                        riskScore < 30 ? '#1cc88a' : riskScore < 60 ? '#f6c23e' : '#e74a3b',
                        isDark() ? 'rgba(255,255,255,0.06)' : 'rgba(0,0,0,0.06)'
                    ],
                    borderWidth: 0
                }]
            },
            options: {
                responsive: true, maintainAspectRatio: false,
                circumference: 180, rotation: -90,
                cutout: '72%',
                plugins: { legend: { display: false }, tooltip: { enabled: false } }
            }
        });

        /* -- Forecast Chart -- */
        var forecastLabels = ['Jan','Feb','Mar','Apr','May','Jun','Jul','Aug','Sep','Oct','Nov','Dec'];
        var actualBase = 45000;
        var actualData = forecastLabels.map(function (_, i) {
            return i < 7 ? Math.floor(actualBase + (Math.random() * 20000) + i * 3000) : null;
        });
        var forecastData = forecastLabels.map(function (_, i) {
            return Math.floor(actualBase * 1.25 + (Math.random() * 10000) + i * 4000);
        });
        buildChart('forecastChart', {
            type: 'line',
            data: {
                labels: forecastLabels,
                datasets: [{
                    label: 'Actual Sales',
                    data: actualData,
                    borderColor: '#4e73df',
                    backgroundColor: 'rgba(78,115,223,0.08)',
                    borderWidth: 2.5,
                    fill: true,
                    tension: 0.4,
                    pointRadius: 4
                }, {
                    label: 'AI Forecast',
                    data: forecastData,
                    borderColor: '#1cc88a',
                    backgroundColor: 'rgba(28,200,138,0.04)',
                    borderWidth: 2.5,
                    borderDash: [8, 4],
                    fill: true,
                    tension: 0.4,
                    pointRadius: 4
                }]
            },
            options: {
                responsive: true, maintainAspectRatio: false,
                plugins: { legend: { position: 'top', labels: { usePointStyle: true } },
                    tooltip: { mode: 'index', intersect: false } },
                scales: {
                    x: { grid: { display: false } },
                    y: { grid: { color: gridColor() }, ticks: { callback: function (v) { return '$' + (v/1000).toFixed(0) + 'k'; } } }
                }
            }
        });

        /* ------------------------------------------------------------------ */
        /*  Product Search (Analytics Table)                                    */
        /* ------------------------------------------------------------------ */
        var productSearch = document.getElementById('productSearch');
        if (productSearch) {
            productSearch.addEventListener('input', function () {
                var q = this.value.toLowerCase();
                document.querySelectorAll('#productsTableBody tr').forEach(function (row) {
                    row.style.display = row.textContent.toLowerCase().includes(q) ? '' : 'none';
                });
            });
        }

        /* ------------------------------------------------------------------ */
        /*  Sortable Table Headers                                              */
        /* ------------------------------------------------------------------ */
        document.querySelectorAll('.sortable').forEach(function (th) {
            th.addEventListener('click', function () {
                var table = this.closest('table');
                if (!table) return;
                var col  = Array.from(this.parentElement.children).indexOf(this);
                var asc  = this.dataset.sortDir !== 'asc';
                this.dataset.sortDir = asc ? 'asc' : 'desc';
                var rows = Array.from(table.querySelectorAll('tbody tr'));
                rows.sort(function (a, b) {
                    var aText = (a.cells[col] ? a.cells[col].textContent : '').trim();
                    var bText = (b.cells[col] ? b.cells[col].textContent : '').trim();
                    var aNum  = parseFloat(aText.replace(/[^0-9.-]/g, ''));
                    var bNum  = parseFloat(bText.replace(/[^0-9.-]/g, ''));
                    if (!isNaN(aNum) && !isNaN(bNum)) return asc ? aNum - bNum : bNum - aNum;
                    return asc ? aText.localeCompare(bText) : bText.localeCompare(aText);
                });
                var tbody = table.querySelector('tbody');
                rows.forEach(function (r) { tbody.appendChild(r); });
            });
        });

        /* ------------------------------------------------------------------ */
        /*  Settings page: accent color restore                                 */
        /* ------------------------------------------------------------------ */
        var savedColor = localStorage.getItem('accentColor');
        if (savedColor) {
            document.documentElement.style.setProperty('--primary', savedColor);
        }

        /* ------------------------------------------------------------------ */
        /*  Dark mode switch on Settings page                                   */
        /* ------------------------------------------------------------------ */
        var darkModeSwitch = document.getElementById('darkModeSwitch');
        if (darkModeSwitch) {
            darkModeSwitch.checked = (localStorage.getItem('theme') === 'dark');
            darkModeSwitch.addEventListener('change', function () {
                var t = this.checked ? 'dark' : 'light';
                applyTheme(t);
                localStorage.setItem('theme', t);
            });
        }

        /* ------------------------------------------------------------------ */
        /*  Auto-dismiss alerts                                                  */
        /* ------------------------------------------------------------------ */
        setTimeout(function () {
            document.querySelectorAll('.alert.alert-success').forEach(function (el) {
                if (typeof bootstrap !== 'undefined' && bootstrap.Alert) {
                    var bsAlert = new bootstrap.Alert(el);
                    bsAlert.close();
                } else {
                    el.style.opacity = '0';
                    setTimeout(function () { if (el.parentElement) el.parentElement.removeChild(el); }, 400);
                }
            });
        }, 4000);

    }); // end DOMContentLoaded

})();
