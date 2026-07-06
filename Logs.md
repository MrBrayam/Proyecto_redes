PS C:\xampp\htdocs\Proyecto_redes>  & 'C:\Program Files\Java\jdk-21\bin\java.exe' '@C:\Users\braya\AppData\Local\Temp\cp_f009n453mbcxcx7mamzkwvme6.argfile' 'api.proyecto.redes.RedesApplication' 

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/

 :: Spring Boot ::                (v4.0.7)

2026-06-30T15:43:47.627-05:00  INFO 23392 --- [redes] [  restartedMain] api.proyecto.redes.RedesApplication      : Starting RedesApplication using Java 21.0.2 with PID 23392 (C:\xampp\htdocs\Proyecto_redes\target\classes started by braya in C:\xampp\htdocs\Proyecto_redes)
2026-06-30T15:43:47.631-05:00  INFO 23392 --- [redes] [  restartedMain] api.proyecto.redes.RedesApplication      : No active profile set, falling back to 1 default profile: "default"
2026-06-30T15:43:47.687-05:00  INFO 23392 --- [redes] [  restartedMain] .e.DevToolsPropertyDefaultsPostProcessor : Devtools property defaults active! Set 'spring.devtools.add-properties' to 'false' to disable
2026-06-30T15:43:47.687-05:00  INFO 23392 --- [redes] [  restartedMain] .e.DevToolsPropertyDefaultsPostProcessor : For additional web related logging consider setting the 'logging.level.web' property to 'DEBUG'
2026-06-30T15:43:48.679-05:00  INFO 23392 --- [redes] [  restartedMain] .s.d.r.c.RepositoryConfigurationDelegate : Bootstrapping Spring Data JPA repositories in DEFAULT mode.
2026-06-30T15:43:48.769-05:00  INFO 23392 --- [redes] [  restartedMain] .s.d.r.c.RepositoryConfigurationDelegate : Finished Spring Data repository scanning in 79 ms. Found 11 JPA repository interfaces.
2026-06-30T15:43:49.715-05:00  INFO 23392 --- [redes] [  restartedMain] o.s.boot.tomcat.TomcatWebServer          : Tomcat initialized with port 8080 (http)
2026-06-30T15:43:49.736-05:00  INFO 23392 --- [redes] [  restartedMain] o.apache.catalina.core.StandardService   : Starting service [Tomcat]
2026-06-30T15:43:49.737-05:00  INFO 23392 --- [redes] [  restartedMain] o.apache.catalina.core.StandardEngine    : Starting Servlet engine: [Apache Tomcat/11.0.22]
2026-06-30T15:43:49.809-05:00  INFO 23392 --- [redes] [  restartedMain] b.w.c.s.WebApplicationContextInitializer : Root WebApplicationContext: initialization completed in 2120 ms
2026-06-30T15:43:50.175-05:00  INFO 23392 --- [redes] [  restartedMain] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Starting...
2026-06-30T15:43:50.376-05:00  INFO 23392 --- [redes] [  restartedMain] com.zaxxer.hikari.pool.HikariPool        : HikariPool-1 - Added connection com.mysql.cj.jdbc.ConnectionImpl@d13db7b
2026-06-30T15:43:50.377-05:00  INFO 23392 --- [redes] [  restartedMain] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Start completed.
2026-06-30T15:43:50.401-05:00  INFO 23392 --- [redes] [  restartedMain] org.hibernate.orm.jpa                    : HHH008540: Processing PersistenceUnitInfo [name: default]
2026-06-30T15:43:50.439-05:00  INFO 23392 --- [redes] [  restartedMain] org.hibernate.orm.core                   : HHH000001: Hibernate ORM core version 7.2.19.Final
2026-06-30T15:43:50.988-05:00  INFO 23392 --- [redes] [  restartedMain] o.s.o.j.p.SpringPersistenceUnitInfo      : No LoadTimeWeaver setup: ignoring JPA class transformer
2026-06-30T15:43:51.051-05:00  WARN 23392 --- [redes] [  restartedMain] org.hibernate.orm.deprecation            : HHH90000025: MySQLDialect does not need to be specified explicitly using 'hibernate.dialect' (remove the property setting and it will be selected by default)
2026-06-30T15:43:51.067-05:00  INFO 23392 --- [redes] [  restartedMain] org.hibernate.orm.connections.pooling    : HHH10001005: Database info:
        Database JDBC URL [jdbc:mysql://BrayamAristaF:3306/redes?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true]
        Database driver: MySQL Connector/J
        Database dialect: MySQLDialect
        Database version: 8.4.6
        Default catalog/schema: redes/undefined
        Autocommit mode: undefined/unknown
        Isolation level: REPEATABLE_READ [default REPEATABLE_READ]
        JDBC fetch size: none
        Pool: DataSourceConnectionProvider
        Minimum pool size: undefined/unknown
        Maximum pool size: undefined/unknown
2026-06-30T15:43:51.895-05:00  INFO 23392 --- [redes] [  restartedMain] org.hibernate.orm.core                   : HHH000489: No JTA platform available (set 'hibernate.transaction.jta.platform' to enable JTA platform integration)
2026-06-30T15:43:51.900-05:00  INFO 23392 --- [redes] [  restartedMain] j.LocalContainerEntityManagerFactoryBean : Initialized JPA EntityManagerFactory for persistence unit 'default'
2026-06-30T15:43:51.996-05:00  INFO 23392 --- [redes] [  restartedMain] o.s.d.j.r.query.QueryEnhancerFactories   : Hibernate is in classpath; If applicable, HQL parser will be used.
2026-06-30T15:43:53.482-05:00  WARN 23392 --- [redes] [  restartedMain] JpaBaseConfiguration$JpaWebConfiguration : spring.jpa.open-in-view is enabled by default. Therefore, database queries may be performed during view rendering. Explicitly configure spring.jpa.open-in-view to disable this warning
2026-06-30T15:43:53.496-05:00  INFO 23392 --- [redes] [  restartedMain] o.s.b.w.a.WelcomePageHandlerMapping      : Adding welcome page: class path resource [static/index.html]
2026-06-30T15:43:53.513-05:00  INFO 23392 --- [redes] [  restartedMain] o.s.v.b.OptionalValidatorFactoryBean     : Failed to set up a Bean Validation provider: jakarta.validation.NoProviderFoundException: Unable to create a Configuration, because no Jakarta Validation provider could be found. Add a provider like Hibernate Validator (RI) to your classpath.
2026-06-30T15:43:54.084-05:00  INFO 23392 --- [redes] [  restartedMain] o.s.boot.tomcat.TomcatWebServer          : Tomcat started on port 8080 (http) with context path '/'
2026-06-30T15:43:54.092-05:00  INFO 23392 --- [redes] [  restartedMain] api.proyecto.redes.RedesApplication      : Started RedesApplication in 6.911 seconds (process running for 7.36)

========================================
Iniciando Test de Conexión a MySQL
========================================

Conexión exitosa a la base de datos
  - Base de Datos: redes
  - URL: jdbc:mysql://BrayamAristaF:3306/redes?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
  - Usuario: root@BrayamAristaF
  - Driver: MySQL Connector/J
  - Versión del Driver: mysql-connector-j-9.7.0 (Revision: 0aade1f13bcc98faf7dda5c02e782481eb291f62)
  - Producto BD: MySQL
  - Versión BD: 8.4.6

Conexión cerrada correctamente

========================================

2026-06-30T15:44:03.968-05:00  INFO 23392 --- [redes] [nio-8080-exec-2] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring DispatcherServlet 'dispatcherServlet'
2026-06-30T15:44:03.969-05:00  INFO 23392 --- [redes] [nio-8080-exec-2] o.s.web.servlet.DispatcherServlet        : Initializing Servlet 'dispatcherServlet'
2026-06-30T15:44:03.971-05:00  INFO 23392 --- [redes] [nio-8080-exec-2] o.s.web.servlet.DispatcherServlet        : Completed initialization in 2 ms
2026-06-30T15:44:04.401-05:00  WARN 23392 --- [redes] [nio-8080-exec-5] .w.s.m.a.ResponseStatusExceptionResolver : Resolved [org.springframework.web.server.ResponseStatusException: 401 UNAUTHORIZED "Token invalido o expirado"]
2026-06-30T15:44:04.467-05:00 ERROR 23392 --- [redes] [nio-8080-exec-4] w.s.h.ExceptionWebSocketHandlerDecorator : Closing session due to exception for StandardWebSocketSession[id=2c735304-828d-4dbb-a930-77c5b9fb5fd1, uri=ws://localhost:8080/ws/conductor]

org.springframework.web.server.ResponseStatusException: 401 UNAUTHORIZED "Token invalido o expirado"
        at api.proyecto.redes.service.AuthService.validarToken(AuthService.java:109) ~[classes/:na]
        at api.proyecto.redes.service.AuthService.obtenerSesion(AuthService.java:69) ~[classes/:na]
        at api.proyecto.redes.websocket.ConductorWebSocketHandler.validarConductor(ConductorWebSocketHandler.java:149) ~[classes/:na]
        at api.proyecto.redes.websocket.ConductorWebSocketHandler.handleDriverConnect(ConductorWebSocketHandler.java:63) ~[classes/:na]
        at api.proyecto.redes.websocket.ConductorWebSocketHandler.handleTextMessage(ConductorWebSocketHandler.java:47) ~[classes/:na]
        at org.springframework.web.socket.handler.AbstractWebSocketHandler.handleMessage(AbstractWebSocketHandler.java:43) ~[spring-websocket-7.0.8.jar:7.0.8]
        at org.springframework.web.socket.handler.WebSocketHandlerDecorator.handleMessage(WebSocketHandlerDecorator.java:75) ~[spring-websocket-7.0.8.jar:7.0.8]
        at org.springframework.web.socket.handler.LoggingWebSocketHandlerDecorator.handleMessage(LoggingWebSocketHandlerDecorator.java:56) ~[spring-websocket-7.0.8.jar:7.0.8]
        at org.springframework.web.socket.handler.ExceptionWebSocketHandlerDecorator.handleMessage(ExceptionWebSocketHandlerDecorator.java:58) ~[spring-websocket-7.0.8.jar:7.0.8]
        at org.springframework.web.socket.adapter.standard.StandardWebSocketHandlerAdapter.handleTextMessage(StandardWebSocketHandlerAdapter.java:113) ~[spring-websocket-7.0.8.jar:7.0.8]
        at org.springframework.web.socket.adapter.standard.StandardWebSocketHandlerAdapter$3.onMessage(StandardWebSocketHandlerAdapter.java:84) ~[spring-websocket-7.0.8.jar:7.0.8]
        at org.springframework.web.socket.adapter.standard.StandardWebSocketHandlerAdapter$3.onMessage(StandardWebSocketHandlerAdapter.java:81) ~[spring-websocket-7.0.8.jar:7.0.8]
        at org.apache.tomcat.websocket.WsFrameBase.sendMessageText(WsFrameBase.java:392) ~[tomcat-embed-websocket-11.0.22.jar:11.0.22]
        at org.apache.tomcat.websocket.server.WsFrameServer.sendMessageText(WsFrameServer.java:130) ~[tomcat-embed-websocket-11.0.22.jar:11.0.22]
        at org.apache.tomcat.websocket.WsFrameBase.processDataText(WsFrameBase.java:486) ~[tomcat-embed-websocket-11.0.22.jar:11.0.22]
        at org.apache.tomcat.websocket.WsFrameBase.processData(WsFrameBase.java:286) ~[tomcat-embed-websocket-11.0.22.jar:11.0.22]
        at org.apache.tomcat.websocket.WsFrameBase.processInputBuffer(WsFrameBase.java:129) ~[tomcat-embed-websocket-11.0.22.jar:11.0.22]
        at org.apache.tomcat.websocket.server.WsFrameServer.onDataAvailable(WsFrameServer.java:85) ~[tomcat-embed-websocket-11.0.22.jar:11.0.22]
        at org.apache.tomcat.websocket.server.WsFrameServer.doOnDataAvailable(WsFrameServer.java:184) ~[tomcat-embed-websocket-11.0.22.jar:11.0.22]
        at org.apache.tomcat.websocket.server.WsFrameServer.notifyDataAvailable(WsFrameServer.java:164) ~[tomcat-embed-websocket-11.0.22.jar:11.0.22]
        at org.apache.tomcat.websocket.server.WsHttpUpgradeHandler.upgradeDispatch(WsHttpUpgradeHandler.java:152) ~[tomcat-embed-websocket-11.0.22.jar:11.0.22]
        at org.apache.coyote.http11.upgrade.UpgradeProcessorInternal.dispatch(UpgradeProcessorInternal.java:60) ~[tomcat-embed-core-11.0.22.jar:11.0.22]
        at org.apache.coyote.AbstractProcessorLight.process(AbstractProcessorLight.java:57) ~[tomcat-embed-core-11.0.22.jar:11.0.22]
        at org.apache.coyote.AbstractProtocol$ConnectionHandler.process(AbstractProtocol.java:1272) ~[tomcat-embed-core-11.0.22.jar:11.0.22]
        at org.apache.tomcat.util.net.NioEndpoint$SocketProcessor.doRun(NioEndpoint.java:1801) ~[tomcat-embed-core-11.0.22.jar:11.0.22]
        at org.apache.tomcat.util.net.SocketProcessorBase.run(SocketProcessorBase.java:52) ~[tomcat-embed-core-11.0.22.jar:11.0.22]
        at org.apache.tomcat.util.threads.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:946) ~[tomcat-embed-core-11.0.22.jar:11.0.22]
        at org.apache.tomcat.util.threads.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:480) ~[tomcat-embed-core-11.0.22.jar:11.0.22]
        at org.apache.tomcat.util.threads.TaskThread$WrappingRunnable.run(TaskThread.java:57) ~[tomcat-embed-core-11.0.22.jar:11.0.22]
        at java.base/java.lang.Thread.run(Thread.java:1583) ~[na:na]

