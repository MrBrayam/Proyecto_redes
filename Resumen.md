Universidad Nacional de San Martín – UNSM
"Año de la Esperanza y el Fortalecimiento de la Democracia"

UNIVERSIDAD NACIONAL DE SAN MARTÍN
FACULTAD DE INGENIERÍA DE SISTEMAS E INFORMÁTICA

"Plataforma de Transporte Inteligente AristaRideAI"

Estudiante: Arista Fernández, Brayam
Docente: Ing. Mg José Enrique Celis Escudero
TARAPOTO - PERÚ
2026

Contenido

I Resumen
II Descripción del Proyecto
II.I Contexto y Justificación
II.II Alcance del Proyecto
III Objetivos del Proyecto
III.I Objetivo General
III.II Objetivos Específicos
IV Arquitectura del Sistema
IV.I Descripción General
IV.II Estructura MVC del Backend (Spring Boot)
IV.III Stack Tecnológico
IV.IV Componentes Clave
V Funcionalidades Principales
V.I Módulo de Pasajeros
V.II Módulo de Conductores
V.III Módulo de Administración de Red
VI Cronograma de Desarrollo
VII Riesgos y Mitigación
VIII Resultados Esperados
IX Conclusiones

Resumen:

AristaRideAI es una plataforma de transporte urbano inteligente que combina tres pilares tecnológicos: un servidor de Inteligencia Artificial para la toma de decisiones en tiempo real, una arquitectura de servidor escalable tipo Uber implementada con Java y Spring Boot siguiendo el patrón MVC, y un sistema de administración y monitoreo de redes que garantiza la disponibilidad y rendimiento del servicio.

El sistema permitirá conectar pasajeros con conductores de forma eficiente, utilizando algoritmos de matching inteligente, predicción de demanda por zonas geográficas y detección de anomalías en el comportamiento del sistema, todo supervisado mediante un panel de administración de red.

Descripción del Proyecto:

Contexto y Justificación:

El crecimiento del transporte bajo demanda ha transformado la movilidad urbana a nivel mundial. Sin embargo, en ciudades intermedias como Tarapoto, aún no existen plataformas locales que integren inteligencia artificial con administración eficiente de redes para optimizar este servicio.

AristaRideAI nace como respuesta a esta necesidad, proponiendo una solución tecnológica completa que integra los tres temas principales indicados por el docente:

Servidor de Inteligencia Artificial: toma de decisiones autónoma y optimización de rutas

Servidor para aplicación tipo Uber: gestión de usuarios, viajes y pagos en tiempo real

Administración de redes: monitoreo, escalabilidad y alta disponibilidad del sistema

Alcance del Proyecto:

El proyecto comprende el diseño, desarrollo e implementación de:

API REST y servidor WebSocket para comunicación en tiempo real entre conductores y pasajeros

Módulo de IA para matching inteligente conductor-pasajero y predicción de demanda

Aplicación móvil multiplataforma para pasajeros y conductores

Panel de administración de red con métricas en tiempo real (latencia, disponibilidad, tráfico)

Sistema de autenticación, gestión de usuarios y registro de viajes

Objetivos del Proyecto:

Objetivo General:
Desarrollar una plataforma de transporte inteligente que integre un servidor de IA, arquitectura de servicios tipo Uber y administración de redes, para optimizar la conexión entre pasajeros y conductores en entornos urbanos.

Objetivos Específicos:

Diseñar e implementar el servidor backend con Java y Spring Boot siguiendo el patrón MVC, capaz de gestionar solicitudes de viaje en tiempo real mediante Spring WebSocket y STOMP.

Desarrollar un módulo de Inteligencia Artificial con algoritmos de matching y predicción de demanda utilizando Python y scikit-learn, integrado como microservicio consumido por Spring Boot.

Implementar un sistema de administración de redes con Spring Actuator, Prometheus y Grafana para monitorear la infraestructura del servidor en tiempo real.

Construir una aplicación móvil con React Native que permita a pasajeros solicitar viajes y a conductores aceptarlos con actualización de ubicación en tiempo real.

Evaluar el rendimiento del sistema bajo carga simulada y demostrar la escalabilidad de la arquitectura propuesta.

Arquitectura del Sistema

Descripción General
La arquitectura principal del backend sigue el patrón MVC (Model-View-Controller) implementado con Spring Boot. Las peticiones HTTP ingresan a los @RestController (Capa de Control), son procesadas por los @Service (Capa de Negocio / Lógica), y persisten datos a través de los @Repository con Spring Data JPA (Capa de Modelo). Para la comunicación en tiempo real se usa Spring WebSocket con STOMP. El módulo de IA corre como microservicio independiente en Python, consumido por Spring Boot mediante WebClient.

Estructura MVC del Backend (Spring Boot)
El proyecto Spring Boot se organiza en los siguientes paquetes siguiendo el patrón MVC:

controller/ --- @RestController: ViajeController, UsuarioController, ConductorController. Reciben y responden peticiones HTTP/WebSocket.

service/ --- @Service: ViajeService, MatchingService (consume IA), UsuarioService. Contienen toda la lógica de negocio.

