const badge = document.getElementById("adminBadge");
const logoutButton = document.getElementById("logoutButton");
const userForm = document.getElementById("userForm");
const userFormMessage = document.getElementById("userFormMessage");
const roleFilter = document.getElementById("roleFilter");
const userList = document.getElementById("userList");
const conductorForm = document.getElementById("conductorForm");
const conductorFormMessage = document.getElementById("conductorFormMessage");
const conductorList = document.getElementById("conductorList");
const pasajeroList = document.getElementById("pasajeroList");
const reportPeriod = document.getElementById("reportPeriod");
const reportDate = document.getElementById("reportDate");
const reportMonth = document.getElementById("reportMonth");
const reportYear = document.getElementById("reportYear");
const loadReportButton = document.getElementById("loadReport");
const exportReportButton = document.getElementById("exportReport");
const summaryDesde = document.getElementById("summaryDesde");
const summaryHasta = document.getElementById("summaryHasta");
const loadSummaryButton = document.getElementById("loadSummary");
const rankingConductores = document.getElementById("rankingConductores");

function getToken() {
  return localStorage.getItem("authToken");
}

function redirectToLogin() {
  window.location.href = "/auth/login-admin.html";
}

async function apiFetch(url, options = {}) {
  const token = getToken();
  if (!token) {
    redirectToLogin();
    return null;
  }
  const headers = options.headers || {};
  headers.Authorization = `Bearer ${token}`;
  if (options.body) {
    headers["Content-Type"] = "application/json";
  }
  const response = await fetch(url, { ...options, headers });
  if (response.status === 401 || response.status === 403) {
    redirectToLogin();
    return null;
  }
  return response;
}

async function validarSesion() {
  const response = await apiFetch("/api/auth/me");
  if (!response) {
    return false;
  }
  if (!response.ok) {
    redirectToLogin();
    return false;
  }
  const data = await response.json();
  if (!data || !data.usuario || data.usuario.rol !== "ADMIN") {
    redirectToLogin();
    return false;
  }
  badge.textContent = data.usuario.nombre || "Administrador";
  return true;
}

logoutButton.addEventListener("click", async () => {
  const token = getToken();
  if (token) {
    try {
      await fetch("/api/auth/logout", {
        method: "POST",
        headers: { Authorization: `Bearer ${token}` }
      });
    } catch (error) {
      // ignore
    }
  }
  localStorage.removeItem("authToken");
  redirectToLogin();
});

async function cargarStats() {
  const usuariosResponse = await apiFetch("/api/admin/usuarios");
  if (!usuariosResponse || !usuariosResponse.ok) {
    return;
  }
  const usuarios = await usuariosResponse.json();
  const conteo = usuarios.reduce(
    (acc, item) => {
      acc.total += 1;
      acc[item.rol] = (acc[item.rol] || 0) + 1;
      return acc;
    },
    { total: 0 }
  );

  document.getElementById("statUsuarios").textContent = conteo.total;
  document.getElementById("statPasajeros").textContent = conteo.PASAJERO || 0;

  const conductoresResponse = await apiFetch("/api/admin/conductores");
  if (conductoresResponse && conductoresResponse.ok) {
    const conductores = await conductoresResponse.json();
    const disponibles = conductores.filter((item) => item.disponible).length;
    document.getElementById("statConductores").textContent = disponibles;
  }

  const reporteResponse = await apiFetch("/api/admin/reportes/dia");
  if (reporteResponse && reporteResponse.ok) {
    const reporte = await reporteResponse.json();
    document.getElementById("statViajes").textContent = reporte.viajes ?? 0;
  }
}

async function cargarUsuarios() {
  const rol = roleFilter.value;
  const url = rol ? `/api/admin/usuarios?rol=${rol}` : "/api/admin/usuarios";
  const response = await apiFetch(url);
  if (!response || !response.ok) {
    return;
  }
  const usuarios = await response.json();
  userList.innerHTML = "";
  if (usuarios.length === 0) {
    userList.innerHTML = "<div class=\"data-row\">Sin usuarios registrados.</div>";
    return;
  }

  usuarios.forEach((usuario) => {
    const row = document.createElement("div");
    row.className = "data-row";
    row.innerHTML = `
      <div>
        <strong>${usuario.nombre}</strong><br />
        <span>${usuario.email} | ${usuario.rol}</span>
      </div>
      <div class="data-actions">
        <button data-id="${usuario.idUsuario}">Eliminar</button>
      </div>
    `;
    row.querySelector("button").addEventListener("click", async () => {
      const response = await apiFetch(`/api/admin/usuarios/${usuario.idUsuario}`, { method: "DELETE" });
      if (response && response.ok) {
        cargarUsuarios();
        cargarStats();
      }
    });
    userList.appendChild(row);
  });
}