Hibernate: select u1_0.id_usuario,u1_0.creado_en,u1_0.email,u1_0.nombre,u1_0.password,u1_0.rol from usuarios u1_0 where u1_0.email=?
Hibernate: select c1_0.id_conductor,c1_0.calificacion_promedio,c1_0.creado_en,c1_0.disponible,c1_0.licencia,c1_0.usuario_id,c1_0.vehiculo from conductores c1_0 where c1_0.usuario_id=?
Hibernate: select c1_0.id_conductor,c1_0.calificacion_promedio,c1_0.creado_en,c1_0.disponible,c1_0.licencia,c1_0.usuario_id,c1_0.vehiculo from conductores c1_0 where c1_0.usuario_id=?
Hibernate: select v1_0.id_viaje,v1_0.actualizado_en,v1_0.conductor_id,v1_0.creado_en,v1_0.destino_lat,v1_0.destino_lng,v1_0.distancia_km,v1_0.estado,v1_0.multiplicador_demanda,v1_0.origen_lat,v1_0.origen_lng,v1_0.pasajero_id,v1_0.precio,v1_0.precio_base from viajes v1_0 where v1_0.estado=?
2026-06-30T15:44:20.079-05:00  WARN 23392 --- [redes] [nio-8080-exec-2] .w.s.m.a.ResponseStatusExceptionResolver : Resolved [org.springframework.web.server.ResponseStatusException: 403 FORBIDDEN "Rol no autorizado"]
Hibernate: select u1_0.id_usuario,u1_0.creado_en,u1_0.email,u1_0.nombre,u1_0.password,u1_0.rol from usuarios u1_0 where u1_0.email=?
Hibernate: select c1_0.id_conductor,c1_0.calificacion_promedio,c1_0.creado_en,c1_0.disponible,c1_0.licencia,c1_0.usuario_id,c1_0.vehiculo from conductores c1_0 where c1_0.disponible=?
Hibernate: select u1_0.id_usuario,u1_0.creado_en,u1_0.email,u1_0.nombre,u1_0.password,u1_0.rol from usuarios u1_0 where u1_0.id_usuario=?
Hibernate: select c1_0.id_conductor,c1_0.calificacion_promedio,c1_0.creado_en,c1_0.disponible,c1_0.licencia,c1_0.usuario_id,c1_0.vehiculo from conductores c1_0 where c1_0.disponible=?
Hibernate: select u1_0.id_usuario,u1_0.creado_en,u1_0.email,u1_0.nombre,u1_0.password,u1_0.rol from usuarios u1_0 where u1_0.id_usuario=?
Hibernate: select u1_0.id_usuario,u1_0.creado_en,u1_0.email,u1_0.nombre,u1_0.password,u1_0.rol from usuarios u1_0 where u1_0.id_usuario=?
Hibernate: select c1_0.id_conductor,c1_0.calificacion_promedio,c1_0.creado_en,c1_0.disponible,c1_0.licencia,c1_0.usuario_id,c1_0.vehiculo from conductores c1_0 where c1_0.id_conductor=?
Hibernate: insert into viajes (conductor_id,destino_lat,destino_lng,distancia_km,estado,multiplicador_demanda,origen_lat,origen_lng,pasajero_id,precio,precio_base) values (?,?,?,?,?,?,?,?,?,?,?)
Hibernate: select u1_0.id_usuario,u1_0.creado_en,u1_0.email,u1_0.nombre,u1_0.password,u1_0.rol from usuarios u1_0 where u1_0.id_usuario=?
Hibernate: select u1_0.id_usuario,u1_0.creado_en,u1_0.email,u1_0.nombre,u1_0.password,u1_0.rol from usuarios u1_0 where u1_0.id_usuario=?
Hibernate: select c1_0.id_conductor,c1_0.calificacion_promedio,c1_0.creado_en,c1_0.disponible,c1_0.licencia,c1_0.usuario_id,c1_0.vehiculo from conductores c1_0 where c1_0.id_conductor=?
Hibernate: insert into viajes (conductor_id,destino_lat,destino_lng,distancia_km,estado,multiplicador_demanda,origen_lat,origen_lng,pasajero_id,precio,precio_base) values (?,?,?,?,?,?,?,?,?,?,?)
Hibernate: select u1_0.id_usuario,u1_0.creado_en,u1_0.email,u1_0.nombre,u1_0.password,u1_0.rol from usuarios u1_0 where u1_0.id_usuario=?
Hibernate: select c1_0.id_conductor,c1_0.calificacion_promedio,c1_0.creado_en,c1_0.disponible,c1_0.licencia,c1_0.usuario_id,c1_0.vehiculo from conductores c1_0 where c1_0.id_conductor=?
Hibernate: insert into viajes (conductor_id,destino_lat,destino_lng,distancia_km,estado,multiplicador_demanda,origen_lat,origen_lng,pasajero_id,precio,precio_base) values (?,?,?,?,?,?,?,?,?,?,?)
Hibernate: select u1_0.id_usuario,u1_0.creado_en,u1_0.email,u1_0.nombre,u1_0.password,u1_0.rol from usuarios u1_0 where u1_0.id_usuario=?
Hibernate: select u1_0.id_usuario,u1_0.creado_en,u1_0.email,u1_0.nombre,u1_0.password,u1_0.rol from usuarios u1_0 where u1_0.id_usuario=?
Hibernate: select c1_0.id_conductor,c1_0.calificacion_promedio,c1_0.creado_en,c1_0.disponible,c1_0.licencia,c1_0.usuario_id,c1_0.vehiculo from conductores c1_0 where c1_0.id_conductor=?
Hibernate: insert into viajes (conductor_id,destino_lat,destino_lng,distancia_km,estado,multiplicador_demanda,origen_lat,origen_lng,pasajero_id,precio,precio_base) values (?,?,?,?,?,?,?,?,?,?,?)
Hibernate: select c1_0.id_conductor,c1_0.calificacion_promedio,c1_0.creado_en,c1_0.disponible,c1_0.licencia,c1_0.usuario_id,c1_0.vehiculo from conductores c1_0 where c1_0.disponible=?
Hibernate: select u1_0.id_usuario,u1_0.creado_en,u1_0.email,u1_0.nombre,u1_0.password,u1_0.rol from usuarios u1_0 where u1_0.id_usuario=?
Hibernate: select u1_0.id_usuario,u1_0.creado_en,u1_0.email,u1_0.nombre,u1_0.password,u1_0.rol from usuarios u1_0 where u1_0.email=?
Hibernate: select c1_0.id_conductor,c1_0.calificacion_promedio,c1_0.creado_en,c1_0.disponible,c1_0.licencia,c1_0.usuario_id,c1_0.vehiculo from conductores c1_0 where c1_0.usuario_id=?
Hibernate: select v1_0.id_viaje,v1_0.actualizado_en,v1_0.conductor_id,v1_0.creado_en,v1_0.destino_lat,v1_0.destino_lng,v1_0.distancia_km,v1_0.estado,v1_0.multiplicador_demanda,v1_0.origen_lat,v1_0.origen_lng,v1_0.pasajero_id,v1_0.precio,v1_0.precio_base from viajes v1_0 where v1_0.estado=?
Hibernate: select c1_0.id_conductor,c1_0.calificacion_promedio,c1_0.creado_en,c1_0.disponible,c1_0.licencia,c1_0.usuario_id,c1_0.vehiculo from conductores c1_0 where c1_0.usuario_id=?
Hibernate: select c1_0.id_conductor,c1_0.calificacion_promedio,c1_0.creado_en,c1_0.disponible,c1_0.licencia,c1_0.usuario_id,c1_0.vehiculo from conductores c1_0 where c1_0.usuario_id=?
2026-06-30T15:45:59.523-05:00  WARN 23392 --- [redes] [nio-8080-exec-6] .w.s.m.a.ResponseStatusExceptionResolver : Resolved [org.springframework.web.server.ResponseStatusException: 403 FORBIDDEN "Rol no autorizado"]
Hibernate: select u1_0.id_usuario,u1_0.creado_en,u1_0.email,u1_0.nombre,u1_0.password,u1_0.rol from usuarios u1_0 where u1_0.email=?
Hibernate: select c1_0.id_conductor,c1_0.calificacion_promedio,c1_0.creado_en,c1_0.disponible,c1_0.licencia,c1_0.usuario_id,c1_0.vehiculo from conductores c1_0 where c1_0.disponible=?
Hibernate: select u1_0.id_usuario,u1_0.creado_en,u1_0.email,u1_0.nombre,u1_0.password,u1_0.rol from usuarios u1_0 where u1_0.id_usuario=?
Hibernate: select u1_0.id_usuario,u1_0.creado_en,u1_0.email,u1_0.nombre,u1_0.password,u1_0.rol from usuarios u1_0 where u1_0.id_usuario=?
Hibernate: select c1_0.id_conductor,c1_0.calificacion_promedio,c1_0.creado_en,c1_0.disponible,c1_0.licencia,c1_0.usuario_id,c1_0.vehiculo from conductores c1_0 where c1_0.id_conductor=?
Hibernate: insert into viajes (conductor_id,destino_lat,destino_lng,distancia_km,estado,multiplicador_demanda,origen_lat,origen_lng,pasajero_id,precio,precio_base) values (?,?,?,?,?,?,?,?,?,?,?)
Hibernate: select u1_0.id_usuario,u1_0.creado_en,u1_0.email,u1_0.nombre,u1_0.password,u1_0.rol from usuarios u1_0 where u1_0.id_usuario=?
Hibernate: select u1_0.id_usuario,u1_0.creado_en,u1_0.email,u1_0.nombre,u1_0.password,u1_0.rol from usuarios u1_0 where u1_0.id_usuario=?
Hibernate: select c1_0.id_conductor,c1_0.calificacion_promedio,c1_0.creado_en,c1_0.disponible,c1_0.licencia,c1_0.usuario_id,c1_0.vehiculo from conductores c1_0 where c1_0.id_conductor=?
Hibernate: insert into viajes (conductor_id,destino_lat,destino_lng,distancia_km,estado,multiplicador_demanda,origen_lat,origen_lng,pasajero_id,precio,precio_base) values (?,?,?,?,?,?,?,?,?,?,?)