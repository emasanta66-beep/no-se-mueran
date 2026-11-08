package com.emanu.nosemueran.update

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

/** Chequeo diario silencioso: si hay versión nueva, avisa con una notificación. */
class UpdateWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val info = Updater.check(applicationContext).getOrNull() ?: return Result.success()
        Updater.notifyUpdate(applicationContext, info)
        return Result.success()
    }
}
