let map;
let passengerMarker;
let destinationMarker;
let driverMarker;
let ws;
let activeRideId = null;
const rides = new Map();
const pendingRideActions = new Set();

let directionsService;
let directionsRenderer;
let pickupDirectionsService;
let pickupDirectionsRenderer;

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
  toast.style.padding = "12px 18px";
  toast.style.borderRadius = "12px";
  toast.style.boxShadow = "0 10px 30px rgba(0, 0, 0, 0.3)";
  toast.style.fontSize = "13px";
  toast.style.fontWeight = "600";
  toast.style.color = "#ffffff";
  toast.style.background = variant === "error" ? "#ef4444" : "#10b981";
  document.body.appendChild(toast);

  window.setTimeout(() => {
    toast.remove();
  }, 3500);
}

function initMap() {
  const defaultPos = { lat: -6.501, lng: -76.365 };
  map = new google.maps.Map(document.getElementById("map"), {
    center: defaultPos,
    zoom: 14,
    mapTypeControl: false,
    mapId: "7f05a409dd0b6a5abd5c170e"
  });

  directionsService = new google.maps.DirectionsService();
  directionsRenderer = new google.maps.DirectionsRenderer({
    map,
    suppressMarkers: true,
    polylineOptions: {
      strokeColor: "#f59e0b", // Amber
      strokeWeight: 6
    }
  });

  pickupDirectionsService = new google.maps.DirectionsService();
  pickupDirectionsRenderer = new google.maps.DirectionsRenderer({
    map,
    suppressMarkers: true,
    polylineOptions: {
      strokeColor: "#3b82f6", // Blue
      strokeWeight: 4,
      strokeOpacity: 0.7
    }
  });

  map.setCenter(FIXED_COORDS);
  createDriverMarker(FIXED_COORDS);

  connectWebSocket();
  cargarPerfil();
  setupFinishRide();
  
  // Start location reporting loop
  sendDriverLocation();
  setInterval(sendDriverLocation, 15000);
}

function createDriverMarker(position) {
  if (!driverMarker) {
    const driverEl = document.createElement("div");
    driverEl.innerHTML = `
      <div style="background-color: #10b981; color: white; padding: 10px; border-radius: 50%; box-shadow: 0 4px 14px rgba(16, 185, 129, 0.4); border: 2.5px solid white; display: flex; align-items: center; justify-content: center;">
        <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M19 17h2c.6 0 1-.4 1-1v-3c0-.9-.7-1.7-1.5-1.9C18.7 10.6 16 10 16 10s-1.3-1.4-2.2-2.3c-.5-.4-1.1-.7-1.8-.7H5c-.6 0-1.1.4-1.4.9l-1.4 2.9A3.7 3.7 0 0 0 2 12v4c0 .6.4 1 1 1h2"/><circle cx="7" cy="17" r="2"/><path d="M9 17h6"/><circle cx="17" cy="17" r="2"/></svg>
      </div>
    `;
    driverMarker = new google.maps.marker.AdvancedMarkerElement({
      map,
      position,
      content: driverEl,
      title: "Mi Ubicación"
    });
  } else {
    driverMarker.position = position;
  }
}

