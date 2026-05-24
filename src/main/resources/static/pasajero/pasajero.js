let map;
let pickupMarker;
let destinationMarker;
let driverMarker;
let ws;
let pendingRequest = false;
let destinationLat = null;
let destinationLng = null;
let selectedConductor = null;
let sessionChecked = false;

function initMap() {
  const defaultPos = { lat: -6.501, lng: -76.365 };
  map = new google.maps.Map(document.getElementById("map"), {
    center: defaultPos,
    zoom: 14,
    mapTypeControl: false,
    mapId: "7f05a409dd0b6a5abd5c170e"
  });

  pickupMarker = new google.maps.marker.AdvancedMarkerElement({
    map,
    position: defaultPos,
    title: "Punto de recogida",
    gmpDraggable: true
  });

  map.addListener("click", (event) => {
    if (!event.latLng) {
      return;
    }
    const pos = { lat: event.latLng.lat(), lng: event.latLng.lng() };
    destinationLat = pos.lat;
    destinationLng = pos.lng;
    setDestinationMarker(pos);
    const destinoInput = document.getElementById("destino");
    if (!destinoInput.value.trim()) {
      destinoInput.value = "Destino en mapa";
    }
    document.getElementById("rideStatus").textContent = "Destino seleccionado en el mapa";
  });

  pickupMarker.addListener("dragend", updateCoords);
  updateCoords();
  connectWebSocket();
  cargarConductores();
  validarSesion();
}

function updateCoords() {
  const pos = readLatLng(pickupMarker.position);
  if (!pos) {
    return;
  }
  document.getElementById("origenLat").value = pos.lat().toFixed(6);
  document.getElementById("origenLng").value = pos.lng().toFixed(6);
}

function connectWebSocket() {
  const status = document.getElementById("wsStatus");
  const scheme = window.location.protocol === "https:" ? "wss" : "ws";
  const wsUrl = `${scheme}://${window.location.host}/ws/pasajero`;

  status.textContent = `Conectando a ${wsUrl}`;
  ws = new WebSocket(wsUrl);

  ws.onopen = () => {
    status.textContent = "WebSocket conectado";
  };

  ws.onmessage = (event) => {
    try {
      const payload = JSON.parse(event.data);
      if (payload.type === "driver-location" && payload.lat && payload.lng) {
        showDriverMarker(payload.lat, payload.lng);
      }
      if (payload.type === "ride-status" && payload.status) {
        document.getElementById("rideStatus").textContent = payload.status;
      }
    } catch (error) {
      // ignore invalid messages
    }
  };

  ws.onclose = () => {
    status.textContent = "WebSocket desconectado";
  };

  ws.onerror = () => {
    status.textContent = "Error de WebSocket";
  };
}

function showDriverMarker(lat, lng) {
  const position = { lat: Number(lat), lng: Number(lng) };
  if (!driverMarker) {
    driverMarker = new google.maps.marker.AdvancedMarkerElement({
      map,
      position,
      title: "Conductor"
    });
  } else {
    driverMarker.position = position;
  }
}

function setDestinationMarker(position) {
  if (!destinationMarker) {
    destinationMarker = new google.maps.marker.AdvancedMarkerElement({
      map,
      position,
      title: "Destino"
    });
  } else {
    destinationMarker.position = position;
  }
}

function readLatLng(value) {
  if (!value) {
    return null;
  }
  if (typeof value.lat === "function") {
    return value;
  }
  return {
    lat: () => value.lat,
    lng: () => value.lng
  };
}

function solicitarViaje() {
  if (pendingRequest) {
    return;
  }
  const origenLat = document.getElementById("origenLat").value;
  const origenLng = document.getElementById("origenLng").value;
  const destino = document.getElementById("destino").value.trim();
  const token = localStorage.getItem("authToken");

  if (!destino && (destinationLat == null || destinationLng == null)) {
    alert("Selecciona un destino en el mapa");
    return;
  }
  if (!token) {
    document.getElementById("rideStatus").textContent = "Inicia sesion para solicitar";
    return;
  }

  pendingRequest = true;
  document.getElementById("rideStatus").textContent = selectedConductor
    ? `Solicitando a ${selectedConductor.nombre}`
    : "Buscando conductor disponible";

  if (destinationLat != null && destinationLng != null) {
    enviarSolicitud(token, origenLat, origenLng, destinationLat, destinationLng, destino);
    return;
  }

  if (!google.maps.Geocoder) {
    document.getElementById("rideStatus").textContent = "Selecciona el destino en el mapa";
    pendingRequest = false;
    return;
  }

  const geocoder = new google.maps.Geocoder();
  geocoder.geocode({ address: destino }, (results, status) => {
    if (status !== "OK" || !results || results.length === 0) {
      document.getElementById("rideStatus").textContent = "No se pudo geocodificar el destino";
      pendingRequest = false;
      return;
    }

    const destinoLatResult = results[0].geometry.location.lat().toFixed(6);
    const destinoLngResult = results[0].geometry.location.lng().toFixed(6);
    enviarSolicitud(token, origenLat, origenLng, destinoLatResult, destinoLngResult, destino);
  });
}

