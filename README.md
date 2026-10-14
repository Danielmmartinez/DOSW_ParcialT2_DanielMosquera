# DOSW_ParcialT2_DanielMosquera

* **Integrantes Pareja** Daniel Camilo Mosquera Martinez, Juan Manuel Garzon 
* **Color:** VERDE

## Teoria

### 1. 1.	Asignación del equipo de Producto

| Módulo | Método  | Endpoint | Idempotencia | Acceso | Validaciones de entrada | Validaciones de Negocio | Datos de Entrada | Ejemplo de entrada | Datos de Salida | Ejemplo de Salida |
|:-------|:--------|:---------|:-------------|:-------|:------------------------|:------------------------|:-----------------|:-------------------|:----------------|:------------------|
| **Mascotas**   | `POST`  | `/api/v1/mascotas` | No | Admin | - | <ul><li>La especie debe ser PERRO, GATO, CONEJO, AVE o REPTIL</li><li>El tamaño debe ser PEQUEÑO, MEDIANO, GRANDE</li><li>El refugio debe existir</li></lu> | <ul><li>`nombre` (text, Obligatorio)</li> <li>`especie`(enum, Obligatorio)</li> <li>`edadMeses`(numero, Obligatorio)</li> <li>`tamano`(enum, Obligatorio)</li> <li>`compatibleNinos` (boolean, Obligatorio)</li> <li>`compatibleOtrasMascotas`(boolean, Obligatorio)</li><li>`refugioId`(text,Obligatorio)</li> <li>compatibleEspaciosReducidos(boolean, Obligatorio)</li>| {<br>"nombre": "Max",<br>"especie": "PERRO",<br>"edadMeses": 24, <br>"tamano": "MEDIANO", <br>"refugioId": "65f...", <br>"compatibleNinos": true, <br>"compatibleOtrasMascotas": true, <br>"compatibleEspaciosReducidos": true <br>} | idMascota(text) | {<br>"idMascota": "43c..."<br>} | 
|**Adopcion**| `POST`  | `/api/v1/solucitudes` | No | Adoptante | Fecha entre 1 y 15 dias a partir de hoy | <ul><li>La mascota debe estar disponible</li> <li>El adoptante no debe tener solicitud activa</li><li>condiciones compatibles con la mascota</li></ul> | idMascota (text, Obligatorio), fechaRecibimiento (fecha, Obligatorio), tieneNinos(boolean, Obligatorio), tieneOtrasMascotas(boolean, Obligatorio), tieneEspacioReducido(boolean, obligatorio) | {<br>"idMascota": "PERRO-01", <br>"fechaRecibimiento": "2026-05-20", <br>"tieneNinos": true, <br>"tieneOtrasMascotas": false, <br>"tieneEspacioReducido": false<br>} | idSolicitud (text, Obligatorio), estadoSolicitud (enum, Obligatorio) | {<br>"idSolicitud": "SOL-001", <br>"estadoSolicitud": "APROBADA"<br>} |
|**Adopcion**| `GET` |`/api/v1/solicitudes/adoptadas`| Si | Adoptante, Admin | - | - | - | - |  idMascota (text, Obligatorio), estadoMascota(enum, Obligatorio) fechaRecibimiento (fecha, Obligatorio), tieneNinos(boolean, Obligatorio), tieneOtrasMascotas(boolean, Obligatorio), tieneEspacioReducido(boolean, obligatorio) | {<br>"idMascota": "PERRO-01", <br>"estadoMascota": "EN_PROCESO" <br>"fechaRecibimiento": "2026-05-20", <br>"tieneNinos": true, <br>"tieneOtrasMascotas": false, <br>"tieneEspacioReducido": false<br>} | 
| **Adopcion**   | `PATCH` | `/api/v1/solicitudes/{id}/aplazar` | No | Adoptante | Fecha posterior a la actual y no superar los 15 dias adicionales | La solicitud debe estar aprobada, maximo un aplazamiento por solicitud | idSolicitud (text, Obligatorio), nuevaFechaRecibimiento (fecha, Obligatorio) | {<br>"idSolicitud": "SOL-002", <br>"nuevaFechaRecibimiento": "2026-10-16"<br>} | idSolicitud (text), fechaNuevaRecibimiento(fecha) | {<br>"idSolicitud": "SOL-002", <br>"fechaNuevaRecibimiento": "2026-10-16"<br>} |
|**Auth**| `POST`  |`/api/v1/auth/login`| No | Adoptante, Admin | El correo tenga el formato correcto | El administrador y los adoptantes son usuarios predefinidos | <ul><li>`correo` (text, Obligatorio)</li> <li>`contraseña` (text,Obligatorio) </li></ul>| {<br>"correo": "Daniel@gmail.com", "contrasena": "Constraseña124"} | tokenJWT (Text) | {<br>"tokenJWT": "eNi7d68h12kjK..."<br>} |


## Preguntas de Revision
* **a. ¿Cuál es la diferencia entre una validación de entrada y una validación de negocio? Seleccionen tres reglas del caso, incluyendo al menos una de cada tipo, y justifiquen su clasificación.**
  *  La diferencia principal es que la validacion de entrada se enfoca en el valor ingresado, y la validacion de negocio se enfoca en las reglas de negocio.
* **b.	En crear solicitud, ¿cómo debe responder la API si fallan varias condiciones? Expliquen qué error se informa y qué cambios deben evitarse.**
  * No debe permitir que los datos erroneos entren a la base de datos y debe informar que valores estan erroneos.

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