repository/ --- @Repository: interfaces de Spring Data JPA para acceso a PostgreSQL sin SQL manual.

model/ --- @Entity: clases Java que mapean las tablas: Viaje, Usuario, Conductor, Calificacion.

websocket/ --- UbicacionWebSocketHandler: gestiona la ubicación en tiempo real con STOMP.

config/ --- SecurityConfig (JWT), WebSocketConfig, ActuatorConfig para métricas de red.

Stack Tecnológico
Capa: Backend (MVC) - Tecnología: Java + Spring Boot + Spring MVC - Función: Controladores REST, lógica de negocio y patrón MVC
Capa: IA / ML - Tecnología: Python + scikit-learn (microservicio) - Función: Matching inteligente y predicción de demanda
Capa: Frontend Móvil - Tecnología: React Native - Función: App de pasajeros y conductores
Capa: Comunicación - Tecnología: Spring WebSocket + STOMP + REST API - Función: Comunicación en tiempo real y endpoints HTTP
Capa: Seguridad - Tecnología: Spring Security + JWT - Función: Autenticación y autorización robusta
Capa: Monitoreo de Red - Tecnología: Spring Actuator + Prometheus + Grafana - Función: Panel de administración de red y métricas

Componentes Clave

Motor de Matching IA (MatchingService.java): el @Service llama al microservicio Python vía WebClient y devuelve el conductor óptimo según distancia, calificación y demanda de zona.

Módulo de Predicción de Demanda (Python): microservicio Flask/FastAPI independiente con modelo scikit-learn, consumido por Spring Boot mediante REST.

Panel de Red (Spring Actuator + Grafana): Spring Boot expone métricas de JVM, latencia de endpoints y conexiones activas a Prometheus, visualizadas en Grafana.

Spring Security + JWT: cada petición pasa por un filtro de autenticación que valida el token antes de llegar al @RestController.

Funcionalidades Principales

Módulo de Pasajeros

Registro e inicio de sesión con autenticación segura (JWT)

Solicitud de viaje con geolocalización en tiempo real

Visualización del conductor asignado y tiempo estimado de llegada

Sistema de calificación y comentarios post-viaje

Historial de viajes y métodos de pago

Módulo de Conductores

Panel de disponibilidad y aceptación de viajes

Navegación con ruta optimizada por IA

Estado de ganancias y estadísticas de rendimiento

Notificaciones push para nuevas solicitudes cercanas

Módulo de Administración de Red

Dashboard de Grafana con métricas de latencia, uptime y tráfico

Alertas automáticas ante caídas o degradación del servicio

Logs centralizados y análisis de errores

Control de escalado automático según demanda

Cronograma de Desarrollo

Fase 1: Análisis de requerimientos, diseño de arquitectura del sistema y base de datos - Duración: 1 semana
Fase 2: Configuración del proyecto Spring Boot, estructura MVC, Spring Security + JWT, WebSocket con STOMP - Duración: 2 semanas
Fase 3: Desarrollo del módulo de IA: modelo de matching y predicción de demanda - Duración: 2 semanas
Fase 4: Desarrollo de la aplicación móvil (pasajero y conductor) - Duración: 2 semanas
Fase 5: Integración backend-IA, configuración Spring Actuator + Prometheus + Grafana, pruebas de carga - Duración: 2 semanas
Fase 6: Despliegue, documentación final y presentación - Duración: 1 semana

Duración total estimada del proyecto: 10 semanas.

Riesgos y Mitigación

Riesgo: Baja precisión del modelo IA en las primeras iteraciones
Mitigación: Usar datasets públicos de transporte y reentrenar con datos reales progresivamente

Riesgo: Problemas de latencia en tiempo real
Mitigación: Implementar Redis para caché y WebSockets con reconexión automática

Riesgo: Escalabilidad ante alta demanda simultánea
Mitigación: Diseño stateless del backend para escalar horizontalmente con Docker

Riesgo: Disponibilidad del servidor
Mitigación: Monitoreo continuo con alertas en Grafana y respaldos automatizados

Resultados Esperados

Al finalizar el proyecto se espera obtener:

Una plataforma funcional de transporte inteligente con todas las capas integradas y probadas.

Un servidor de IA operativo que reduzca el tiempo de matching conductor-pasajero en al menos un 30% respecto a asignación aleatoria.

Un sistema de monitoreo de red que garantice visibilidad completa del estado de la infraestructura en tiempo real.

Documentación técnica completa: arquitectura, APIs, manual de despliegue y manual de usuario.

Una presentación demostrable con datos de prueba que evidencie el funcionamiento integrado del sistema.

Conclusiones

AristaRideAI representa un proyecto ambicioso y relevante que integra de manera coherente los tres ejes tecnológicos propuestos por la cátedra. El uso de Java con Spring Boot y el patrón MVC aporta una estructura profesional, mantenible y escalable al backend, complementada con Inteligencia Artificial en Python y un robusto sistema de administración de redes.

La correcta ejecución de este proyecto permitirá al equipo adquirir competencias avanzadas en desarrollo de sistemas distribuidos con Java, machine learning aplicado y operaciones de red, formando perfiles altamente demandados en el mercado laboral del sector tecnológico.