async function cargarConductores() {
  const response = await apiFetch("/api/admin/conductores");
  if (!response || !response.ok) {
    return;
  }
  const conductores = await response.json();
  conductorList.innerHTML = "";
  if (conductores.length === 0) {
    conductorList.innerHTML = "<div class=\"data-row\">Sin conductores creados.</div>";
    return;
  }

  conductores.forEach((conductor) => {
    const row = document.createElement("div");
    row.className = "data-row";
    row.innerHTML = `
      <div>
        <strong>Conductor #${conductor.idConductor}</strong><br />
        <span>Usuario ${conductor.usuarioId} | ${conductor.licencia}</span>
      </div>
      <div class="data-actions">
        <span class="pill">${conductor.disponible ? "Disponible" : "No disponible"}</span>
      </div>
    `;
    conductorList.appendChild(row);
  });
}

async function cargarPasajeros() {
  const response = await apiFetch("/api/pasajeros");
  if (!response || !response.ok) {
    return;
  }
  const pasajeros = await response.json();
  pasajeroList.innerHTML = "";
  if (pasajeros.length === 0) {
    pasajeroList.innerHTML = "<div class=\"data-row\">Sin pasajeros registrados.</div>";
    return;
  }

  pasajeros.forEach((pasajero) => {
    const row = document.createElement("div");
    row.className = "data-row";
    row.innerHTML = `
      <div>
        <strong>${pasajero.nombre}</strong><br />
        <span>${pasajero.email}</span>
      </div>
      <div class="data-actions">
        <span class="pill">Pasajero</span>
      </div>
    `;
    pasajeroList.appendChild(row);
  });
}

userForm.addEventListener("submit", async (event) => {
  event.preventDefault();
  userFormMessage.textContent = "";

  const payload = {
    nombre: userForm.nombre.value.trim(),
    email: userForm.email.value.trim(),
    password: userForm.password.value.trim(),
    rol: userForm.rol.value
  };

  const response = await apiFetch("/api/admin/usuarios", {
    method: "POST",
    body: JSON.stringify(payload)
  });

  if (response && response.ok) {
    userForm.reset();
    cargarUsuarios();
    cargarStats();
  } else {
    userFormMessage.textContent = "No se pudo crear el usuario";
  }
});

conductorForm.addEventListener("submit", async (event) => {
  event.preventDefault();
  conductorFormMessage.textContent = "";

  const payload = {
    usuarioId: Number(conductorForm.usuarioId.value),
    licencia: conductorForm.licencia.value.trim(),
    vehiculo: conductorForm.vehiculo.value.trim(),
    calificacionPromedio: conductorForm.calificacion.value ? Number(conductorForm.calificacion.value) : null,
    disponible: conductorForm.disponible.value === "true"
  };

  const response = await apiFetch("/api/admin/conductores", {
    method: "POST",
    body: JSON.stringify(payload)
  });

  if (response && response.ok) {
    conductorForm.reset();
    cargarConductores();
    cargarStats();
  } else {
    conductorFormMessage.textContent = "No se pudo crear el conductor";
  }
});

roleFilter.addEventListener("change", cargarUsuarios);

function actualizarControlesReporte() {
  reportDate.style.display = reportPeriod.value === "dia" ? "block" : "none";
  reportMonth.style.display = reportPeriod.value === "mes" ? "block" : "none";
  reportYear.style.display = reportPeriod.value === "anio" ? "block" : "none";
}

async function cargarReporte() {
  let url = "/api/admin/reportes/dia";
  if (reportPeriod.value === "dia") {
    if (reportDate.value) {
      url += `?fecha=${reportDate.value}`;
    }
  } else if (reportPeriod.value === "mes") {
    let params = [];
    if (reportMonth.value) {
      const [anio, mes] = reportMonth.value.split("-");
      params.push(`anio=${anio}`, `mes=${Number(mes)}`);
    }
    url = "/api/admin/reportes/mes" + (params.length ? `?${params.join("&")}` : "");
  } else {
    const anio = reportYear.value || new Date().getFullYear();
    url = `/api/admin/reportes/anio?anio=${anio}`;
  }

  const response = await apiFetch(url);
  if (!response || !response.ok) {
    return;
  }
  const reporte = await response.json();
  document.getElementById("reportViajes").textContent = reporte.viajes ?? 0;
  document.getElementById("reportUsuarios").textContent = reporte.usuarios ?? 0;
  document.getElementById("reportPasajeros").textContent = reporte.pasajeros ?? 0;
  document.getElementById("reportConductores").textContent = reporte.conductores ?? 0;
  document.getElementById("reportAdmins").textContent = reporte.administradores ?? 0;
  renderChart(reporte.serieViajes || []);
}

