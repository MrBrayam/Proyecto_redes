let map;
let pickupMarker;
let destinationMarker;
let driverMarker;
const nearbyDriverMarkers = new Map();
let ws;
let pendingRequest = false;
let destinationLat = null;
let destinationLng = null;
let selectedConductor = null;
let activeRideId = null;
let sessionChecked = false;
let driverRouteDrawn = false;
let directionsService;
let directionsRenderer;
let driverDirectionsService;
let driverDirectionsRenderer;

// Rating variables
let selectedRating = 5;
const ratingLabels = {
  1: "Muy malo 😠",
  2: "Malo ☹️",
  3: "Regular 😐",
  4: "Bueno 🙂",
  5: "Excelente 😄"
};

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

  driverDirectionsService = new google.maps.DirectionsService();
  driverDirectionsRenderer = new google.maps.DirectionsRenderer({
    map,
    suppressMarkers: true,
    polylineOptions: {
      strokeColor: "#3b82f6", // Blue
      strokeWeight: 4,
      strokeOpacity: 0.7
    }
  });

  // Custom Pickup Element (SVG Pin)
  const pickupEl = document.createElement("div");
  pickupEl.innerHTML = `
    <div style="background-color: #3b82f6; color: white; padding: 10px; border-radius: 50%; box-shadow: 0 4px 14px rgba(59, 130, 246, 0.4); border: 2.5px solid white; display: flex; align-items: center; justify-content: center; transform: scale(1.1);">
      <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><path d="M20 10c0 6-8 12-8 12s-8-6-8-12a8 8 0 0 1 16 0Z"/><circle cx="12" cy="10" r="3"/></svg>
    </div>
  `;

  pickupMarker = new google.maps.marker.AdvancedMarkerElement({
    map,
    position: defaultPos,
    content: pickupEl,
    title: "Origen (Arrastrame)",
    gmpDraggable: true
  });

  // Map click for destination
  map.addListener("click", (event) => {
    if (!event.latLng) {
      return;
    }
    const pos = { lat: event.latLng.lat(), lng: event.latLng.lng() };
    destinationLat = pos.lat;
    destinationLng = pos.lng;
    setDestinationMarker(pos);
    updateDestinoInputFromCoords(pos.lat, pos.lng);
    updateTripPreview();
  });

  pickupMarker.addListener("dragend", updateCoords);

  // Setup Autocomplete
  initAutocomplete();

  // Load coordinates initially
  updateCoords();
  connectWebSocket();
  cargarConductores();
  validarSesion();
}

function initAutocomplete() {
  const destinoInput = document.getElementById("destino");
  const autocomplete = new google.maps.places.Autocomplete(destinoInput, {
    fields: ["geometry", "formatted_address", "name"],
    types: ["geocode", "establishment"]
  });

  autocomplete.addListener("place_changed", () => {
    const place = autocomplete.getPlace();
    if (!place.geometry || !place.geometry.location) {
      return;
    }
    const pos = { lat: place.geometry.location.lat(), lng: place.geometry.location.lng() };
    destinationLat = pos.lat;
    destinationLng = pos.lng;
    setDestinationMarker(pos);
    updateTripPreview();
  });
}

function updateCoords() {
  const pos = readLatLng(pickupMarker.position);
  if (!pos) {
    return;
  }
  document.getElementById("origenLat").value = pos.lat().toFixed(6);
  document.getElementById("origenLng").value = pos.lng().toFixed(6);
  updateTripPreview();
}

function updateDestinoInputFromCoords(lat, lng) {
  const destinoInput = document.getElementById("destino");
  if (!google.maps.Geocoder) {
    destinoInput.value = `${Number(lat).toFixed(5)}, ${Number(lng).toFixed(5)}`;
    return;
  }

  const geocoder = new google.maps.Geocoder();
  geocoder.geocode({ location: { lat: Number(lat), lng: Number(lng) } }, (results, status) => {
    if (status === "OK" && results && results.length > 0) {
      destinoInput.value = results[0].formatted_address;
      return;
    }
    destinoInput.value = `${Number(lat).toFixed(5)}, ${Number(lng).toFixed(5)}`;
  });
}