function setPassengerMarkers(origenLat, origenLng, destinoLat, destinoLng) {
  const pickupPos = { lat: Number(origenLat), lng: Number(origenLng) };

  // Set Passenger Pickup marker
  if (!passengerMarker) {
    const passengerEl = document.createElement("div");
    passengerEl.innerHTML = `
      <div style="background-color: #3b82f6; color: white; padding: 10px; border-radius: 50%; box-shadow: 0 4px 14px rgba(59, 130, 246, 0.4); border: 2.5px solid white; display: flex; align-items: center; justify-content: center;">
        <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><path d="M20 10c0 6-8 12-8 12s-8-6-8-12a8 8 0 0 1 16 0Z"/><circle cx="12" cy="10" r="3"/></svg>
      </div>
    `;
    passengerMarker = new google.maps.marker.AdvancedMarkerElement({
      map,
      position: pickupPos,
      content: passengerEl,
      title: "Origen Pasajero"
    });
  } else {
    passengerMarker.position = pickupPos;
  }

  // Set Passenger Destination marker
  if (destinoLat && destinoLng) {
    const destPos = { lat: Number(destinoLat), lng: Number(destinoLng) };
    if (!destinationMarker) {
      const destEl = document.createElement("div");
      destEl.innerHTML = `
        <div style="background-color: #ef4444; color: white; padding: 10px; border-radius: 50%; box-shadow: 0 4px 14px rgba(239, 68, 68, 0.4); border: 2.5px solid white; display: flex; align-items: center; justify-content: center;">
          <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><circle cx="12" cy="12" r="3"/></svg>
        </div>
      `;
      destinationMarker = new google.maps.marker.AdvancedMarkerElement({
        map,
        position: destPos,
        content: destEl,
        title: "Destino Pasajero"
      });
    } else {
      destinationMarker.position = destPos;
    }
  }

  map.setCenter(pickupPos);
}

function clearPassengerMarkers() {
  if (passengerMarker) {
    passengerMarker.setMap(null);
    passengerMarker = null;
  }
  if (destinationMarker) {
    destinationMarker.setMap(null);
    destinationMarker = null;
  }
}

let fallbackPickupPolyline = null;
let fallbackTripPolyline = null;

function drawFallbackPickupPolyline(origin, destination) {
  if (fallbackPickupPolyline) {
    fallbackPickupPolyline.setMap(null);
  }
  fallbackPickupPolyline = new google.maps.Polyline({
    path: [origin, destination],
    geodesic: true,
    strokeColor: "#3b82f6",
    strokeOpacity: 0.7,
    strokeWeight: 4,
    map: map
  });
}

function drawFallbackTripPolyline(origin, destination) {
  if (fallbackTripPolyline) {
    fallbackTripPolyline.setMap(null);
  }
  fallbackTripPolyline = new google.maps.Polyline({
    path: [origin, destination],
    geodesic: true,
    strokeColor: "#f59e0b",
    strokeOpacity: 0.8,
    strokeWeight: 5,
    map: map
  });
}

function clearFallbackPolylines() {
  if (fallbackPickupPolyline) {
    fallbackPickupPolyline.setMap(null);
    fallbackPickupPolyline = null;
  }
  if (fallbackTripPolyline) {
    fallbackTripPolyline.setMap(null);
    fallbackTripPolyline = null;
  }
}

let currentPickupPath = [];
let currentTripPath = [];
let driverSimulationInterval = null;

function runSimulation(path, onFinish) {
  if (driverSimulationInterval) clearInterval(driverSimulationInterval);
  if (!path || path.length === 0) {
    if (onFinish) onFinish();
    return;
  }
  let index = 0;
  const token = localStorage.getItem("driverAuthToken");

  driverSimulationInterval = setInterval(() => {
    if (index >= path.length) {
      clearInterval(driverSimulationInterval);
      driverSimulationInterval = null;
      if (onFinish) onFinish();
      return;
    }

    const nextPos = path[index];
    driverMarker.position = nextPos;
    map.setCenter(nextPos);

    if (token) {
      reportLocationToServer(token, nextPos.lat.toFixed(6), nextPos.lng.toFixed(6));
    }

    index++;
  }, 1000);
}

function startDriverSimulation() {
  showRideMessage("Iniciando simulación: Conduciendo hacia el pasajero...");
  runSimulation(currentPickupPath, () => {
    showRideMessage("¡Has llegado donde el pasajero! Esperando abordaje...");
    document.getElementById("mapOverlay").textContent = "Viaje Activo - Pasajero abordando";
    
    setTimeout(() => {
      showRideMessage("Simulación: Pasajero a bordo. Conduciendo al destino...");
      document.getElementById("mapOverlay").textContent = "Viaje Activo - Pasajero a bordo";
      runSimulation(currentTripPath, () => {
        showRideMessage("¡Has llegado al destino! Presiona Finalizar Viaje.");
        document.getElementById("mapOverlay").textContent = "Simulación: Llegaste al destino";
        const btn = document.getElementById("btnFinalizarViaje");
        if (btn) {
          btn.style.animation = "pulse 1.5s infinite";
        }
      });
    }, 2500);
  });
}

