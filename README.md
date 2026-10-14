# DOSW_ParcialT2_DanielMosquera

* **Integrantes Pareja** Daniel Camilo Mosquera Martinez, Juan Manuel Garzon 
* **Color:** VERDE

## Teoria

### 1. 1.	Asignación del equipo de Producto

| Módulo | Método  | Endpoint | Idempotencia | Acceso | Validaciones de entrada | Validaciones de Negocio | Datos de Entrada | Ejemplo de entrada | Datos de Salida | Ejemplo de Salida | Codigo HTTP |
|:-------|:--------|:---------|:-------------|:-------|:------------------------|:------------------------|:-----------------|:-------------------|:----------------|:------------------|-----|
| **Mascotas**   | `POST`  | `/api/v1/mascotas` | No | Admin | <ul><li>La edad debe ser entera positiva</li></ul> | <ul><li>La especie debe ser PERRO, GATO, CONEJO, AVE o REPTIL</li><li>El tamaño debe ser PEQUEÑO, MEDIANO, GRANDE</li><li>El refugio debe existir</li></lu> | <ul><li>`nombre` (text, Obligatorio)</li> <li>`especie`(enum, Obligatorio)</li> <li>`edadMeses`(numero, Obligatorio)</li> <li>`tamano`(enum, Obligatorio)</li> <li>`compatibleNinos` (boolean, Obligatorio)</li> <li>`compatibleOtrasMascotas`(boolean, Obligatorio)</li><li>`refugioId`(text,Obligatorio)</li> <li>compatibleEspaciosReducidos(boolean, Obligatorio)</li>| {<br>"nombre": "Max",<br>"especie": "PERRO",<br>"edadMeses": 24, <br>"tamano": "MEDIANO", <br>"refugioId": "65f...", <br>"compatibleNinos": true, <br>"compatibleOtrasMascotas": true, <br>"compatibleEspaciosReducidos": true <br>} | <li>`idMascota` (text)</li> | {<br>"idMascota": "PERRO-001"<br>} | <ul><li>`201 created` (Mascota creada con exito)</li> <li>`400` (JSON mal formado)</li> <li>`404` (refugio no existe: E03)</li> <li>`422` (datos invalidos: E02)</li> <li>`403` (Rol incorrecto)</li> <li>`401` (Sin token)</li></ul>|
|**Adopcion**| `POST`  | `/api/v1/solucitudes` | No | Adoptante | <li>Fecha entre 1 y 15 dias a partir de hoy</li> | <ul><li>La mascota debe existir y estar disponible</li> <li>El adoptante no debe tener solicitud activa</li><li>condiciones compatibles con la mascota</li></ul> | <ul><li>`idMascota` (text, Obligatorio)</li> <li>`fechaRecibimiento` (fecha, Obligatorio)</li> <li>`tieneNinos` (boolean, Obligatorio)</li> <li>`tieneOtrasMascotas` (boolean, Obligatorio)</li> <li>`tieneEspacioReducido`(boolean, obligatorio)</li></ul> | {<br>"idMascota": "PERRO-01", <br>"fechaRecibimiento": "2026-05-20", <br>"tieneNinos": true, <br>"tieneOtrasMascotas": false, <br>"tieneEspacioReducido": false<br>} | <ul><li>`idSolicitud` (text, Obligatorio)</li> <li>`estadoSolicitud` (enum, Obligatorio)</li></ul> | {<br>"idSolicitud": "SOL-001", <br>"estadoSolicitud": "APROBADA"<br>} | <ul> <li>`201` (Solicitud de adopcion creada con exito)</li> <li>`400` (JSON mal formado)</li> <li>`404`(Perro con ese id no existe)</li> <li>`422` (incompatibilidad, fecha invalida, ninos, otrasMascotas, espacios reducidos, fecha invalida)</li> <li>`409` (mascota adoptada o en proceso, adoptante tiene una solicitud aprobada activa)</li> <li>`403` (Rol incorrecto)</li> <li>`401` (Sin token)</li></ul>|
|**Adopcion**| `GET` |`/api/v1/solicitudes/adoptadas`| Si | Adoptante, Admin | - | - | - | - |  <ul><li>`idMascota` (text, Obligatorio)</li> <li>`estadoMascota`(enum, Obligatorio) </li> <li>`fechaRecibimiento` (fecha, Obligatorio)</li> <li>`tieneNinos`(boolean, Obligatorio)</li> <li>`tieneOtrasMascotas`(boolean, Obligatorio)</li> <li>`tieneEspacioReducido`(boolean, obligatorio)</li></ul> | {<br>"idMascota": "PERRO-01", <br>"estadoMascota": "EN_PROCESO" <br>"fechaRecibimiento": "2026-05-20", <br>"tieneNinos": true, <br>"tieneOtrasMascotas": false, <br>"tieneEspacioReducido": false<br>} | <ul><li>`200` (listado ordenado descendente por fecha, puede ser vacio: E11)</li> <li>`401` (Sin token)</li> <li>`400` (parametros invalidos)</li> </ul> |
| **Adopcion**   | `PATCH` | `/api/v1/solicitudes/{id}/aplazar` | No | Adoptante | <li>Fecha posterior a la actual y no superar los 15 dias adicionales</li> | <ul><li>La solicitud debe estar aprobada</li> <li>maximo un aplazamiento por solicitud</li> | <ul><li>`idSolicitud` (text, Obligatorio)</li> <li>`nuevaFechaRecibimiento` (fecha, Obligatorio)</li><ul> | {<br>"idSolicitud": "SOL-002", <br>"nuevaFechaRecibimiento": "2026-10-16"<br>} | <ul><li>`idSolicitud` (text)</li> <li>`fechaNuevaRecibimiento`(fecha)</li></ul> | {<br>"idSolicitud": "SOL-002", <br>"fechaNuevaRecibimiento": "2026-10-16"<br>} | <ul> <li>`200` (Aplazada correctamente)</li> <li>`400` (JSON mal formado)</li> <li>`403` (Rol incorrecto)</li> <li>`404` (No existe solicitud con ese id)</li> <li>`409` (solicitud ya aplazada)</li> <li>`422` (Fecha no valida)</li> <`401` (Sin token)> </ul> |
|**Auth**| `POST`  |`/api/v1/auth/login`| No | Adoptante, Admin | <ul><li>El correo tenga el formato correcto </li><ul>| <li>El administrador y los adoptantes son usuarios predefinidos</li> | <ul><li>`correo` (text, Obligatorio)</li> <li>`contraseña` (text,Obligatorio) </li></ul>| {<br>"correo": "Daniel@gmail.com", "contrasena": "Constraseña124"<br>} | tokenJWT (Text) | {<br>"tokenJWT": "eNi7d68h12kjK..."<br>} | <ul> <li>`200` (JWT generado)</li> <li>`401` (Credenciales invalidas)</li></ul> |


