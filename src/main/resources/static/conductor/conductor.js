let map;
let passengerMarker;
let driverMarker;
let ws;
let activeRideId = null;
const rides = new Map();
const pendingRideActions = new Set();
const FIXED_COORDS = { lat: -6.485623, lng: -76.371148 };

function showRideMessage(message, variant = "info") {
  const existing = document.getElementById("rideToast");
  if (existing) {
    existing.remove();
  }

  const toast = document.createElement("div");
  toast.id = "rideToast";
  toast.textContent = message;
  toast.style.position = "fixed";
  toast.style.right = "20px";
  toast.style.bottom = "20px";
  toast.style.zIndex = "9999";
  toast.style.padding = "10px 14px";
  toast.style.borderRadius = "10px";
  toast.style.boxShadow = "0 8px 24px rgba(23, 32, 38, 0.2)";
  toast.style.fontSize = "13px";
  toast.style.color = "#ffffff";
  toast.style.background = variant === "error" ? "#b91c1c" : "#1f6f5b";
  document.body.appendChild(toast);

  window.setTimeout(() => {
    toast.remove();
  }, 2800);
}

function initMap() {
  const defaultPos = { lat: -6.501, lng: -76.365 };
  map = new google.maps.Map(document.getElementById("map"), {
    center: defaultPos,
    zoom: 14,
    mapTypeControl: false,
    mapId: "7f05a409dd0b6a5abd5c170e"
  });

  map.setCenter(FIXED_COORDS);
  if (!driverMarker) {
    driverMarker = new google.maps.marker.AdvancedMarkerElement({
      map,
      position: FIXED_COORDS,
      title: "Conductor"
    });
  } else {
    driverMarker.position = FIXED_COORDS;
  }
}

function setPassengerMarker(lat, lng) {
  const position = { lat: Number(lat), lng: Number(lng) };
  if (!passengerMarker) {
    passengerMarker = new google.maps.marker.AdvancedMarkerElement({
      map,
      position,
      title: "Pasajero"
    });
  } else {
    passengerMarker.position = position;
  }
  map.setCenter(position);
}

function updateRideList() {
  const list = document.getElementById("rideList");
  list.innerHTML = "";
  const entries = Array.from(rides.values());
  document.getElementById("rideCount").textContent = entries.length;

  if (entries.length === 0) {
    list.innerHTML = "<div class=\"card\">Sin solicitudes activas.</div>";
    return;
  }

  entries.forEach((ride) => {
    const card = document.createElement("div");
    card.className = "ride-card" + (ride.viajeId === activeRideId ? " active" : "");
    card.innerHTML = `
      <div><strong>${ride.pasajeroNombre || "Pasajero"}</strong></div>
      <div class="ride-meta">
        <span>Origen: ${Number(ride.origenLat).toFixed(4)}, ${Number(ride.origenLng).toFixed(4)}</span>
        <span>${ride.estado || "SOLICITADO"}</span>
      </div>
      <div class="ride-actions">
        <button class="secondary-button accept" data-action="accept" ${pendingRideActions.has(ride.viajeId) ? "disabled" : ""}>Aceptar</button>
        <button class="secondary-button reject" data-action="reject" ${pendingRideActions.has(ride.viajeId) ? "disabled" : ""}>Rechazar</button>
      </div>
    `;

    card.addEventListener("click", () => {
      activeRideId = ride.viajeId;
      if (ride.origenLat && ride.origenLng) {
        setPassengerMarker(ride.origenLat, ride.origenLng);
        document.getElementById("mapOverlay").textContent = `Pasajero: ${ride.pasajeroNombre}`;
      }
      updateRideList();
    });

    const buttons = card.querySelectorAll("button");
    buttons.forEach((button) => {
      button.addEventListener("click", (event) => {
        event.stopPropagation();
        if (pendingRideActions.has(ride.viajeId)) {
          return;
        }
        pendingRideActions.add(ride.viajeId);
        updateRideList();
        if (button.dataset.action === "accept") {
          sendRideAction("ride-accept", ride.viajeId);
        } else {
          sendRideAction("ride-reject", ride.viajeId);
        }
      });
    });

    list.appendChild(card);
  });
}

function sendRideAction(type, viajeId) {
  const token = localStorage.getItem("authToken");
  if (!token) {
    pendingRideActions.delete(viajeId);
    updateRideList();
    showRideMessage("Tu sesion no es valida. Inicia sesion nuevamente.", "error");
    return;
  }
  if (ws && ws.readyState === WebSocket.OPEN) {
    ws.send(JSON.stringify({ type, token, viajeId }));
    return;
  }

  pendingRideActions.delete(viajeId);
  updateRideList();
  showRideMessage("No hay conexion con el servidor. Intenta de nuevo.", "error");
}