function traceRoutes(driverPos, passengerPickup, passengerDest) {
  clearFallbackPolylines();
  currentPickupPath = [];
  currentTripPath = [];

  // Trace route from Conductor to Passenger Pickup
  if (pickupDirectionsService && pickupDirectionsRenderer) {
    pickupDirectionsService.route({
      origin: driverPos,
      destination: passengerPickup,
      travelMode: google.maps.TravelMode.DRIVING
    }, (result, status) => {
      if (status === "OK") {
        pickupDirectionsRenderer.setDirections(result);
        currentPickupPath = result.routes[0].overview_path.map(p => ({ lat: p.lat(), lng: p.lng() }));
        startDriverSimulation();
      } else {
        fetchOSRMRoute(driverPos, passengerPickup).then(osrmResult => {
          if (osrmResult) {
            drawFallbackPickupPolyline(osrmResult.coords);
            currentPickupPath = osrmResult.coords;
          } else {
            drawFallbackPickupPolyline([driverPos, passengerPickup]);
            currentPickupPath = [driverPos, passengerPickup];
          }
          startDriverSimulation();
        });
        if (pickupDirectionsRenderer) {
          pickupDirectionsRenderer.set("directions", null);
        }
      }
    });
  }

  // Trace route from Passenger Pickup to Passenger Destination
  if (passengerDest && directionsService && directionsRenderer) {
    directionsService.route({
      origin: passengerPickup,
      destination: passengerDest,
      travelMode: google.maps.TravelMode.DRIVING
    }, (result, status) => {
      if (status === "OK") {
        directionsRenderer.setDirections(result);
        currentTripPath = result.routes[0].overview_path.map(p => ({ lat: p.lat(), lng: p.lng() }));
      } else {
        fetchOSRMRoute(passengerPickup, passengerDest).then(osrmResult => {
          if (osrmResult) {
            drawFallbackTripPolyline(osrmResult.coords);
            currentTripPath = osrmResult.coords;
          } else {
            drawFallbackTripPolyline([passengerPickup, passengerDest]);
            currentTripPath = [passengerPickup, passengerDest];
          }
        });
        if (directionsRenderer) {
          directionsRenderer.set("directions", null);
        }
      }
    });
  }
}

function clearRoutes() {
  if (directionsRenderer) {
    directionsRenderer.set("directions", null);
  }
  if (pickupDirectionsRenderer) {
    pickupDirectionsRenderer.set("directions", null);
  }
  clearFallbackPolylines();
  if (driverSimulationInterval) {
    clearInterval(driverSimulationInterval);
    driverSimulationInterval = null;
  }
  const btn = document.getElementById("btnFinalizarViaje");
  if (btn) {
    btn.style.animation = "";
  }
}