function setTripPreview(distanceText, durationText, fareText) {
  document.getElementById("routeDistance").textContent = distanceText;
  document.getElementById("routeDuration").textContent = durationText;
  document.getElementById("routeFare").textContent = fareText;
}

let fallbackPolyline = null;

function haversineDistance(lat1, lon1, lat2, lon2) {
  const R = 6371; // km
  const dLat = (lat2 - lat1) * Math.PI / 180;
  const dLon = (lon2 - lon1) * Math.PI / 180;
  const a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
            Math.cos(lat1 * Math.PI / 180) * Math.cos(lat2 * Math.PI / 180) *
            Math.sin(dLon / 2) * Math.sin(dLon / 2);
  const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
  return R * c;
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
      const distanceKm = route.distance / 1000;
      const durationMin = route.duration / 60;
      return { coords, distanceKm, durationMin };
    }
  } catch (error) {
    console.error("Failed to fetch OSRM route:", error);
  }
  return null;
}

function drawFallbackPolyline(pathOrOrigin, destination) {
  if (fallbackPolyline) {
    fallbackPolyline.setMap(null);
  }
  let path = [];
  if (Array.isArray(pathOrOrigin)) {
    path = pathOrOrigin;
  } else {
    path = [pathOrOrigin, destination];
  }
  fallbackPolyline = new google.maps.Polyline({
    path: path,
    geodesic: true,
    strokeColor: "#f59e0b",
    strokeOpacity: 0.8,
    strokeWeight: 5,
    map: map
  });
}

function clearFallbackPolyline() {
  if (fallbackPolyline) {
    fallbackPolyline.setMap(null);
    fallbackPolyline = null;
  }
}

async function updateTripPreview() {
  const origenLat = Number(document.getElementById("origenLat").value);
  const origenLng = Number(document.getElementById("origenLng").value);
  if (!Number.isFinite(origenLat) || !Number.isFinite(origenLng) || destinationLat == null || destinationLng == null) {
    setTripPreview("-", "-", "-");
    if (directionsRenderer) {
      directionsRenderer.set("directions", null);
    }
    clearFallbackPolyline();
    return;
  }

  if (directionsService && directionsRenderer) {
    directionsService.route({
      origin: { lat: origenLat, lng: origenLng },
      destination: { lat: Number(destinationLat), lng: Number(destinationLng) },
      travelMode: google.maps.TravelMode.DRIVING
    }, async (result, status) => {
      if (status !== "OK" || !result || !result.routes || result.routes.length === 0) {
        clearFallbackPolyline();
        drawFallbackPolyline({ lat: origenLat, lng: origenLng }, { lat: Number(destinationLat), lng: Number(destinationLng) });
        
        const fallbackDist = haversineDistance(origenLat, origenLng, Number(destinationLat), Number(destinationLng));
        const distanceText = `${fallbackDist.toFixed(2)} km (Línea recta)`;
        const durationText = `${Math.round(fallbackDist * 2.5)} min (est.)`;
        
        const fareText = await estimateFare(origenLat, origenLng, Number(destinationLat), Number(destinationLng));
        setTripPreview(distanceText, durationText, fareText);
        
        if (directionsRenderer) {
          directionsRenderer.set("directions", null);
        }
        return;
      }

      clearFallbackPolyline();
      directionsRenderer.setDirections(result);
      const leg = result.routes[0].legs && result.routes[0].legs[0];
      const distanceText = leg?.distance?.text || "-";
      const durationText = leg?.duration?.text || "-";

      const fareText = await estimateFare(origenLat, origenLng, Number(destinationLat), Number(destinationLng));
      setTripPreview(distanceText, durationText, fareText);
    });
  }
}

async function estimateFare(origenLat, origenLng, destinoLat, destinoLng) {
  try {
    const response = await fetch("/api/viajes/calcular-tarifa", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        origenLat,
        origenLng,
        destinoLat,
        destinoLng,
        destinoTexto: document.getElementById("destino").value || "Destino"
      })
    });
    if (!response.ok) {
      return "No disponible";
    }
    const data = await response.json();
    if (!data || data.tarifaEstimada == null) {
      return "No disponible";
    }
    return `S/. ${Number(data.tarifaEstimada).toFixed(2)}`;
  } catch (error) {
    return "No disponible";
  }
}

