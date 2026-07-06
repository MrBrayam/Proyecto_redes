PS C:\xampp\htdocs\Proyecto_redes>  & 'C:\Program Files\Java\jdk-21\bin\java.exe' '@C:\Users\braya\AppData\Local\Temp\cp_w5lvrd1niazt7y4ry55d2c84.argfile' 'api.proyecto.redes.RedesApplication' 

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/

 :: Spring Boot ::                (v4.0.7)

2026-07-06T01:02:34.404-05:00  INFO 22828 --- [redes] [  restartedMain] api.proyecto.redes.RedesApplication      : Starting RedesApplication using Java 21.0.2 with PID 22828 (C:\xampp\htdocs\Proyecto_redes\target\classes started by braya in C:\xampp\htdocs\Proyecto_redes)
2026-07-06T01:02:34.413-05:00  INFO 22828 --- [redes] [  restartedMain] api.proyecto.redes.RedesApplication      : No active profile set, falling back to 1 default profile: "default"
2026-07-06T01:02:34.475-05:00  INFO 22828 --- [redes] [  restartedMain] .e.DevToolsPropertyDefaultsPostProcessor : Devtools property defaults active! Set 'spring.devtools.add-properties' to 'false' to disable
2026-07-06T01:02:34.475-05:00  INFO 22828 --- [redes] [  restartedMain] .e.DevToolsPropertyDefaultsPostProcessor : For additional web related logging consider setting the 'logging.level.web' property to 'DEBUG'
2026-07-06T01:02:35.597-05:00  INFO 22828 --- [redes] [  restartedMain] .s.d.r.c.RepositoryConfigurationDelegate : Bootstrapping Spring Data JPA repositories in DEFAULT mode.
2026-07-06T01:02:35.706-05:00  INFO 22828 --- [redes] [  restartedMain] .s.d.r.c.RepositoryConfigurationDelegate : Finished Spring Data repository scanning in 96 ms. Found 11 JPA repository interfaces.   
2026-07-06T01:02:36.821-05:00  INFO 22828 --- [redes] [  restartedMain] o.s.boot.tomcat.TomcatWebServer          : Tomcat initialized with port 8080 (http)
2026-07-06T01:02:36.842-05:00  INFO 22828 --- [redes] [  restartedMain] o.apache.catalina.core.StandardService   : Starting service [Tomcat]
2026-07-06T01:02:36.842-05:00  INFO 22828 --- [redes] [  restartedMain] o.apache.catalina.core.StandardEngine    : Starting Servlet engine: [Apache Tomcat/11.0.22]
2026-07-06T01:02:36.912-05:00  INFO 22828 --- [redes] [  restartedMain] b.w.c.s.WebApplicationContextInitializer : Root WebApplicationContext: initialization completed in 2435 ms
2026-07-06T01:02:37.244-05:00  INFO 22828 --- [redes] [  restartedMain] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Starting...
2026-07-06T01:02:37.549-05:00  INFO 22828 --- [redes] [  restartedMain] com.zaxxer.hikari.pool.HikariPool        : HikariPool-1 - Added connection com.mysql.cj.jdbc.ConnectionImpl@3f9c7628
2026-07-06T01:02:37.552-05:00  INFO 22828 --- [redes] [  restartedMain] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Start completed.
2026-07-06T01:02:37.595-05:00  INFO 22828 --- [redes] [  restartedMain] org.hibernate.orm.jpa         
           : HHH008540: Processing PersistenceUnitInfo [name: default]
