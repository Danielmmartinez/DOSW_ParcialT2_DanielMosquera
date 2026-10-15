# DOSW_ParcialT2_DanielMosquera

* **Integrantes** Daniel Camilo Mosquera, Juan Manuel Garzon
* **Grupo:** 1
* **Equipo:** Verde

## Teoria

### 1. Asignación del equipo de Producto

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

### 2. Asignacion del equipo de Arquitectura

#### Diagrama de componentes general

![Diagrama de componentes general](/docs/uml/DC-General.png)

- El Cliente se comunica a la API Paw Connect mediante HTTPS, con autenticacion por JWT. LA API concentra toda la logica del sistema. MongoDB se usa como base de datos no relacional que almacena las cuatro colecciones del sistema.

#### Diagrama de componentes especifico

![Diagrama de componentes especifico](/docs/uml/DC-Especifico.png)

- Teniendo tres funcionalidades principales (Autenticacion, Solicitudes de adopcion y mascotas), decidimos manejarlo por tres funcionalidades en paralelo, lo cual queda consecuente a la tabla de endpoints ya escrita.

- Separamos los Mapper IN-OUT con Mapper y MapperEntity. Donde manejamos los request dto con el Mapper y transformamos a las entidades con el EntityMapper, respectivamente.

#### Diagrama de clases

![Diagrama de clases](/docs/uml/diagrama%20clases.png)

- Manejamos la jerarquia de refugios con el Patron de diseño Composite, con la clase Abstracta NodoRefugio, que permite invocar el metodo `obtenerMascotas()` sobre cualquier nodo, sin saber la estructura interna.

#### Preguntas

* **a.	¿Qué problemas aparecen cuando no se separan correctamente las capas? Presenten un ejemplo del caso y expliquen cómo distribuyen sus responsabilidades.**
  *  Puede generar acoplamiento, dificultad en test, no cumplir con Sigle Responsability de SOLID. Ejemplo: Si la validacion de "solo un adoptante con solicitud activa" vive en controller, no se puede reutilizar para aplazar.
* **b.	¿Qué diferencia existe entre un validador, una utilidad y un servicio? Ubiquen un ejemplo de cada uno en su diseño e indiquen su responsabilidad.**
  * Validador: Verifica reglas de negocio puntuales. Ejemplo: MascotaValidator
  * Utilidad: funciones puras sin estado. Ejemplo: FechaUtil, para calcular los 1-15 dias.
  * Servicio: Maneja el caso de uso completo. Ejemplo: SolicitudService

* **c. ¿Qué diferencia existe entre el Mapper IN y el Mapper OUT? Indiquen dónde se ubican y qué representaciones transforman cada uno.**
  * Mapper IN: Request dto a modelo
  * Mapper OUT: Modelo a Response dto
  * Se ubican en Controller.

* **d.	Retomen las tres reglas clasificadas en el paso anterior: ¿en qué componente debe ejecutarse cada una y por qué?**
  * Especie/tamaño valido con MascotaValidator
  * RegufioExistente y tipo Refugio con MascotaService
  * Edad 0-360 con MascotaValidator

### 3.

#### Modelo Embebido y Referenciado
Se utiliza una solución **híbrida** para balancear rendimiento e integridad:
* **Embebido (`Compatibilidad` en `Mascota`):**
  * Es una relación 1 a 1 de composición. La compatibilidad no tiene sentido sin la mascota y siempre se consulta junto a ella para validar adopciones.
* **Referenciado (`Mascota` hacia `Refugio`):** 
  * Cada mascota solo guarda el `refugioId`. El refugio es una entidad independiente que agrupa múltiples mascotas y pertenece a una jerarquía nacional.

---

#### Análisis de Criterios Técnicos

* **Consultas y Actualizaciones:**
  * *Consultas:* Lecturas rapidas. Al buscar una mascota en el catalogo, se obtienen sus datos y compatibilidad en una sola operacion.
  * *Actualizaciones:* Operaciones atomicas. Cambiar el estado de una mascota (`DISPONIBLE` a `EN_PROCESO`) o aplazar una fecha solo modifica un documento sin bloquear a otros.

* **Duplicación de Datos:**
  * Es mínima y controlada. Se evita repetir información institucional (como nombres y sedes de refugios) dentro de cada mascota; solo se almacena la referencia por ID.

* **Crecimiento de los Documentos:**
  * Al no usar listas que crezcan de forma muy grande, se evita la fragmentación de memoria, asi no superando el limitr.

---

#### Ventaja y Desventaja de la Alternativa Elegida

* **Ventaja:** Alto rendimiento, todo se puede consultar en una sola accion.
* **Desventaja:** Requiere una consulta adicional para la referencia (`refugioId`) si se desea mostrar la información completa de refugio.


### 4. Asignación del Equipo de Seguridad - Proteger el Acceso y la Información

#### a. Autenticación, Autorización e Integridad en Paw Connect

