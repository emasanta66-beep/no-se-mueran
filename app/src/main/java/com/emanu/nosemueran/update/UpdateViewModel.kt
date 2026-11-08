package com.emanu.nosemueran.update

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

sealed interface UpdateState {
    data object Idle : UpdateState
    data object Checking : UpdateState
    data object UpToDate : UpdateState
    data class Available(val info: UpdateInfo) : UpdateState
    data class Downloading(val progress: Int) : UpdateState
    data class Downloaded(val info: UpdateInfo, val file: File) : UpdateState
    data class Failed(val message: String) : UpdateState
}

class UpdateViewModel(app: Application) : AndroidViewModel(app) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val _state = MutableStateFlow<UpdateState>(UpdateState.Idle)
    val state: StateFlow<UpdateState> = _state

    fun check() {
        if (_state.value is UpdateState.Checking) return
        _state.value = UpdateState.Checking
        scope.launch {
            Updater.check(getApplication()).fold(
                onSuccess = { info ->
                    _state.value = if (info == null) {
                        UpdateState.UpToDate
                    } else {
                        UpdateState.Available(info)
                    }
                },
                onFailure = { _state.value = UpdateState.Failed(it.friendly()) }
            )
        }
    }

    fun download(info: UpdateInfo) {
        if (_state.value is UpdateState.Downloading) return
        _state.value = UpdateState.Downloading(0)
        scope.launch {
            Updater.download(getApplication(), info) { percent ->
                if (percent in 0..100) {
                    _state.value = UpdateState.Downloading(percent)
                }
            }.fold(
                onSuccess = { file -> _state.value = UpdateState.Downloaded(info, file) },
                onFailure = { _state.value = UpdateState.Failed(it.friendly()) }
            )
        }
    }

    fun reset() {
        _state.value = UpdateState.Idle
    }

    override fun onCleared() {
        scope.cancel()
        super.onCleared()
    }

    private fun Throwable.friendly(): String = when (this) {
        is UnknownHostException -> "No hay conexión a internet."
        is SocketTimeoutException -> "La conexión tardó demasiado. Probá de nuevo."
        is IOException -> "No se pudo conectar con GitHub. Revisá tu internet."
        else -> message ?: "No se pudo comprobar la actualización."
    }
}
