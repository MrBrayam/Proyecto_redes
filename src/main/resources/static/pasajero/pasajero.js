let map;
let pickupMarker;
let driverMarker;
let ws;

function initMap() {
  const defaultPos = { lat: -6.501, lng: -76.365 };
  map = new google.maps.Map(document.getElementById("map"), {
    center: defaultPos,
    zoom: 14,
    mapTypeControl: false
  });

  pickupMarker = new google.maps.Marker({
    map,
    position: defaultPos,
    draggable: true,
    title: "Punto de recogida"
  });

  map.addListener("click", (event) => {
    pickupMarker.setPosition(event.latLng);
    updateCoords();
  });

  pickupMarker.addListener("dragend", updateCoords);
  updateCoords();
  connectWebSocket();
}

function updateCoords() {
  const pos = pickupMarker.getPosition();
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
    driverMarker = new google.maps.Marker({
      map,
      position,
      title: "Conductor"
    });
  } else {
    driverMarker.setPosition(position);
  }
}

function solicitarViaje() {
  const origenLat = document.getElementById("origenLat").value;
  const origenLng = document.getElementById("origenLng").value;
  const destino = document.getElementById("destino").value.trim();

  if (!destino) {
    alert("Ingresa un destino");
    return;
  }

  const message = {
    type: "ride-request",
    origenLat,
    origenLng,
    destino
  };

  if (ws && ws.readyState === WebSocket.OPEN) {
    ws.send(JSON.stringify(message));
    document.getElementById("rideStatus").textContent = "Solicitud enviada";
  } else {
    document.getElementById("rideStatus").textContent = "WebSocket no conectado";
  }
}

window.initMap = initMap;
window.solicitarViaje = solicitarViaje;