function connectWebSocket() {
  const scheme = window.location.protocol === "https:" ? "wss" : "ws";
  const wsUrl = `${scheme}://${window.location.host}/ws/conductor`;
  ws = new WebSocket(wsUrl);

  ws.onopen = () => {
    const token = localStorage.getItem("authToken");
    if (token) {
      ws.send(JSON.stringify({ type: "driver-connect", token }));
    }
  };

  ws.onmessage = (event) => {
    try {
      const payload = JSON.parse(event.data);
      if (payload.type === "driver-ready" && payload.pendientes) {
        rides.clear();
        payload.pendientes.forEach((ride) => rides.set(ride.viajeId, ride));
        updateRideList();
      }
      if (payload.type === "ride-request") {
        rides.set(payload.viajeId, payload);
        updateRideList();
      }
      if (payload.type === "ride-accepted") {
        pendingRideActions.delete(payload.viajeId);
        rides.delete(payload.viajeId);
        if (activeRideId === payload.viajeId) {
          activeRideId = null;
        }
        showRideMessage(`Viaje #${payload.viajeId} aceptado.`);
        updateRideList();
      }
      if (payload.type === "ride-rejected") {
        pendingRideActions.delete(payload.viajeId);
        rides.delete(payload.viajeId);
        if (activeRideId === payload.viajeId) {
          activeRideId = null;
        }
        showRideMessage(`Viaje #${payload.viajeId} rechazado.`);
        updateRideList();
      }
      if (payload.type === "error") {
        showRideMessage(payload.message || "Ocurrio un error al procesar la solicitud.", "error");
        if (/token invalido|expirado|no autorizado|rol no autorizado/i.test(payload.message || "")) {
          localStorage.removeItem("authToken");
          window.location.href = "/auth/login-conductor.html";
        }
      }
    } catch (error) {
      // ignore invalid messages
    }
  };

  ws.onclose = () => {
    pendingRideActions.clear();
    updateRideList();
    showRideMessage("Conexion WebSocket cerrada. Recarga la pagina si el problema persiste.", "error");
  };
}

async function cargarPerfil() {
  const token = localStorage.getItem("authToken");
  if (!token) {
    window.location.href = "/auth/login-conductor.html";
    return;
  }

  try {
    const response = await fetch("/api/auth/me", {
      headers: { "Authorization": `Bearer ${token}` }
    });

    if (!response.ok) {
      window.location.href = "/auth/login-conductor.html";
      return;
    }

    const data = await response.json();
    if (!data || !data.usuario || data.usuario.rol !== "CONDUCTOR") {
      window.location.href = "/auth/login-conductor.html";
      return;
    }

    document.getElementById("driverBadge").textContent = data.usuario.nombre || "Conductor";
  } catch (error) {
    window.location.href = "/auth/login-conductor.html";
  }

  try {
    const response = await fetch("/api/conductor/me", {
      headers: { "Authorization": `Bearer ${token}` }
    });
    if (!response.ok) {
      return;
    }
    const profile = await response.json();
    document.getElementById("profileName").textContent = profile.nombre || "-";
    document.getElementById("profileEmail").textContent = profile.email || "-";
    document.getElementById("profileLicense").textContent = profile.licencia || "-";
    document.getElementById("profileVehicle").textContent = profile.vehiculo || "-";
    document.getElementById("profileRating").textContent = profile.calificacionPromedio ?? "-";
    document.getElementById("profileStatus").textContent = profile.disponible ? "Disponible" : "No disponible";
  } catch (error) {
    // ignore
  }
}

function sendDriverLocation() {
  const token = localStorage.getItem("authToken");
  if (!token) {
    return;
  }

  if (FIXED_COORDS) {
    const lat = FIXED_COORDS.lat.toFixed(6);
    const lng = FIXED_COORDS.lng.toFixed(6);

    if (ws && ws.readyState === WebSocket.OPEN) {
      if (activeRideId) {
        ws.send(JSON.stringify({
          type: "driver-location",
          token,
          viajeId: activeRideId,
          lat,
          lng
        }));
      }

      ws.send(JSON.stringify({
        type: "driver-available-location",
        token,
        lat,
        lng
      }));
    }

    const coords = { lat: Number(lat), lng: Number(lng) };
    if (!driverMarker) {
      driverMarker = new google.maps.marker.AdvancedMarkerElement({
        map,
        position: coords,
        title: "Conductor"
      });
    } else {
      driverMarker.position = coords;
    }
    return;
  }

  if (!navigator.geolocation) {
    return;
  }

  navigator.geolocation.getCurrentPosition((position) => {
    const lat = position.coords.latitude.toFixed(6);
    const lng = position.coords.longitude.toFixed(6);
    if (ws && ws.readyState === WebSocket.OPEN) {
      if (activeRideId) {
        ws.send(JSON.stringify({
          type: "driver-location",
          token,
          viajeId: activeRideId,
          lat,
          lng
        }));
      }

      ws.send(JSON.stringify({
        type: "driver-available-location",
        token,
        lat,
        lng
      }));
    }

    const coords = { lat: Number(lat), lng: Number(lng) };
    if (!driverMarker) {
      driverMarker = new google.maps.marker.AdvancedMarkerElement({
        map,
        position: coords,
        title: "Conductor"
      });
    } else {
      driverMarker.position = coords;
    }
  });
}

function setupLogout() {
  const logoutButton = document.getElementById("logoutButton");
  logoutButton.addEventListener("click", async () => {
    const token = localStorage.getItem("authToken");
    if (token) {
      try {
        await fetch("/api/auth/logout", {
          method: "POST",
          headers: { "Authorization": `Bearer ${token}` }
        });
      } catch (error) {
        // ignore
      }
    }
    localStorage.removeItem("authToken");
    window.location.href = "/auth/login-conductor.html";
  });
}

document.getElementById("sendLocationButton").addEventListener("click", sendDriverLocation);

setupLogout();
connectWebSocket();
cargarPerfil();
setInterval(sendDriverLocation, 15000);
window.initMap = initMap;