function updateRideList() {
  const list = document.getElementById("rideList");
  list.innerHTML = "";
  const entries = Array.from(rides.values());
  document.getElementById("rideCount").textContent = entries.length;

  if (entries.length === 0) {
    list.innerHTML = "<div class=\"status-placeholder\">Sin solicitudes activas en cola.</div>";
    return;
  }

  entries.forEach((ride) => {
    const card = document.createElement("div");
    card.className = "ride-card" + (ride.viajeId === activeRideId ? " active" : "");
    card.innerHTML = `
      <div><strong>${ride.pasajeroNombre || "Pasajero"}</strong></div>
      <div class="ride-meta">
        <span>Origen: ${Number(ride.origenLat).toFixed(5)}, ${Number(ride.origenLng).toFixed(5)}</span>
        <span>Destino: ${ride.destino || "No especificado"}</span>
        <span style="font-weight: 600; color: var(--accent);">${ride.estado || "SOLICITADO"}</span>
      </div>
      <div class="ride-actions">
        <button class="action-btn accept" data-action="accept" ${pendingRideActions.has(ride.viajeId) ? "disabled" : ""}>Aceptar</button>
        <button class="action-btn reject" data-action="reject" ${pendingRideActions.has(ride.viajeId) ? "disabled" : ""}>Rechazar</button>
      </div>
    `;

    card.addEventListener("click", () => {
      if (ride.origenLat && ride.origenLng) {
        setPassengerMarkers(ride.origenLat, ride.origenLng, ride.destinoLat, ride.destinoLng);
        document.getElementById("mapOverlay").textContent = `Detalles de solicitud - Pasajero: ${ride.pasajeroNombre}`;

        // Trace temporary preview route from driver to passenger
        const driverPos = readLatLng(driverMarker.position);
        if (driverPos) {
          const dPos = { lat: driverPos.lat(), lng: driverPos.lng() };
          const pPickup = { lat: Number(ride.origenLat), lng: Number(ride.origenLng) };
          const pDest = ride.destinoLat && ride.destinoLng ? { lat: Number(ride.destinoLat), lng: Number(ride.destinoLng) } : null;
          traceRoutes(dPos, pPickup, pDest, false);
        }
      }
      document.querySelectorAll(".ride-card").forEach(c => c.classList.remove("active"));
      card.classList.add("active");
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
  const token = localStorage.getItem("driverAuthToken");
  if (!token) {
    pendingRideActions.delete(viajeId);
    updateRideList();
    showRideMessage("Tu sesión no es válida. Inicia sesión nuevamente.", "error");
    return;
  }

  const endpoint = type === "ride-accept" ? "aceptar" : "rechazar";
  
  fetch(`/api/viajes/${viajeId}/${endpoint}`, {
    method: "POST",
    headers: {
      "Authorization": `Bearer ${token}`
    }
  })
    .then(async (response) => {
      pendingRideActions.delete(viajeId);
      if (response.status === 401 || response.status === 403) {
        localStorage.removeItem("driverAuthToken");
        window.location.href = "/auth/login-conductor.html";
        return;
      }
      if (!response.ok) {
        throw new Error("Error en la solicitud");
      }
      return response.json();
    })
    .then((data) => {
      if (!data) return;
      
      if (type === "ride-accept") {
        activeRideId = viajeId;
        showRideMessage(`Viaje #${viajeId} aceptado.`);
        
        const activeCard = document.getElementById("activeRideCard");
        activeCard.classList.remove("hidden");
        
        const ride = rides.get(viajeId);
        rides.delete(viajeId);
        
        document.getElementById("activePassengerName").textContent = ride ? ride.pasajeroNombre : "Pasajero";
        document.getElementById("activeRouteDetails").textContent = ride && ride.destino ? ride.destino : "Destino asignado";

        if (ride) {
          setPassengerMarkers(ride.origenLat, ride.origenLng, ride.destinoLat, ride.destinoLng);
          const driverPos = readLatLng(driverMarker.position);
          if (driverPos) {
            traceRoutes(
              { lat: driverPos.lat(), lng: driverPos.lng() },
              { lat: Number(ride.origenLat), lng: Number(ride.origenLng) },
              ride.destinoLat ? { lat: Number(ride.destinoLat), lng: Number(ride.destinoLng) } : null,
              true
            );
          }
        }
        document.getElementById("mapOverlay").textContent = "Viaje Activo - Dirígete al origen";
      } else {
        rides.delete(viajeId);
        if (activeRideId === viajeId) {
          activeRideId = null;
          document.getElementById("activeRideCard").classList.add("hidden");
          clearPassengerMarkers();
          clearRoutes();
        }
        showRideMessage(`Viaje #${viajeId} rechazado.`);
      }
      
      updateRideList();
    })
    .catch((err) => {
      pendingRideActions.delete(viajeId);
      updateRideList();
      showRideMessage("Error al procesar la solicitud: " + err.message, "error");
    });
}

function connectWebSocket() {
  const scheme = window.location.protocol === "https:" ? "wss" : "ws";
  const wsUrl = `${scheme}://${window.location.host}/ws/conductor`;
  ws = new WebSocket(wsUrl);

  ws.onopen = () => {
    const token = localStorage.getItem("driverAuthToken");
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
        showRideMessage(`Nueva solicitud de ${payload.pasajeroNombre}`);
      }

      if (payload.type === "ride-taken") {
        // Another driver accepted this ride, remove it from queue
        rides.delete(payload.viajeId);
        if (activeRideId === payload.viajeId) {
          activeRideId = null;
          document.getElementById("activeRideCard").classList.add("hidden");
          clearPassengerMarkers();
          clearRoutes();
        }
        updateRideList();
      }

      if (payload.type === "ride-accepted") {
        pendingRideActions.delete(payload.viajeId);
        const ride = rides.get(payload.viajeId);
        rides.delete(payload.viajeId);
        
        activeRideId = payload.viajeId;
        showRideMessage(`Viaje #${payload.viajeId} aceptado.`);
        
        // Show Active Trip Card
        const activeCard = document.getElementById("activeRideCard");
        activeCard.classList.remove("hidden");
        document.getElementById("activePassengerName").textContent = ride ? ride.pasajeroNombre : "Pasajero";
        document.getElementById("activeRouteDetails").textContent = ride && ride.destino ? ride.destino : "Destino asignado";

        if (ride) {
          setPassengerMarkers(ride.origenLat, ride.origenLng, ride.destinoLat, ride.destinoLng);
          const driverPos = readLatLng(driverMarker.position);
          if (driverPos) {
            traceRoutes(
              { lat: driverPos.lat(), lng: driverPos.lng() },
              { lat: Number(ride.origenLat), lng: Number(ride.origenLng) },
              ride.destinoLat ? { lat: Number(ride.destinoLat), lng: Number(ride.destinoLng) } : null,
              true
            );
          }
        }

        updateRideList();
        document.getElementById("mapOverlay").textContent = "Viaje Activo - Dirígete al origen";
      }

      if (payload.type === "ride-rejected") {
        pendingRideActions.delete(payload.viajeId);
        rides.delete(payload.viajeId);
        if (activeRideId === payload.viajeId) {
          activeRideId = null;
          document.getElementById("activeRideCard").classList.add("hidden");
          clearPassengerMarkers();
          clearRoutes();
        }
        showRideMessage(`Viaje #${payload.viajeId} rechazado.`);
        updateRideList();
      }

      if (payload.type === "error") {
        showRideMessage(payload.message || "Ocurrió un error al procesar.", "error");
        if (/token invalido|expirado|no autorizado|rol no autorizado/i.test(payload.message || "")) {
          localStorage.removeItem("driverAuthToken");
          window.location.href = "/auth/login-conductor.html";
        }
      }
    } catch (error) {
      // ignore
    }
  };

  ws.onclose = () => {
    pendingRideActions.clear();
    updateRideList();
    showRideMessage("Conexión WebSocket cerrada. Recarga la página.", "error");
  };
}

function setupFinishRide() {
  const btnFinalizar = document.getElementById("btnFinalizarViaje");
  btnFinalizar.addEventListener("click", () => {
    if (!activeRideId) {
      return;
    }
    const token = localStorage.getItem("driverAuthToken");
    if (!token) {
      return;
    }

    if (confirm("¿Estás seguro de que deseas finalizar este viaje?")) {
      fetch(`/api/viajes/${activeRideId}/finalizar`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          "Authorization": `Bearer ${token}`
        }
      })
        .then(async (response) => {
          if (!response.ok) {
            throw new Error("No se pudo finalizar el viaje");
          }
          showRideMessage("¡Viaje finalizado exitosamente!");
          
          // Clear active ride state
          activeRideId = null;
          document.getElementById("activeRideCard").classList.add("hidden");
          clearPassengerMarkers();
          clearRoutes();
          document.getElementById("mapOverlay").textContent = "Esperando solicitudes...";
          updateRideList();
        })
        .catch((err) => {
          showRideMessage(err.message, "error");
        });
    }
  });
}

