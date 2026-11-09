package com.emanu.nosemueran

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import com.emanu.nosemueran.ui.AppNav
import com.emanu.nosemueran.ui.theme.NoSeMueranTheme
import com.emanu.nosemueran.update.Updater

class MainActivity : ComponentActivity() {

    private var openUpdates by mutableStateOf(false)

    private val notificationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        openUpdates = intent?.getBooleanExtra(Updater.EXTRA_OPEN_UPDATES, false) == true
        Updater.cancelUpdateNotification(this)

        if (Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        setContent {
            NoSeMueranTheme {
                AppNav(openUpdates = openUpdates, onUpdatesHandled = { openUpdates = false })
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        if (intent.getBooleanExtra(Updater.EXTRA_OPEN_UPDATES, false)) {
            Updater.cancelUpdateNotification(this)
            openUpdates = true
        }
    }
}
