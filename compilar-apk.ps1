$ErrorActionPreference = "Stop"
$env:ANDROID_HOME = "C:\Android\sdk"
$env:ANDROID_SDK_ROOT = "C:\Android\sdk"
$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-17.0.21.9-hotspot"

$project = Split-Path -Parent $MyInvocation.MyCommand.Path
Write-Host "Compilando No se mueran..." -ForegroundColor Green

& "C:\Android\gradle-8.13\bin\gradle.bat" -p $project assembleDebug --console=plain

if ($LASTEXITCODE -ne 0) {
    Write-Host "La compilacion fallo." -ForegroundColor Red
    exit $LASTEXITCODE
}

$apk = Join-Path $project "app\build\outputs\apk\debug\app-debug.apk"
$dest = Join-Path $project "NoSeMueran.apk"
Copy-Item $apk $dest -Force

Write-Host ""
Write-Host "Listo: $dest" -ForegroundColor Green
Write-Host "Pasala al telefono e instalala." -ForegroundColor Green
