package com.pilltack.app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.pilltack.app.data.Prefs
import com.pilltack.app.ui.AppRoot
import com.pilltack.app.ui.PT
import com.pilltack.app.ui.PillTrackTheme

class MainActivity : ComponentActivity() {
    private val askNotif = registerForActivityResult(ActivityResultContracts.RequestPermission()) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val repo = (application as PillApp).repo
        if (Build.VERSION.SDK_INT >= 33 &&
            checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) askNotif.launch(Manifest.permission.POST_NOTIFICATIONS)
        setContent {
            val prefs by repo.prefs.collectAsState(Prefs())
            PillTrackTheme(prefs.theme) {
                Surface(Modifier.fillMaxSize(), color = PT.Bg, contentColor = PT.Ink) {
                    LaunchedEffect(Unit) { repo.sweep() }
                    AppRoot(repo)
                }
            }
        }
    }
}