function connectWebSocket() {
  const indicator = document.getElementById("wsIndicator");
  const status = document.getElementById("wsStatus");
  const scheme = window.location.protocol === "https:" ? "wss" : "ws";
  const wsUrl = `${scheme}://${window.location.host}/ws/pasajero`;

  ws = new WebSocket(wsUrl);

  ws.onopen = () => {
    indicator.classList.add("connected");
    status.textContent = "WebSocket: Conectado";
    const token = localStorage.getItem("passengerAuthToken");
    if (token) {
      ws.send(JSON.stringify({ type: "passenger-connect", token }));
    }
  };

  ws.onmessage = (event) => {
    try {
      const payload = JSON.parse(event.data);

      if (payload.type === "driver-location" && payload.lat && payload.lng) {
        showDriverMarker(payload.lat, payload.lng);
        
        if (!driverRouteDrawn) {
          driverRouteDrawn = true;
          const passengerLat = Number(document.getElementById("origenLat").value);
          const passengerLng = Number(document.getElementById("origenLng").value);
          traceDriverToPassenger(payload.lat, payload.lng, passengerLat, passengerLng);
        }
      }

      if (payload.type === "ride-status" && payload.status) {
        document.getElementById("rideStatus").textContent = payload.status;
        handleRideStatusTransition(payload);
      }

      if (payload.type === "error" && payload.message) {
        document.getElementById("rideStatus").textContent = payload.message;
        if (/token invalido|expirado|no autorizado/i.test(payload.message)) {
          manejarSesionInvalida();
        }
      }
    } catch (error) {
      // ignore
    }
  };

  ws.onclose = () => {
    indicator.classList.remove("connected");
    status.textContent = "WebSocket: Desconectado";
  };

  ws.onerror = () => {
    indicator.classList.remove("connected");
    status.textContent = "WebSocket: Error";
  };
}

let fallbackDriverPolyline = null;
function drawFallbackDriverPolyline(pathOrOrigin, destination) {
  if (fallbackDriverPolyline) {
    fallbackDriverPolyline.setMap(null);
  }
  let path = [];
  if (Array.isArray(pathOrOrigin)) {
    path = pathOrOrigin;
  } else {
    path = [pathOrOrigin, destination];
  }
  fallbackDriverPolyline = new google.maps.Polyline({
    path: path,
    geodesic: true,
    strokeColor: "#3b82f6",
    strokeOpacity: 0.7,
    strokeWeight: 4,
    map: map
  });
}

function clearFallbackDriverPolyline() {
  if (fallbackDriverPolyline) {
    fallbackDriverPolyline.setMap(null);
    fallbackDriverPolyline = null;
  }
}

function traceDriverToPassenger(driverLat, driverLng, passengerLat, passengerLng) {
  if (driverDirectionsService && driverDirectionsRenderer) {
    driverDirectionsService.route({
      origin: { lat: Number(driverLat), lng: Number(driverLng) },
      destination: { lat: Number(passengerLat), lng: Number(passengerLng) },
      travelMode: google.maps.TravelMode.DRIVING
    }, (result, status) => {
      if (status === "OK") {
        clearFallbackDriverPolyline();
        driverDirectionsRenderer.setDirections(result);
      } else {
        clearFallbackDriverPolyline();
        fetchOSRMRoute(
          { lat: Number(driverLat), lng: Number(driverLng) },
          { lat: Number(passengerLat), lng: Number(passengerLng) }
        ).then(osrmResult => {
          if (osrmResult) {
            drawFallbackDriverPolyline(osrmResult.coords);
          } else {
            drawFallbackDriverPolyline(
              { lat: Number(driverLat), lng: Number(driverLng) },
              { lat: Number(passengerLat), lng: Number(passengerLng) }
            );
          }
        });
        if (driverDirectionsRenderer) {
          driverDirectionsRenderer.set("directions", null);
        }
      }
    });
  }
}

