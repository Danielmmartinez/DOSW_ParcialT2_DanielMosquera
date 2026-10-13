# DOSW_ParcialT2_DanielMosquera

* **Nombre completo:** Daniel Camilo Mosquera
* **Grupo:** 1


* **URL Base en Azure:** `https://dosw-parcial2-danielmosquera.azurewebsites.net`
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