2026-07-06T01:02:37.674-05:00  INFO 22828 --- [redes] [  restartedMain] org.hibernate.orm.core                   : HHH000001: Hibernate ORM core version 7.2.19.Final
2026-07-06T01:02:38.346-05:00  INFO 22828 --- [redes] [  restartedMain] o.s.o.j.p.SpringPersistenceUnitInfo      : No LoadTimeWeaver setup: ignoring JPA class transformer
2026-07-06T01:02:38.442-05:00  WARN 22828 --- [redes] [  restartedMain] org.hibernate.orm.deprecation            : HHH90000025: MySQLDialect does not need to be specified explicitly using 'hibernate.dialect' (remove the property setting and it will be selected by default)
2026-07-06T01:02:38.458-05:00  INFO 22828 --- [redes] [  restartedMain] org.hibernate.orm.connections.pooling    : HHH10001005: Database info:
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
2026-07-06T01:02:39.480-05:00  INFO 22828 --- [redes] [  restartedMain] org.hibernate.orm.core                   : HHH000489: No JTA platform available (set 'hibernate.transaction.jta.platform' to enable JTA platform integration)
2026-07-06T01:02:39.487-05:00  INFO 22828 --- [redes] [  restartedMain] j.LocalContainerEntityManagerFactoryBean : Initialized JPA EntityManagerFactory for persistence unit 'default'
2026-07-06T01:02:39.608-05:00  INFO 22828 --- [redes] [  restartedMain] o.s.d.j.r.query.QueryEnhancerFactories   : Hibernate is in classpath; If applicable, HQL parser will be used.
2026-07-06T01:02:41.357-05:00  WARN 22828 --- [redes] [  restartedMain] JpaBaseConfiguration$JpaWebConfiguration : spring.jpa.open-in-view is enabled by default. Therefore, database queries may be performed during view rendering. Explicitly configure spring.jpa.open-in-view to disable this warning        
2026-07-06T01:02:41.374-05:00  INFO 22828 --- [redes] [  restartedMain] o.s.b.w.a.WelcomePageHandlerMapping      : Adding welcome page: class path resource [static/index.html]
2026-07-06T01:02:41.391-05:00  INFO 22828 --- [redes] [  restartedMain] o.s.v.b.OptionalValidatorFactoryBean     : Failed to set up a Bean Validation provider: jakarta.validation.NoProviderFoundException: Unable to create a Configuration, because no Jakarta Validation provider could be found. Add a provider like Hibernate Validator (RI) to your classpath.
2026-07-06T01:02:42.141-05:00  INFO 22828 --- [redes] [  restartedMain] o.s.boot.tomcat.TomcatWebServer          : Tomcat started on port 8080 (http) with context path '/'
2026-07-06T01:02:42.150-05:00  INFO 22828 --- [redes] [  restartedMain] api.proyecto.redes.RedesApplication      : Started RedesApplication in 8.312 seconds (process running for 9.766)

========================================
Iniciando Test de Conexión a MySQL
========================================

Conexión exitosa a la base de datos
  - URL: jdbc:mysql://BrayamAristaF:3306/redes?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
  - Usuario: root@BrayamAristaF
  - Driver: MySQL Connector/J
  - Versión del Driver: mysql-connector-j-9.7.0 (Revision: 0aade1f13bcc98faf7dda5c02e782481eb291f62)
  - Producto BD: MySQL
  - Versión BD: 8.4.6

Conexión cerrada correctamente

========================================