function handleRideStatusTransition(payload) {
  const overlay = document.getElementById("activeRideOverlay");
  const overlayTitle = document.getElementById("overlayTitle");
  const overlayMessage = document.getElementById("overlayMessage");
  const driverCard = document.getElementById("overlayDriverCard");
  const driverName = document.getElementById("overlayDriverName");
  const driverVehicle = document.getElementById("overlayDriverVehicle");

  if (payload.viajeId) {
    activeRideId = payload.viajeId;
  }

  switch (payload.status) {
    case "SOLICITADO":
      overlay.classList.remove("hidden");
      overlay.classList.remove("floating");
      overlayTitle.textContent = "Buscando Conductor";
      overlayMessage.textContent = "Hemos enviado tu solicitud a los conductores disponibles cercanos...";
      driverCard.classList.add("hidden");
      break;

    case "ACEPTADO":
      overlay.classList.remove("hidden");
      overlay.classList.add("floating");
      overlayTitle.textContent = "¡Conductor en camino!";
      overlayMessage.textContent = "El conductor está yendo a recogerte.";
      driverCard.classList.remove("hidden");
      driverName.textContent = payload.conductorNombre || "Conductor";
      driverVehicle.textContent = "Auto asignado";
      break;

    case "RECHAZADO":
      overlay.classList.add("hidden");
      overlay.classList.remove("floating");
      alert("El conductor rechazó la solicitud. Puedes elegir otro conductor o solicitar un viaje general.");
      resetActiveRideState();
      break;

    case "CANCELADO":
      overlay.classList.add("hidden");
      overlay.classList.remove("floating");
      alert("El viaje fue cancelado.");
      resetActiveRideState();
      break;

    case "FINALIZADO":
      overlay.classList.add("hidden");
      overlay.classList.remove("floating");
      openRatingModal();
      break;

    default:
      break;
  }
}

function resetActiveRideState() {
  activeRideId = null;
  pendingRequest = false;
  selectedConductor = null;
  document.getElementById("rideStatus").textContent = "Sin solicitud activa";
  if (driverMarker) {
    driverMarker.setMap(null);
    driverMarker = null;
  }
  if (driverDirectionsRenderer) {
    driverDirectionsRenderer.set("directions", null);
  }
  clearFallbackPolyline();
  clearFallbackDriverPolyline();
  driverRouteDrawn = false;
  const overlay = document.getElementById("activeRideOverlay");
  if (overlay) {
    overlay.classList.remove("floating");
  }
}

function showDriverMarker(lat, lng) {
  const position = { lat: Number(lat), lng: Number(lng) };
  if (!driverMarker) {
    const driverEl = document.createElement("div");
    driverEl.innerHTML = `
      <div style="background-color: #10b981; color: white; padding: 8px; border-radius: 50%; box-shadow: 0 4px 10px rgba(16, 185, 129, 0.4); border: 2px solid white; display: flex; align-items: center; justify-content: center;">
        <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M19 17h2c.6 0 1-.4 1-1v-3c0-.9-.7-1.7-1.5-1.9C18.7 10.6 16 10 16 10s-1.3-1.4-2.2-2.3c-.5-.4-1.1-.7-1.8-.7H5c-.6 0-1.1.4-1.4.9l-1.4 2.9A3.7 3.7 0 0 0 2 12v4c0 .6.4 1 1 1h2"/><circle cx="7" cy="17" r="2"/><path d="M9 17h6"/><circle cx="17" cy="17" r="2"/></svg>
      </div>
    `;
    driverMarker = new google.maps.marker.AdvancedMarkerElement({
      map,
      position,
      content: driverEl,
      title: "Conductor Asignado"
    });
  } else {
    driverMarker.position = position;
  }
}

