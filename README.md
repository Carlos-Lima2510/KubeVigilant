# 🛡️ KubeVigilant

> Un monitor de salud para Kubernetes ligero, en tiempo real y tolerante a fallos, construido con Java y la API oficial de Kubernetes.

![Java](https://img.shields.io/badge/Java-17%2B-ED8B00?style=flat&logo=openjdk&logoColor=white)
![Kubernetes](https://img.shields.io/badge/Kubernetes-Client-326CE5?style=flat&logo=kubernetes&logoColor=white)
![Build](https://img.shields.io/badge/build-Maven-C71A36?style=flat&logo=apache-maven&logoColor=white)

**KubeVigilant** es una herramienta de observabilidad que se conecta a tu clúster local (MicroK8s/Minikube) y vigila continuamente el ciclo de vida de los Pods. Su objetivo principal es la detección proactiva del temido estado `CrashLoopBackOff`, alertando en consola mucho antes de que un humano revise el dashboard.

[![Architecture](https://img.shields.io/badge/Architecture-View%20Public%20API-007EC6?style=for-the-badge&logo=uml&logoColor=white)](/documents/vistaPública.png) 

## 🚀 Características Principales

* **Vigilancia en Tiempo Real:** Utiliza la API de `Watch` de Kubernetes para recibir eventos (streaming) en lugar de hacer polling constante.
* **Detección Inteligente:** Algoritmo de filtrado que ignora el ruido de arranque y solo alerta sobre fallos reales de contenedores.
* **Fail-Fast Connection:** La fábrica de clientes valida la conexión al inicio e impide ejecuciones zombies si el clúster no está disponible.

---

## 🏗️ Arquitectura del Proyecto

* **Core (`/core`):** Manejo de infraestructura y conexión (Factory Pattern).
* **Watcher (`/watcher`):** Capa de escucha de eventos crudos de la API de K8s.
* **Logic (`/logic`):** El "cerebro". Interpreta los eventos y aplica reglas de negocio (ej. ¿Es esto un CrashLoop?).

## 🛠️ Pre-requisitos

- **Java JDK 11 o superior.**

- **Maven 3.6+.**

- Un clúster de Kubernetes corriendo localmente (**MicroK8s, Minikube, Docker Desktop**).

## ⚡ Guía de Inicio Rápido

1. **Compilar el proyecto**

Limpia los target antiguos y compila las fuentes:

```bash
mvn clean compile
```

2. **Ejecutar el vigilante**

Inicia la aplicación. Verás un mensaje indicando que la vigilancia ha comenzado:

```bash
mvn exec:java -Dexec.mainClass="com.vigilant.Main"
```

3. **Resultado**

En la terminal de KubeVigilant deberías ver inmediatamente:

```text
🚨 DETECCIÓN DE FALLO CRÍTICO
   📦 Pod: pod-suicida
   🏷️  NS:  default
   💥 Estado: CrashLoopBackOff
```