function enviarSolicitud(token, origenLat, origenLng, destinoLatValue, destinoLngValue, destino) {
  const message = {
    type: "ride-request",
    token,
    origenLat,
    origenLng,
    destinoLat: destinoLatValue,
    destinoLng: destinoLngValue,
    destino,
    conductorId: selectedConductor ? selectedConductor.idConductor : null
  };

  const targetUrl = "/api/viajes";
  const headers = {
    "Content-Type": "application/json",
    "Authorization": `Bearer ${token}`
  };

  fetch(targetUrl, {
    method: "POST",
    headers,
    body: JSON.stringify({
      origenLat: Number(origenLat),
      origenLng: Number(origenLng),
      destinoLat: Number(destinoLatValue),
      destinoLng: Number(destinoLngValue),
      destinoTexto: destino,
      conductorId: message.conductorId
    })
  })
    .then((response) => {
      if (!response.ok) {
        throw new Error("No se pudo crear el viaje");
      }
      return response.json();
    })
    .then((data) => {
      if (ws && ws.readyState === WebSocket.OPEN) {
        ws.send(JSON.stringify(message));
      }
      document.getElementById("rideStatus").textContent = "Solicitud enviada";
    })
    .catch(() => {
      document.getElementById("rideStatus").textContent = "No se pudo enviar la solicitud";
    })
    .finally(() => {
      selectedConductor = null;
      pendingRequest = false;
    });
  selectedConductor = null;
}

async function cargarConductores() {
  const token = localStorage.getItem("authToken");
  const driversList = document.getElementById("driversList");
  if (!driversList) {
    return;
  }

  driversList.innerHTML = "<div class=\"status\">Cargando conductores...</div>";
  if (!token) {
    driversList.innerHTML = "<div class=\"status\">Inicia sesion para ver conductores.</div>";
    return;
  }

  const origenLat = document.getElementById("origenLat").value;
  const origenLng = document.getElementById("origenLng").value;
  if (!origenLat || !origenLng) {
    driversList.innerHTML = "<div class=\"status\">Selecciona tu origen.</div>";
    return;
  }

  try {
    const response = await fetch(`/api/pasajero/conductores/cercanos?lat=${origenLat}&lng=${origenLng}`,
      { headers: { "Authorization": `Bearer ${token}` } });
    if (response.status === 401 || response.status === 403) {
      manejarSesionInvalida();
      return;
    }
    if (!response.ok) {
      driversList.innerHTML = "<div class=\"status\">No se pudo cargar conductores.</div>";
      return;
    }
    const conductores = await response.json();
    renderDrivers(conductores || []);
  } catch (error) {
    driversList.innerHTML = "<div class=\"status\">Error cargando conductores.</div>";
  }
}

function renderDrivers(conductores) {
  const driversList = document.getElementById("driversList");
  const driversCount = document.getElementById("driversCount");
  driversList.innerHTML = "";
  driversCount.textContent = conductores.length;

  if (conductores.length === 0) {
    driversList.innerHTML = "<div class=\"status\">No hay conductores cercanos disponibles.</div>";
    return;
  }

  conductores.forEach((conductor) => {
    const card = document.createElement("div");
    card.className = "driver-card";
    card.innerHTML = `
      <div>
        <strong>${conductor.nombre}</strong><br />
        <span>${conductor.vehiculo || "Vehiculo no registrado"}</span>
      </div>
      <button type="button">Ver</button>
    `;
    card.querySelector("button").addEventListener("click", () => {
      openDriverModal(conductor);
    });
    driversList.appendChild(card);
  });
}

function openDriverModal(conductor) {
  selectedConductor = conductor;
  document.getElementById("modalDriverName").textContent = conductor.nombre || "Conductor";
  document.getElementById("modalVehicle").textContent = conductor.vehiculo || "-";
  document.getElementById("modalRating").textContent = conductor.calificacionPromedio ?? "-";
  document.getElementById("modalDistance").textContent = conductor.distanciaKm < 0
    ? "Sin ubicacion"
    : `${conductor.distanciaKm.toFixed(2)} km`;
  document.getElementById("driverModal").classList.remove("hidden");
}

function closeDriverModal() {
  document.getElementById("driverModal").classList.add("hidden");
}

document.getElementById("buscarConductores").addEventListener("click", cargarConductores);
document.getElementById("closeModal").addEventListener("click", closeDriverModal);
document.getElementById("requestDriver").addEventListener("click", () => {
  if (!selectedConductor) {
    return;
  }
  closeDriverModal();
  document.getElementById("rideStatus").textContent = `Solicitando a ${selectedConductor.nombre}`;
  solicitarViaje();
});

async function validarSesion() {
  if (sessionChecked) {
    return;
  }
  sessionChecked = true;
  const token = localStorage.getItem("authToken");
  if (!token) {
    return;
  }

  try {
    const response = await fetch("/api/auth/me", {
      headers: { "Authorization": `Bearer ${token}` }
    });
    if (!response.ok) {
      manejarSesionInvalida();
      return;
    }
    const data = await response.json();
    if (!data || !data.usuario || data.usuario.rol !== "PASAJERO") {
      manejarSesionInvalida();
    }
  } catch (error) {
    // ignore
  }
}

function manejarSesionInvalida() {
  localStorage.removeItem("authToken");
  window.location.href = "/auth/login-cliente.html";
}

window.initMap = initMap;
window.solicitarViaje = solicitarViaje;