function setDestinationMarker(position) {
  if (!destinationMarker) {
    const destEl = document.createElement("div");
    destEl.innerHTML = `
      <div style="background-color: #ef4444; color: white; padding: 10px; border-radius: 50%; box-shadow: 0 4px 14px rgba(239, 68, 68, 0.4); border: 2.5px solid white; display: flex; align-items: center; justify-content: center;">
        <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><circle cx="12" cy="12" r="3"/></svg>
      </div>
    `;
    destinationMarker = new google.maps.marker.AdvancedMarkerElement({
      map,
      position,
      content: destEl,
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
  const token = localStorage.getItem("passengerAuthToken");

  if (!destino && (destinationLat == null || destinationLng == null)) {
    alert("Por favor selecciona un destino en el mapa o búscalo en el buscador.");
    return;
  }
  if (!token) {
    document.getElementById("rideStatus").textContent = "Inicia sesión para solicitar";
    return;
  }

  pendingRequest = true;
  document.getElementById("rideStatus").textContent = selectedConductor
    ? `Solicitando a ${selectedConductor.nombre}`
    : "Buscando conductor...";

  document.getElementById("activeRideOverlay").classList.remove("hidden");
  document.getElementById("overlayTitle").textContent = "Enviando Solicitud";
  document.getElementById("overlayMessage").textContent = selectedConductor 
    ? `Solicitando viaje al conductor ${selectedConductor.nombre}...` 
    : "Buscando al conductor más cercano...";

  if (destinationLat != null && destinationLng != null) {
    enviarSolicitud(token, origenLat, origenLng, destinationLat, destinationLng, destino);
    return;
  }

  const geocoder = new google.maps.Geocoder();
  geocoder.geocode({ address: destino }, (results, status) => {
    if (status !== "OK" || !results || results.length === 0) {
      document.getElementById("rideStatus").textContent = "Error de geolocalización de destino";
      pendingRequest = false;
      document.getElementById("activeRideOverlay").classList.add("hidden");
      return;
    }

    const destinoLatResult = results[0].geometry.location.lat().toFixed(6);
    const destinoLngResult = results[0].geometry.location.lng().toFixed(6);
    enviarSolicitud(token, origenLat, origenLng, destinoLatResult, destinoLngResult, destino);
  });
}

function enviarSolicitud(token, origenLat, origenLng, destinoLatValue, destinoLngValue, destino) {
  const body = {
    origenLat: Number(origenLat),
    origenLng: Number(origenLng),
    destinoLat: Number(destinoLatValue),
    destinoLng: Number(destinoLngValue),
    destinoTexto: destino || "Destino",
    conductorId: selectedConductor ? selectedConductor.idConductor : null
  };

  fetch("/api/viajes", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      "Authorization": `Bearer ${token}`
    },
    body: JSON.stringify(body)
  })
    .then(async (response) => {
      if (!response.ok) {
        if (response.status === 401 || response.status === 403) {
          manejarSesionInvalida();
          return;
        }
        let errMsg = "Error al solicitar viaje";
        try {
          const payload = await response.json();
          errMsg = payload.message || payload.error || errMsg;
        } catch(e) {}
        throw new Error(errMsg);
      }
      return response.json();
    })
    .then((data) => {
      if (!data) return;
      activeRideId = data.idViaje;
      document.getElementById("rideStatus").textContent = "SOLICITADO";
      document.getElementById("overlayTitle").textContent = "Buscando Conductor";
      document.getElementById("overlayMessage").textContent = selectedConductor
        ? `Esperando respuesta de ${selectedConductor.nombre}...`
        : "Esperando que algún conductor disponible tome tu solicitud...";
    })
    .catch((error) => {
      if (error && error.message) {
        alert(error.message);
      }
      document.getElementById("activeRideOverlay").classList.add("hidden");
      resetActiveRideState();
    });
}

async function cargarConductores() {
  const token = localStorage.getItem("passengerAuthToken");
  const driversList = document.getElementById("driversList");
  if (!driversList) {
    return;
  }

  driversList.innerHTML = "<div class=\"status-placeholder\">Cargando conductores cercanos...</div>";
  if (!token) {
    driversList.innerHTML = "<div class=\"status-placeholder\">Inicia sesión para ver conductores cercanos.</div>";
    return;
  }

  const origenLat = document.getElementById("origenLat").value;
  const origenLng = document.getElementById("origenLng").value;
  if (!origenLat || !origenLng) {
    driversList.innerHTML = "<div class=\"status-placeholder\">Selecciona tu punto de origen arrastrando el marcador azul.</div>";
    return;
  }

  try {
    const response = await fetch(`/api/pasajero/conductores/cercanos?lat=${origenLat}&lng=${origenLng}`, {
      headers: { "Authorization": `Bearer ${token}` }
    });
    if (response.status === 401 || response.status === 403) {
      manejarSesionInvalida();
      return;
    }
    if (!response.ok) {
      driversList.innerHTML = "<div class=\"status-placeholder\">No se pudieron cargar conductores.</div>";
      return;
    }
    const conductores = await response.json();
    renderDrivers(conductores || []);
  } catch (error) {
    driversList.innerHTML = "<div class=\"status-placeholder\">Error al cargar conductores.</div>";
  }
}

function renderDrivers(conductores) {
  const driversList = document.getElementById("driversList");
  const driversCount = document.getElementById("driversCount");
  driversList.innerHTML = "";
  driversCount.textContent = conductores.length;

  // Clear existing nearby markers
  nearbyDriverMarkers.forEach((marker) => marker.setMap(null));
  nearbyDriverMarkers.clear();

  if (conductores.length === 0) {
    driversList.innerHTML = "<div class=\"status-placeholder\">No hay conductores cercanos activos en este momento.</div>";
    return;
  }

  conductores.forEach((conductor) => {
    // Add Marker on map for each nearby driver
    if (conductor.lat && conductor.lng) {
      const carPos = { lat: Number(conductor.lat), lng: Number(conductor.lng) };
      const carEl = document.createElement("div");
      carEl.innerHTML = `
        <div style="background-color: #2b8a6e; color: white; padding: 6px; border-radius: 50%; box-shadow: 0 4px 8px rgba(0,0,0,0.3); border: 2px solid white; display: flex; align-items: center; justify-content: center; cursor: pointer;">
          <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M19 17h2c.6 0 1-.4 1-1v-3c0-.9-.7-1.7-1.5-1.9C18.7 10.6 16 10 16 10s-1.3-1.4-2.2-2.3c-.5-.4-1.1-.7-1.8-.7H5c-.6 0-1.1.4-1.4.9l-1.4 2.9A3.7 3.7 0 0 0 2 12v4c0 .6.4 1 1 1h2"/><circle cx="7" cy="17" r="2"/><path d="M9 17h6"/><circle cx="17" cy="17" r="2"/></svg>
        </div>
      `;
      const carMarker = new google.maps.marker.AdvancedMarkerElement({
        map,
        position: carPos,
        content: carEl,
        title: conductor.nombre
      });
      carMarker.addListener("click", () => openDriverModal(conductor));
      nearbyDriverMarkers.set(conductor.idConductor, carMarker);
    }

    const card = document.createElement("div");
    card.className = "driver-card";
    card.innerHTML = `
      <div class="driver-card-info">
        <strong>${conductor.nombre}</strong>
        <span>${conductor.vehiculo || "Vehículo no registrado"}</span>
        <span style="font-size: 11px; color: var(--accent);">★ ${conductor.calificacionPromedio != null ? conductor.calificacionPromedio.toFixed(1) : "Nuevo"}</span>
      </div>
      <button class="driver-card-btn" type="button">Ver</button>
    `;
    card.querySelector(".driver-card-btn").addEventListener("click", (e) => {
      e.stopPropagation();
      openDriverModal(conductor);
    });
    card.addEventListener("click", () => {
      // Focus map on driver
      if (conductor.lat && conductor.lng) {
        map.setCenter({ lat: Number(conductor.lat), lng: Number(conductor.lng) });
        map.setZoom(16);
      }
      document.querySelectorAll(".driver-card").forEach(c => c.classList.remove("selected"));
      card.classList.add("selected");
    });
    driversList.appendChild(card);
  });
}

function openDriverModal(conductor) {
  selectedConductor = conductor;
  document.getElementById("modalDriverName").textContent = conductor.nombre || "Conductor";
  document.getElementById("modalVehicle").textContent = conductor.vehiculo || "-";
  document.getElementById("modalRating").textContent = conductor.calificacionPromedio != null ? `★ ${conductor.calificacionPromedio.toFixed(1)}` : "Nuevo";
  document.getElementById("modalDistance").textContent = conductor.distanciaKm < 0
    ? "Sin ubicación"
    : `${conductor.distanciaKm.toFixed(2)} km`;
  document.getElementById("driverModal").classList.remove("hidden");
}

function closeDriverModal() {
  document.getElementById("driverModal").classList.add("hidden");
}

function openRatingModal() {
  document.getElementById("ratingModal").classList.remove("hidden");
  // Reset stars
  selectedRating = 5;
  updateStars(selectedRating);
  document.getElementById("ratingComment").value = "";
}

function closeRatingModal() {
  document.getElementById("ratingModal").classList.add("hidden");
}

function updateStars(val) {
  const stars = document.querySelectorAll("#starRatingContainer .star");
  const starLabel = document.getElementById("starLabel");
  stars.forEach(s => {
    const v = parseInt(s.dataset.value);
    if (v <= val) {
      s.classList.add("active");
    } else {
      s.classList.remove("active");
    }
  });
  starLabel.textContent = ratingLabels[val];
}

// GPS Location handling
document.getElementById("btnUsarUbicacion").addEventListener("click", () => {
  if (navigator.geolocation) {
    navigator.geolocation.getCurrentPosition((position) => {
      const pos = { lat: position.coords.latitude, lng: position.coords.longitude };
      pickupMarker.position = pos;
      map.setCenter(pos);
      document.getElementById("origenLat").value = pos.lat.toFixed(6);
      document.getElementById("origenLng").value = pos.lng.toFixed(6);
      updateTripPreview();
      cargarConductores();
    }, (error) => {
      alert("No se pudo obtener tu ubicación. Por favor, arrastra el marcador azul.");
    });
  } else {
    alert("Geolocalización no es soportada en este navegador.");
  }
});

// Cancel active trip button
document.getElementById("btnCancelarViajeActivo").addEventListener("click", () => {
  if (!activeRideId) {
    return;
  }
  const token = localStorage.getItem("passengerAuthToken");
  if (!token) {
    return;
  }

  if (confirm("¿Estás seguro de que deseas cancelar este viaje? Puede aplicarse una multa de cancelación.")) {
    fetch(`/api/viajes/${activeRideId}/cancelar`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        "Authorization": `Bearer ${token}`
      },
      body: JSON.stringify({ motivo: "Cancelado por el pasajero desde el panel" })
    })
      .then(async (response) => {
        if (!response.ok) {
          throw new Error("No se pudo cancelar el viaje");
        }
        document.getElementById("activeRideOverlay").classList.add("hidden");
        resetActiveRideState();
        alert("Viaje cancelado exitosamente.");
      })
      .catch((err) => {
        alert(err.message);
      });
  }
});

