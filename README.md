# 🌱 No se mueran

App de plantas para Android. Tus plantas, fotos y notas viven en tu teléfono. La única conexión a internet es para buscar y descargar actualizaciones desde este repositorio.

## Qué hace

- **Mi jardín**: agregá tus plantas con foto, especie, ubicación y notas. La tarjeta te dice cuándo toca regar.
- **Recordatorios de riego**: un aviso al día (a las 9) si alguna planta necesita agua.
- **Catálogo**: 29 especies de interior y huerta urbana con luz, agua, humedad, temperatura, dificultad y consejos.
- **Diario**: registrá fotos y notas de cada planta para ver cómo crece.
- **Diagnóstico por foto**: la app analiza los colores de la foto (verde, amarillo, marrón, zonas oscuras), te hace 4 preguntas y te da una recomendación. Sin internet.
- **Sin cuenta y sin datos que salgan del teléfono.**

## Actualizaciones

La app se actualiza desde los **releases de este repositorio**:

1. Abrí **Ajustes → Actualizaciones → Buscar actualización**.
2. Si hay una versión nueva, tocá **Descargar** y después **Instalar**.
3. La primera vez, Android te va a pedir permitir **"Instalar apps desconocidas"** para No se mueran. Es solo para aplicar la actualización.
4. La app también revisa una vez al día y te avisa con una notificación si hay una versión nueva.

El APK se descarga desde GitHub y la app verifica que el paquete, la versión y la **firma** coincidan con la app instalada antes de abrir el instalador. Tus plantas y fotos se conservan al actualizar.

Última versión: [Releases](https://github.com/emasanta66-beep/no-se-mueran/releases)

## Capturas

En la carpeta `capturas/` hay pantallas reales de la app corriendo en un Android 15.

## Datos técnicos

- Paquete: `com.emanu.nosemueran`
- Android mínimo: 8.0 (API 26)
- Tamaño: ~17 MB
- Permisos:
  - Notificaciones (recordatorios y aviso de actualización).
  - Internet (solo para consultar y bajar actualizaciones).
  - Instalar apps desconocidas (para aplicar la actualización).
  - Los que usa WorkManager para revisar el riego y las actualizaciones.
- La app no pide cuenta ni envía datos personales.

## Cómo instalar la APK

1. Descargá `NoSeMueran.apk` desde la sección **Releases**.
2. En el teléfono, abrí el archivo desde la app de Archivos.
3. Si Android pregunta, permití **"Instalar apps desconocidas"** para la app de Archivos.
4. Instalar → Abrir.

> Es una APK de depuración firmada, pensada para uso personal. No está publicada en Play Store.

## Cómo publicar una actualización

1. Subí `versionCode` y `versionName` en `app/build.gradle.kts`.
2. Compilá con `.\compilar-apk.ps1`.
3. Creá un release con tag `v<versionCode>` (por ejemplo `v3`) y adjuntá `NoSeMueran.apk`.
   - El número del tag es el que la app compara con su `versionCode`.
4. La app lo detecta en la próxima búsqueda (manual o diaria).

Es importante compilar en esta misma PC: la actualización tiene que estar firmada con la misma clave que la app instalada. Si cambiás de máquina, hay que generar y conservar una clave de firma propia.

## Cómo volver a compilarla

Requisitos ya instalados en esta PC:

- JDK 17 (`C:\Program Files\Eclipse Adoptium\jdk-17.0.21.9-hotspot`)
- Android SDK (`C:\Android\sdk`)
- Gradle 8.13 (`C:\Android\gradle-8.13`)

Doble clic en `compilar-apk.ps1` o desde PowerShell:

```powershell
.\compilar-apk.ps1
```

La APK queda en `app\build\outputs\apk\debug\app-debug.apk` y se copia como `NoSeMueran.apk` en la raíz del proyecto.

## Estructura

```
app/src/main/java/com/emanu/nosemueran/
  data/          modelos, almacenamiento JSON y catálogo
  notifications/ recordatorios con WorkManager
  update/        chequeo, descarga e instalación de actualizaciones
  ui/            pantallas Compose, navegación, tema
  util/          fotos y análisis de imagen
```

## Límites conocidos

- El diagnóstico es **orientativo**: mide colores, no reemplaza a un especialista.
- No identifica especies por foto (eso requiere un modelo de IA o internet).
- Los recordatorios dependen de que Android no bloquee la app; si no llegan, revisá Ahorro de batería.
- Las fotos se guardan en el almacenamiento privado de la app.
- Las actualizaciones requieren conexión a internet; el resto de la app funciona offline.