async function cargarPerfil() {
  const token = localStorage.getItem("driverAuthToken");
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
    document.getElementById("profileRating").textContent = profile.calificacionPromedio != null ? `★ ${profile.calificacionPromedio.toFixed(1)}` : "Nuevo";
    document.getElementById("profileStatus").textContent = profile.disponible ? "Disponible" : "No disponible";
  } catch (error) {
    // ignore
  }
}

function sendDriverLocation() {
  const token = localStorage.getItem("driverAuthToken");
  if (!token) {
    return;
  }

  // Use FIXED_COORDS or getCurrentPosition
  if (FIXED_COORDS) {
    const lat = FIXED_COORDS.lat.toFixed(6);
    const lng = FIXED_COORDS.lng.toFixed(6);
    reportLocationToServer(token, lat, lng);
    return;
  }

  if (navigator.geolocation) {
    navigator.geolocation.getCurrentPosition((position) => {
      const lat = position.coords.latitude.toFixed(6);
      const lng = position.coords.longitude.toFixed(6);
      reportLocationToServer(token, lat, lng);
    });
  }
}

function reportLocationToServer(token, lat, lng) {
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
  createDriverMarker(coords);
}

function setupLogout() {
  const logoutButton = document.getElementById("logoutButton");
  logoutButton.addEventListener("click", async () => {
    const token = localStorage.getItem("driverAuthToken");
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
    localStorage.removeItem("driverAuthToken");
    window.location.href = "/auth/login-conductor.html";
  });
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

async function fetchOSRMRoute(origin, destination) {
  const oLat = typeof origin.lat === "function" ? origin.lat() : origin.lat;
  const oLng = typeof origin.lng === "function" ? origin.lng() : origin.lng;
  const dLat = typeof destination.lat === "function" ? destination.lat() : destination.lat;
  const dLng = typeof destination.lng === "function" ? destination.lng() : destination.lng;
  
  const url = `https://router.project-osrm.org/route/v1/driving/${oLng},${oLat};${dLng},${dLat}?overview=full&geometries=geojson`;
  try {
    const response = await fetch(url);
    if (!response.ok) throw new Error("OSRM error");
    const data = await response.json();
    if (data.code === "Ok" && data.routes && data.routes.length > 0) {
      const route = data.routes[0];
      const coords = route.geometry.coordinates.map(coord => ({
        lat: coord[1],
        lng: coord[0]
      }));
      return { coords };
    }
  } catch (error) {
    console.error("Failed to fetch OSRM route:", error);
  }
  return null;
}

async function recargarSolicitudes() {
  const token = localStorage.getItem("driverAuthToken");
  if (!token) {
    window.location.href = "/auth/login-conductor.html";
    return;
  }

  // If WebSocket is closed or in error state, try to reconnect
  if (!ws || ws.readyState === WebSocket.CLOSED) {
    showRideMessage("Intentando reconectar WebSocket...");
    connectWebSocket();
  }

  try {
    const response = await fetch("/api/viajes/pendientes", {
      headers: {
        "Authorization": `Bearer ${token}`
      }
    });

    if (response.status === 401 || response.status === 403) {
      localStorage.removeItem("driverAuthToken");
      window.location.href = "/auth/login-conductor.html";
      return;
    }

    if (!response.ok) {
      showRideMessage("Error al cargar solicitudes pendientes", "error");
      return;
    }

    const pendientes = await response.json();
    rides.clear();
    pendientes.forEach((ride) => {
      rides.set(ride.idViaje, {
        viajeId: ride.idViaje,
        pasajeroId: ride.pasajeroId,
        pasajeroNombre: ride.pasajeroNombre,
        origenLat: ride.origenLat,
        origenLng: ride.origenLng,
        destinoLat: ride.destinoLat,
        destinoLng: ride.destinoLng,
        destino: null,
        estado: ride.estado
      });
    });
    updateRideList();
    showRideMessage("Solicitudes actualizadas correctamente.");
  } catch (error) {
    showRideMessage("Error al conectar con el servidor", "error");
  }
}

document.getElementById("sendLocationButton").addEventListener("click", sendDriverLocation);
document.getElementById("btnRecargarSolicitudes").addEventListener("click", recargarSolicitudes);

setupLogout();
window.initMap = initMap;