function renderChart(series) {
  const chart = document.getElementById("reportChart");
  chart.innerHTML = "";
  if (series.length === 0) {
    chart.innerHTML = "<div class=\"data-row\">Sin datos para graficar.</div>";
    return;
  }
  const max = Math.max(...series.map((item) => item.total));
  series.forEach((item) => {
    const row = document.createElement("div");
    row.className = "bar";
    const percent = max === 0 ? 0 : Math.round((item.total / max) * 100);
    row.innerHTML = `
      <div>${item.etiqueta}</div>
      <div class="bar-track"><div class="bar-fill" style="width: ${percent}%"></div></div>
      <div>${item.total}</div>
    `;
    chart.appendChild(row);
  });
}

function formatMoney(value) {
  const numeric = Number(value || 0);
  return `S/. ${numeric.toFixed(2)}`;
}

async function cargarResumenEjecutivo() {
  const hoy = new Date();
  const hace30 = new Date(hoy);
  hace30.setDate(hoy.getDate() - 30);

  if (!summaryDesde.value) {
    summaryDesde.value = hace30.toISOString().slice(0, 10);
  }
  if (!summaryHasta.value) {
    summaryHasta.value = hoy.toISOString().slice(0, 10);
  }

  const resumenUrl = `/api/admin/reportes/resumen?desde=${summaryDesde.value}&hasta=${summaryHasta.value}`;
  const resumenResponse = await apiFetch(resumenUrl);
  if (resumenResponse && resumenResponse.ok) {
    const resumen = await resumenResponse.json();
    document.getElementById("summaryFinalizados").textContent = resumen.viajesFinalizados ?? 0;
    document.getElementById("summaryCancelados").textContent = resumen.viajesCancelados ?? 0;
    document.getElementById("summaryUsuariosNuevos").textContent = resumen.usuariosNuevos ?? 0;
    document.getElementById("summaryIngresos").textContent = formatMoney(resumen.ingresosBrutos);
    document.getElementById("summaryComisiones").textContent = formatMoney(resumen.comisionesPlataforma);
    document.getElementById("summaryGananciasConductores").textContent = formatMoney(resumen.gananciasConductores);
  }

  const rankingUrl = `/api/admin/reportes/conductores/ranking?desde=${summaryDesde.value}&hasta=${summaryHasta.value}&limite=10`;
  const rankingResponse = await apiFetch(rankingUrl);
  if (!rankingResponse || !rankingResponse.ok) {
    return;
  }

  const ranking = await rankingResponse.json();
  rankingConductores.innerHTML = "";
  if (!ranking.length) {
    rankingConductores.innerHTML = '<div class="data-row">Sin datos para el rango seleccionado.</div>';
    return;
  }

  ranking.forEach((item, index) => {
    const row = document.createElement("div");
    row.className = "data-row";
    row.innerHTML = `
      <div>
        <strong>#${index + 1} ${item.nombre}</strong><br />
        <span>${item.vehiculo || "-"} | Rating: ${item.calificacionPromedio ?? 0}</span>
      </div>
      <div class="data-actions">
        <span class="pill money">${formatMoney(item.ganancias)}</span>
      </div>
    `;
    rankingConductores.appendChild(row);
  });
}

async function exportarReporte() {
  let url = `/api/admin/reportes/export?periodo=${reportPeriod.value}`;
  if (reportPeriod.value === "dia" && reportDate.value) {
    url += `&fecha=${reportDate.value}`;
  }
  if (reportPeriod.value === "mes" && reportMonth.value) {
    const [anio, mes] = reportMonth.value.split("-");
    url += `&anio=${anio}&mes=${Number(mes)}`;
  }
  if (reportPeriod.value === "anio") {
    const anio = reportYear.value || new Date().getFullYear();
    url += `&anio=${anio}`;
  }

  const response = await apiFetch(url);
  if (!response || !response.ok) {
    return;
  }
  const blob = await response.blob();
  const link = document.createElement("a");
  link.href = URL.createObjectURL(blob);
  link.download = `reporte-${reportPeriod.value}.csv`;
  document.body.appendChild(link);
  link.click();
  link.remove();
}

reportPeriod.addEventListener("change", () => {
  actualizarControlesReporte();
  cargarReporte();
});

loadReportButton.addEventListener("click", cargarReporte);
exportReportButton.addEventListener("click", exportarReporte);
loadSummaryButton.addEventListener("click", cargarResumenEjecutivo);

(async () => {
  const ok = await validarSesion();
  if (!ok) {
    return;
  }
  actualizarControlesReporte();
  await cargarStats();
  await cargarUsuarios();
  await cargarConductores();
  await cargarPasajeros();
  await cargarReporte();
  await cargarResumenEjecutivo();
})();
