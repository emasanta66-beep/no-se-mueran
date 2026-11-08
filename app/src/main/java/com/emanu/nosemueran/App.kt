package com.emanu.nosemueran

import android.app.Application
import com.emanu.nosemueran.data.Store
import com.emanu.nosemueran.notifications.Notifications
import com.emanu.nosemueran.notifications.Reminders
import com.emanu.nosemueran.update.UpdateScheduler
import com.emanu.nosemueran.update.Updater

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        Store.init(this)
        Notifications.ensureChannel(this)
        Updater.cleanup(this)
        if (Store.notifications.value) {
            Reminders.schedule(this)
        }
        // Chequeo diario opcional: solo avisa si hay una versión nueva.
        UpdateScheduler.schedule(this)
    }
}