// Star Rating Setup
const starsElements = document.querySelectorAll("#starRatingContainer .star");
starsElements.forEach((star) => {
  star.addEventListener("click", () => {
    selectedRating = parseInt(star.dataset.value);
    updateStars(selectedRating);
  });
});

document.getElementById("submitRating").addEventListener("click", () => {
  if (!activeRideId) {
    closeRatingModal();
    return;
  }
  const token = localStorage.getItem("passengerAuthToken");
  if (!token) {
    return;
  }

  const comment = document.getElementById("ratingComment").value.trim();

  fetch(`/api/viajes/${activeRideId}/calificar`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      "Authorization": `Bearer ${token}`
    },
    body: JSON.stringify({
      puntuacion: selectedRating,
      comentario: comment || "Excelente servicio"
    })
  })
    .then(async (response) => {
      if (!response.ok) {
        throw new Error("No se pudo enviar la calificación");
      }
      closeRatingModal();
      alert("¡Gracias por calificar tu viaje!");
      resetActiveRideState();
    })
    .catch((err) => {
      alert(err.message);
      closeRatingModal();
      resetActiveRideState();
    });
});

document.getElementById("buscarConductores").addEventListener("click", cargarConductores);
document.getElementById("closeModal").addEventListener("click", closeDriverModal);
document.getElementById("requestDriver").addEventListener("click", () => {
  if (!selectedConductor) {
    return;
  }
  closeDriverModal();
  solicitarViaje();
});

async function validarSesion() {
  if (sessionChecked) {
    return;
  }
  sessionChecked = true;
  const token = localStorage.getItem("passengerAuthToken");
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
  localStorage.removeItem("passengerAuthToken");
  window.location.href = "/auth/login-cliente.html";
}

window.initMap = initMap;
window.solicitarViaje = solicitarViaje;