| Concepto | Definición | Dónde y cómo se aplica en la API | Ejemplo con tus Endpoints |
| :--- | :--- | :--- | :--- |
| **Autenticación** | Valida la identidad del usuario a través de credenciales. | En `POST /api/v1/auth/login`. Si las credenciales coinciden con los usuarios predefinidos, se emite un token JWT. | Si el correo o contraseña son incorrectos, la API responde `401 Unauthorized`. |
| **Autorización** | Valida si el usuario autenticado tiene el rol permitido para ejecutar una acción. | En los controladores mediante filtros de Spring Security según el rol (`Admin` vs `Adoptante`). | Si un `Adoptante` intenta hacer `POST /api/v1/mascotas`, el sistema lo rechaza con `403 Forbidden`. |
| **Integridad** | Garantiza que la información no sea alterada en tránsito ni en reposo. | Cifrado HTTPS en la red, contraseñas hasheadas con BCrypt en MongoDB y firma digital en el token JWT. | Evita que un usuario modifique el payload del token para cambiarse el rol de `Adoptante` a `Admin`. |

---

#### b. Dos Riesgos OWASP Top 10 aplicables a la API

1. **BOLA (Broken Object Level Authorization):**
  * *Escenario en la API:* Un adoptante intenta modificar una solicitud ajena cambiando el parámetro de la ruta en `PATCH /api/v1/solicitudes/{id}/aplazar`.
  * *Mitigación:* En la lógica de negocio se verifica que el `id` de la solicitud pertenezca al usuario extraído del token JWT (`SecurityContext`).
  * *Verificación:* Prueba enviando un token del Usuario A con el `{id}` de una solicitud del Usuario B; la API debe responder `403 Forbidden` y no alterar la fecha.

2. **BFLA (Broken Function Level Authorization):**
  * *Escenario en la API:* Un usuario con rol `Adoptante` descubre el endpoint administrativo e intenta enviar un `POST /api/v1/mascotas` para registrar animales.
  * *Mitigación:* Proteger la ruta a nivel de configuración de seguridad exigiendo exclusivamente el rol `Admin`.
  * *Verificación:* Petición a `POST /api/v1/mascotas` usando un Bearer token con rol `Adoptante`; debe retornar `403 Forbidden`.

---

#### c. Tipo de Autenticación más adecuado para el MVP

**Elección:** **JWT (JSON Web Tokens) con esquema Bearer Token (Stateless).**

**Justificación:**
1. **Sin estado (Stateless):** La API REST no guarda sesiones en el servidor, lo cual facilita el despliegue en contenedores/Azure y la integración limpia con MongoDB.
2. **Transporte de Roles:** El token almacena el rol (`Admin` o `Adoptante`) y el identificador en sus *claims*, permitiendo validar permisos en milisegundos sin consultar la base de datos en cada petición.
3. **Integración con Swagger:** Es el estándar directo para usar mediante el botón `Authorize` de OpenAPI/Swagger requerido en la fase de implementación.


### 5. Asignacion del Equipo de Calidad

#### Escenarios de prueba de "Adoptar una mascota"

Para cada regla de negocio se aplica el ciclo TDD: se escribe la prueba que falla (Red), se implementa lo minimo para pasarla (Green) y se refactoriza sin romperla.

Los escenarios cubiertos son:
- Happy path: 201, solicitud APROBADA, mascota EN_PROCESO
- E04 incompatibilidad: 422, mascota DISPONIBLE, sin solicitud
- E06 mascota no disponible: 409, sin solicitud
- E07 fecha invalida: 422, sin cambios
- E08 segunda solicitud: 409, sin segunda solicitud ni cambio de mascota
- Errores de datos: 400,401,403,404

#### Verificacion del orden

Definimos `SolicitudService.adoptar()` con la precedencia 401/403 - 404 - 409 (E06) - 409 (E08) - 422 (E04) - 422 (E07) - 201. Comprobando con pruebas que incumplen dos reglas a la vez y verifica que devuelve el codigo de la primera regla del orden.

#### Preguntas para la revision con Calidad

* **a.	Explique cómo las pruebas aportan evidencia del cumplimiento de las reglas de negocio. Distingan que verificarian con pruebas unitarias**
  *  Cada regla la traducimos en al menos una prueba con el mismo nombre. Las pruebas unitarias verifican la logica aislada de cada regla. En cambio, las pruebas con TDD evaluamos el flujo con persistencia real y codigos HTTP
* **b.	Explique brevemente y con justificación si considera que las pruebas por sí solas garantizan la ausencia de errores.**
  * No, porque la cobertura mide ejecucion, no cubren casos extremos como fallos de dependencias externas, problemas de configuracion o despliegue.

          
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


## Implementacion y evidencias

### Evidencias
---------------------------------------------------------------

**Api Compilada**

![API compilada](/docs/images/Compilado.png)

---------------------------------------------------------------

**Reporte final Jacoco**
![Jacoco Reporte final](/docs/images/jaCoCo-report-Ultimo.png)

---------------------------------------------------------------

**Pipeline de ultimo Merge Pull Request**

![Pipeline final](/docs/images/pipeline-ultimo-merge.png)

---------------------------------------------------------------

**Api funcionando en local**

![API - Local](/docs/images/API%20en%20local.png)

---------------------------------------------------------------

**Api funcionando en Azure**

![API - Azure](/docs/images/API%20en%20azure.png)

---------------------------------------------------------------

### Instrucciones

- **Ejecucion pruebas**: `mvn clean verify`. Usar registros generados en el `/config/seeder`

- **Ejecutar aplicacion:** `mvn spring-boot:run`, e ingresar a `http://localhost:8080/swagger-ui/index.html#/`