2026-07-06T01:03:21.742-05:00  INFO 22828 --- [redes] [nio-8080-exec-3] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring DispatcherServlet 'dispatcherServlet'
2026-07-06T01:03:21.742-05:00  INFO 22828 --- [redes] [nio-8080-exec-3] o.s.web.servlet.DispatcherServlet        : Initializing Servlet 'dispatcherServlet'
2026-07-06T01:03:21.745-05:00  INFO 22828 --- [redes] [nio-8080-exec-3] o.s.web.servlet.DispatcherServlet        : Completed initialization in 1 ms
Hibernate: select u1_0.id_usuario,u1_0.creado_en,u1_0.email,u1_0.nombre,u1_0.password,u1_0.rol from usuarios u1_0 where u1_0.email=?
Hibernate: select c1_0.id_conductor,c1_0.calificacion_promedio,c1_0.creado_en,c1_0.disponible,c1_0.licencia,c1_0.usuario_id,c1_0.vehiculo from conductores c1_0 where c1_0.disponible=?
Hibernate: select u1_0.id_usuario,u1_0.creado_en,u1_0.email,u1_0.nombre,u1_0.password,u1_0.rol from usuarios u1_0 where u1_0.id_usuario=?
Hibernate: select u1_0.id_usuario,u1_0.creado_en,u1_0.email,u1_0.nombre,u1_0.password,u1_0.rol from usuarios u1_0 where u1_0.email=?
Hibernate: select c1_0.id_conductor,c1_0.calificacion_promedio,c1_0.creado_en,c1_0.disponible,c1_0.licencia,c1_0.usuario_id,c1_0.vehiculo from conductores c1_0 where c1_0.usuario_id=?
Hibernate: select c1_0.id_conductor,c1_0.calificacion_promedio,c1_0.creado_en,c1_0.disponible,c1_0.licencia,c1_0.usuario_id,c1_0.vehiculo from conductores c1_0 where c1_0.usuario_id=?
Hibernate: select v1_0.id_viaje,v1_0.actualizado_en,v1_0.conductor_id,v1_0.creado_en,v1_0.destino_lat,v1_0.destino_lng,v1_0.distancia_km,v1_0.estado,v1_0.multiplicador_demanda,v1_0.origen_lat,v1_0.origen_lng,v1_0.pasajero_id,v1_0.precio,v1_0.precio_base from viajes v1_0 where v1_0.estado=?
Hibernate: select c1_0.id_conductor,c1_0.calificacion_promedio,c1_0.creado_en,c1_0.disponible,c1_0.licencia,c1_0.usuario_id,c1_0.vehiculo from conductores c1_0 where c1_0.usuario_id=?
Hibernate: select u1_0.id_usuario,u1_0.creado_en,u1_0.email,u1_0.nombre,u1_0.password,u1_0.rol from usuarios u1_0 where u1_0.id_usuario=?
Hibernate: select c1_0.id_conductor,c1_0.calificacion_promedio,c1_0.creado_en,c1_0.disponible,c1_0.licencia,c1_0.usuario_id,c1_0.vehiculo from conductores c1_0 where c1_0.id_conductor=?
Hibernate: insert into viajes (conductor_id,destino_lat,destino_lng,distancia_km,estado,multiplicador_demanda,origen_lat,origen_lng,pasajero_id,precio,precio_base) values (?,?,?,?,?,?,?,?,?,?,?)
Hibernate: select u1_0.id_usuario,u1_0.creado_en,u1_0.email,u1_0.nombre,u1_0.password,u1_0.rol from usuarios u1_0 where u1_0.id_usuario=?
Hibernate: select c1_0.id_conductor,c1_0.calificacion_promedio,c1_0.creado_en,c1_0.disponible,c1_0.licencia,c1_0.usuario_id,c1_0.vehiculo from conductores c1_0 where c1_0.usuario_id=?
Hibernate: select c1_0.id_conductor,c1_0.calificacion_promedio,c1_0.creado_en,c1_0.disponible,c1_0.licencia,c1_0.usuario_id,c1_0.vehiculo from conductores c1_0 where c1_0.usuario_id=?
Hibernate: select c1_0.id_conductor,c1_0.calificacion_promedio,c1_0.creado_en,c1_0.disponible,c1_0.licencia,c1_0.usuario_id,c1_0.vehiculo from conductores c1_0 where c1_0.usuario_id=?
Hibernate: select c1_0.id_conductor,c1_0.calificacion_promedio,c1_0.creado_en,c1_0.disponible,c1_0.licencia,c1_0.usuario_id,c1_0.vehiculo from conductores c1_0 where c1_0.usuario_id=?
Hibernate: select c1_0.id_conductor,c1_0.calificacion_promedio,c1_0.creado_en,c1_0.disponible,c1_0.licencia,c1_0.usuario_id,c1_0.vehiculo from conductores c1_0 where c1_0.usuario_id=?
Hibernate: select v1_0.id_viaje,v1_0.actualizado_en,v1_0.conductor_id,v1_0.creado_en,v1_0.destino_lat,v1_0.destino_lng,v1_0.distancia_km,v1_0.estado,v1_0.multiplicador_demanda,v1_0.origen_lat,v1_0.origen_lng,v1_0.pasajero_id,v1_0.precio,v1_0.precio_base from viajes v1_0 where v1_0.id_viaje=?
Hibernate: update viajes set conductor_id=?,destino_lat=?,destino_lng=?,distancia_km=?,estado=?,multiplicador_demanda=?,origen_lat=?,origen_lng=?,pasajero_id=?,precio=?,precio_base=? where id_viaje=?
Hibernate: select u1_0.id_usuario,u1_0.creado_en,u1_0.email,u1_0.nombre,u1_0.password,u1_0.rol from usuarios u1_0 where u1_0.id_usuario=?
Hibernate: select u1_0.id_usuario,u1_0.creado_en,u1_0.email,u1_0.nombre,u1_0.password,u1_0.rol from usuarios u1_0 where u1_0.id_usuario=?       
Hibernate: insert into notificaciones (creado_en,id_viaje,leida,leido_en,mensaje,tipo,titulo,usuario_id) values (?,?,?,?,?,?,?,?)
Hibernate: select c1_0.id_conductor,c1_0.calificacion_promedio,c1_0.creado_en,c1_0.disponible,c1_0.licencia,c1_0.usuario_id,c1_0.vehiculo from conductores c1_0 where c1_0.usuario_id=?
Hibernate: select c1_0.id_conductor,c1_0.calificacion_promedio,c1_0.creado_en,c1_0.disponible,c1_0.licencia,c1_0.usuario_id,c1_0.vehiculo from conductores c1_0 where c1_0.usuario_id=?
Hibernate: select c1_0.id_conductor,c1_0.calificacion_promedio,c1_0.creado_en,c1_0.disponible,c1_0.licencia,c1_0.usuario_id,c1_0.vehiculo from conductores c1_0 where c1_0.usuario_id=?
Hibernate: select c1_0.id_conductor,c1_0.calificacion_promedio,c1_0.creado_en,c1_0.disponible,c1_0.licencia,c1_0.usuario_id,c1_0.vehiculo from conductores c1_0 where c1_0.usuario_id=?
Hibernate: select c1_0.id_conductor,c1_0.calificacion_promedio,c1_0.creado_en,c1_0.disponible,c1_0.licencia,c1_0.usuario_id,c1_0.vehiculo from conductores c1_0 where c1_0.usuario_id=?
Hibernate: select c1_0.id_conductor,c1_0.calificacion_promedio,c1_0.creado_en,c1_0.disponible,c1_0.licencia,c1_0.usuario_id,c1_0.vehiculo from conductores c1_0 where c1_0.usuario_id=?
Hibernate: select c1_0.id_conductor,c1_0.calificacion_promedio,c1_0.creado_en,c1_0.disponible,c1_0.licencia,c1_0.usuario_id,c1_0.vehiculo from conductores c1_0 where c1_0.usuario_id=?
Hibernate: select c1_0.id_conductor,c1_0.calificacion_promedio,c1_0.creado_en,c1_0.disponible,c1_0.licencia,c1_0.usuario_id,c1_0.vehiculo from conductores c1_0 where c1_0.usuario_id=?
Hibernate: select c1_0.id_conductor,c1_0.calificacion_promedio,c1_0.creado_en,c1_0.disponible,c1_0.licencia,c1_0.usuario_id,c1_0.vehiculo from conductores c1_0 where c1_0.usuario_id=?
Hibernate: select c1_0.id_conductor,c1_0.calificacion_promedio,c1_0.creado_en,c1_0.disponible,c1_0.licencia,c1_0.usuario_id,c1_0.vehiculo from conductores c1_0 where c1_0.usuario_id=?
Hibernate: select c1_0.id_conductor,c1_0.calificacion_promedio,c1_0.creado_en,c1_0.disponible,c1_0.licencia,c1_0.usuario_id,c1_0.vehiculo from conductores c1_0 where c1_0.usuario_id=?
Hibernate: select c1_0.id_conductor,c1_0.calificacion_promedio,c1_0.creado_en,c1_0.disponible,c1_0.licencia,c1_0.usuario_id,c1_0.vehiculo from conductores c1_0 where c1_0.usuario_id=?
Hibernate: select c1_0.id_conductor,c1_0.calificacion_promedio,c1_0.creado_en,c1_0.disponible,c1_0.licencia,c1_0.usuario_id,c1_0.vehiculo from conductores c1_0 where c1_0.usuario_id=?
Hibernate: select c1_0.id_conductor,c1_0.calificacion_promedio,c1_0.creado_en,c1_0.disponible,c1_0.licencia,c1_0.usuario_id,c1_0.vehiculo from conductores c1_0 where c1_0.usuario_id=?
Hibernate: select c1_0.id_conductor,c1_0.calificacion_promedio,c1_0.creado_en,c1_0.disponible,c1_0.licencia,c1_0.usuario_id,c1_0.vehiculo from conductores c1_0 where c1_0.usuario_id=?
Hibernate: select c1_0.id_conductor,c1_0.calificacion_promedio,c1_0.creado_en,c1_0.disponible,c1_0.licencia,c1_0.usuario_id,c1_0.vehiculo from conductores c1_0 where c1_0.usuario_id=?
Hibernate: select c1_0.id_conductor,c1_0.calificacion_promedio,c1_0.creado_en,c1_0.disponible,c1_0.licencia,c1_0.usuario_id,c1_0.vehiculo from conductores c1_0 where c1_0.usuario_id=?
Hibernate: select c1_0.id_conductor,c1_0.calificacion_promedio,c1_0.creado_en,c1_0.disponible,c1_0.licencia,c1_0.usuario_id,c1_0.vehiculo from conductores c1_0 where c1_0.usuario_id=?
Hibernate: select c1_0.id_conductor,c1_0.calificacion_promedio,c1_0.creado_en,c1_0.disponible,c1_0.licencia,c1_0.usuario_id,c1_0.vehiculo from conductores c1_0 where c1_0.usuario_id=?
Hibernate: select c1_0.id_conductor,c1_0.calificacion_promedio,c1_0.creado_en,c1_0.disponible,c1_0.licencia,c1_0.usuario_id,c1_0.vehiculo from conductores c1_0 where c1_0.usuario_id=?
Hibernate: select c1_0.id_conductor,c1_0.calificacion_promedio,c1_0.creado_en,c1_0.disponible,c1_0.licencia,c1_0.usuario_id,c1_0.vehiculo from conductores c1_0 where c1_0.usuario_id=?
Hibernate: select c1_0.id_conductor,c1_0.calificacion_promedio,c1_0.creado_en,c1_0.disponible,c1_0.licencia,c1_0.usuario_id,c1_0.vehiculo from conductores c1_0 where c1_0.usuario_id=?
Hibernate: select c1_0.id_conductor,c1_0.calificacion_promedio,c1_0.creado_en,c1_0.disponible,c1_0.licencia,c1_0.usuario_id,c1_0.vehiculo from conductores c1_0 where c1_0.usuario_id=?
Hibernate: select c1_0.id_conductor,c1_0.calificacion_promedio,c1_0.creado_en,c1_0.disponible,c1_0.licencia,c1_0.usuario_id,c1_0.vehiculo from conductores c1_0 where c1_0.usuario_id=?
Hibernate: select c1_0.id_conductor,c1_0.calificacion_promedio,c1_0.creado_en,c1_0.disponible,c1_0.licencia,c1_0.usuario_id,c1_0.vehiculo from conductores c1_0 where c1_0.usuario_id=?
Hibernate: select c1_0.id_conductor,c1_0.calificacion_promedio,c1_0.creado_en,c1_0.disponible,c1_0.licencia,c1_0.usuario_id,c1_0.vehiculo from conductores c1_0 where c1_0.usuario_id=?
Hibernate: select c1_0.id_conductor,c1_0.calificacion_promedio,c1_0.creado_en,c1_0.disponible,c1_0.licencia,c1_0.usuario_id,c1_0.vehiculo from conductores c1_0 where c1_0.usuario_id=?
Hibernate: select c1_0.id_conductor,c1_0.calificacion_promedio,c1_0.creado_en,c1_0.disponible,c1_0.licencia,c1_0.usuario_id,c1_0.vehiculo from conductores c1_0 where c1_0.usuario_id=?
Hibernate: select c1_0.id_conductor,c1_0.calificacion_promedio,c1_0.creado_en,c1_0.disponible,c1_0.licencia,c1_0.usuario_id,c1_0.vehiculo from conductores c1_0 where c1_0.usuario_id=?
Hibernate: select c1_0.id_conductor,c1_0.calificacion_promedio,c1_0.creado_en,c1_0.disponible,c1_0.licencia,c1_0.usuario_id,c1_0.vehiculo from conductores c1_0 where c1_0.usuario_id=?
Hibernate: select c1_0.id_conductor,c1_0.calificacion_promedio,c1_0.creado_en,c1_0.disponible,c1_0.licencia,c1_0.usuario_id,c1_0.vehiculo from conductores c1_0 where c1_0.usuario_id=?
Hibernate: select c1_0.id_conductor,c1_0.calificacion_promedio,c1_0.creado_en,c1_0.disponible,c1_0.licencia,c1_0.usuario_id,c1_0.vehiculo from conductores c1_0 where c1_0.usuario_id=?
Hibernate: select c1_0.id_conductor,c1_0.calificacion_promedio,c1_0.creado_en,c1_0.disponible,c1_0.licencia,c1_0.usuario_id,c1_0.vehiculo from conductores c1_0 where c1_0.usuario_id=?
Hibernate: select c1_0.id_conductor,c1_0.calificacion_promedio,c1_0.creado_en,c1_0.disponible,c1_0.licencia,c1_0.usuario_id,c1_0.vehiculo from conductores c1_0 where c1_0.usuario_id=?
Hibernate: select c1_0.id_conductor,c1_0.calificacion_promedio,c1_0.creado_en,c1_0.disponible,c1_0.licencia,c1_0.usuario_id,c1_0.vehiculo from conductores c1_0 where c1_0.usuario_id=?
Hibernate: select c1_0.id_conductor,c1_0.calificacion_promedio,c1_0.creado_en,c1_0.disponible,c1_0.licencia,c1_0.usuario_id,c1_0.vehiculo from conductores c1_0 where c1_0.usuario_id=?
Hibernate: select c1_0.id_conductor,c1_0.calificacion_promedio,c1_0.creado_en,c1_0.disponible,c1_0.licencia,c1_0.usuario_id,c1_0.vehiculo from conductores c1_0 where c1_0.usuario_id=?
Hibernate: select c1_0.id_conductor,c1_0.calificacion_promedio,c1_0.creado_en,c1_0.disponible,c1_0.licencia,c1_0.usuario_id,c1_0.vehiculo from conductores c1_0 where c1_0.usuario_id=?
Hibernate: select c1_0.id_conductor,c1_0.calificacion_promedio,c1_0.creado_en,c1_0.disponible,c1_0.licencia,c1_0.usuario_id,c1_0.vehiculo from conductores c1_0 where c1_0.usuario_id=?
Hibernate: select c1_0.id_conductor,c1_0.calificacion_promedio,c1_0.creado_en,c1_0.disponible,c1_0.licencia,c1_0.usuario_id,c1_0.vehiculo from conductores c1_0 where c1_0.usuario_id=?
Hibernate: select c1_0.id_conductor,c1_0.calificacion_promedio,c1_0.creado_en,c1_0.disponible,c1_0.licencia,c1_0.usuario_id,c1_0.vehiculo from conductores c1_0 where c1_0.usuario_id=?
Hibernate: select c1_0.id_conductor,c1_0.calificacion_promedio,c1_0.creado_en,c1_0.disponible,c1_0.licencia,c1_0.usuario_id,c1_0.vehiculo from conductores c1_0 where c1_0.usuario_id=?
Hibernate: select c1_0.id_conductor,c1_0.calificacion_promedio,c1_0.creado_en,c1_0.disponible,c1_0.licencia,c1_0.usuario_id,c1_0.vehiculo from conductores c1_0 where c1_0.usuario_id=?
Hibernate: select c1_0.id_conductor,c1_0.calificacion_promedio,c1_0.creado_en,c1_0.disponible,c1_0.licencia,c1_0.usuario_id,c1_0.vehiculo from conductores c1_0 where c1_0.usuario_id=?
Hibernate: select c1_0.id_conductor,c1_0.calificacion_promedio,c1_0.creado_en,c1_0.disponible,c1_0.licencia,c1_0.usuario_id,c1_0.vehiculo from conductores c1_0 where c1_0.usuario_id=?
Hibernate: select c1_0.id_conductor,c1_0.calificacion_promedio,c1_0.creado_en,c1_0.disponible,c1_0.licencia,c1_0.usuario_id,c1_0.vehiculo from conductores c1_0 where c1_0.usuario_id=?
Hibernate: select c1_0.id_conductor,c1_0.calificacion_promedio,c1_0.creado_en,c1_0.disponible,c1_0.licencia,c1_0.usuario_id,c1_0.vehiculo from conductores c1_0 where c1_0.usuario_id=?
Hibernate: select c1_0.id_conductor,c1_0.calificacion_promedio,c1_0.creado_en,c1_0.disponible,c1_0.licencia,c1_0.usuario_id,c1_0.vehiculo from conductores c1_0 where c1_0.usuario_id=?
Hibernate: select c1_0.id_conductor,c1_0.calificacion_promedio,c1_0.creado_en,c1_0.disponible,c1_0.licencia,c1_0.usuario_id,c1_0.vehiculo from conductores c1_0 where c1_0.usuario_id=?
Hibernate: select c1_0.id_conductor,c1_0.calificacion_promedio,c1_0.creado_en,c1_0.disponible,c1_0.licencia,c1_0.usuario_id,c1_0.vehiculo from conductores c1_0 where c1_0.usuario_id=?
Hibernate: select c1_0.id_conductor,c1_0.calificacion_promedio,c1_0.creado_en,c1_0.disponible,c1_0.licencia,c1_0.usuario_id,c1_0.vehiculo from conductores c1_0 where c1_0.usuario_id=?
Hibernate: select c1_0.id_conductor,c1_0.calificacion_promedio,c1_0.creado_en,c1_0.disponible,c1_0.licencia,c1_0.usuario_id,c1_0.vehiculo from conductores c1_0 where c1_0.usuario_id=?
Hibernate: select c1_0.id_conductor,c1_0.calificacion_promedio,c1_0.creado_en,c1_0.disponible,c1_0.licencia,c1_0.usuario_id,c1_0.vehiculo from conductores c1_0 where c1_0.usuario_id=?
Hibernate: select c1_0.id_conductor,c1_0.calificacion_promedio,c1_0.creado_en,c1_0.disponible,c1_0.licencia,c1_0.usuario_id,c1_0.vehiculo from conductores c1_0 where c1_0.usuario_id=?
Hibernate: select c1_0.id_conductor,c1_0.calificacion_promedio,c1_0.creado_en,c1_0.disponible,c1_0.licencia,c1_0.usuario_id,c1_0.vehiculo from conductores c1_0 where c1_0.usuario_id=?
Hibernate: select c1_0.id_conductor,c1_0.calificacion_promedio,c1_0.creado_en,c1_0.disponible,c1_0.licencia,c1_0.usuario_id,c1_0.vehiculo from conductores c1_0 where c1_0.usuario_id=?
Hibernate: select c1_0.id_conductor,c1_0.calificacion_promedio,c1_0.creado_en,c1_0.disponible,c1_0.licencia,c1_0.usuario_id,c1_0.vehiculo from conductores c1_0 where c1_0.usuario_id=?
Hibernate: select c1_0.id_conductor,c1_0.calificacion_promedio,c1_0.creado_en,c1_0.disponible,c1_0.licencia,c1_0.usuario_id,c1_0.vehiculo from conductores c1_0 where c1_0.usuario_id=?
Hibernate: select c1_0.id_conductor,c1_0.calificacion_promedio,c1_0.creado_en,c1_0.disponible,c1_0.licencia,c1_0.usuario_id,c1_0.vehiculo from conductores c1_0 where c1_0.usuario_id=?
Hibernate: select c1_0.id_conductor,c1_0.calificacion_promedio,c1_0.creado_en,c1_0.disponible,c1_0.licencia,c1_0.usuario_id,c1_0.vehiculo from conductores c1_0 where c1_0.usuario_id=?
Hibernate: select c1_0.id_conductor,c1_0.calificacion_promedio,c1_0.creado_en,c1_0.disponible,c1_0.licencia,c1_0.usuario_id,c1_0.vehiculo from conductores c1_0 where c1_0.usuario_id=?
Hibernate: select c1_0.id_conductor,c1_0.calificacion_promedio,c1_0.creado_en,c1_0.disponible,c1_0.licencia,c1_0.usuario_id,c1_0.vehiculo from conductores c1_0 where c1_0.usuario_id=?
Hibernate: select c1_0.id_conductor,c1_0.calificacion_promedio,c1_0.creado_en,c1_0.disponible,c1_0.licencia,c1_0.usuario_id,c1_0.vehiculo from conductores c1_0 where c1_0.usuario_id=?
Hibernate: select c1_0.id_conductor,c1_0.calificacion_promedio,c1_0.creado_en,c1_0.disponible,c1_0.licencia,c1_0.usuario_id,c1_0.vehiculo from conductores c1_0 where c1_0.usuario_id=?
Hibernate: select c1_0.id_conductor,c1_0.calificacion_promedio,c1_0.creado_en,c1_0.disponible,c1_0.licencia,c1_0.usuario_id,c1_0.vehiculo from conductores c1_0 where c1_0.usuario_id=?
Hibernate: select c1_0.id_conductor,c1_0.calificacion_promedio,c1_0.creado_en,c1_0.disponible,c1_0.licencia,c1_0.usuario_id,c1_0.vehiculo from conductores c1_0 where c1_0.usuario_id=?
Hibernate: select c1_0.id_conductor,c1_0.calificacion_promedio,c1_0.creado_en,c1_0.disponible,c1_0.licencia,c1_0.usuario_id,c1_0.vehiculo from conductores c1_0 where c1_0.usuario_id=?
Hibernate: select c1_0.id_conductor,c1_0.calificacion_promedio,c1_0.creado_en,c1_0.disponible,c1_0.licencia,c1_0.usuario_id,c1_0.vehiculo from conductores c1_0 where c1_0.usuario_id=?
Hibernate: select c1_0.id_conductor,c1_0.calificacion_promedio,c1_0.creado_en,c1_0.disponible,c1_0.licencia,c1_0.usuario_id,c1_0.vehiculo from conductores c1_0 where c1_0.usuario_id=?
Hibernate: select c1_0.id_conductor,c1_0.calificacion_promedio,c1_0.creado_en,c1_0.disponible,c1_0.licencia,c1_0.usuario_id,c1_0.vehiculo from conductores c1_0 where c1_0.usuario_id=?
Hibernate: select v1_0.id_viaje,v1_0.actualizado_en,v1_0.conductor_id,v1_0.creado_en,v1_0.destino_lat,v1_0.destino_lng,v1_0.distancia_km,v1_0.estado,v1_0.multiplicador_demanda,v1_0.origen_lat,v1_0.origen_lng,v1_0.pasajero_id,v1_0.precio,v1_0.precio_base from viajes v1_0 where v1_0.estado=?
Hibernate: select c1_0.id_conductor,c1_0.calificacion_promedio,c1_0.creado_en,c1_0.disponible,c1_0.licencia,c1_0.usuario_id,c1_0.vehiculo from conductores c1_0 where c1_0.usuario_id=?