#### Preguntas de Revision
* **a. ¿Cuál es la diferencia entre una validación de entrada y una validación de negocio? Seleccionen tres reglas del caso, incluyendo al menos una de cada tipo, y justifiquen su clasificación.**
  *  La diferencia principal es que la validacion de entrada se enfoca en el valor ingresado (Forma, tipo, presencia, formato del dato), y la validacion de negocio se enfoca en las reglas de negocio sobre datos ya bien formados.
  - "Especie dentro de valores permitidos": negocio, porque el dato es un string valido, pero debe pertenecer al enum.
  - "El refugio debe existir y ser de tuoi REFUGIO": negocio
  - "Edad entre 0 y 360": mixta, porque el tipo entero es de entrada, y el rango de negocio
* **b.	En crear solicitud, ¿cómo debe responder la API si fallan varias condiciones? Expliquen qué error se informa y qué cambios deben evitarse.**
  * Deberia responder con un unico codigo siguiendo un orden de precedencia: primero `400` (JSON malformado) > `422` (Validaciones de campo) > `404` (Refugio no existe) > `409` (duplicado). Adenas ningun cambio deberia persistirse, y se devuelve `422` con lista de campos invalidos.

* **URL Base en Azure:** https://dosw-parcial2-danielmosquera.azurewebsites.net
* **URL Health Check:** [https://dosw-parcial2-danielmosquera.azurewebsites.net/api/v1/health](https://dosw-parcial2-danielmosquera.azurewebsites.net/api/v1/health)
* **URL Swagger UI:** [https://dosw-parcial2-danielmosquera.azurewebsites.net/swagger-ui/index.html](https://dosw-parcial2-danielmosquera.azurewebsites.net/swagger-ui/index.html)


# Construir la imagen local
docker build -t dosw-parcial:local .

# Ejecutar el contenedor
docker run -d -p 8080:8080 \
-e JWT_SECRET="dGhpc0lzQVZlcnlTZWN1cmVTZWNyZXRLZXlGb3JKV1RUb2tlbnNPbmx5Rm9yRGV2MQ==" \
--name dosw-servicio dosw-parcial:local

Resultado de las pruebas en verde:
![Imagen](docs/images/pruebas-unitarias.png)
Reporte de cobertura generado por JaCoCo (target/site/jacoco/index.html):
![Imagen](docs/images/JaCoCo-report.png)
Pipeline en GitHub Actions en verde:
![Imagen](docs/images/actions-pipeline.png)
