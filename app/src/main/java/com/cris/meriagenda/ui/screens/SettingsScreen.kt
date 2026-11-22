package com.cris.meriagenda.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.cris.meriagenda.notifications.NotificationHelper

@Composable
fun SettingsScreen() {

    val context =
        LocalContext.current

    fun checkPermission(): Boolean {

        return if (
            Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.TIRAMISU
        ) {

            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

        } else {

            true
        }
    }

    var hasNotificationPermission by remember {
        mutableStateOf(
            checkPermission()
        )
    }

    val notificationPermissionLauncher =
        rememberLauncherForActivityResult(
            contract =
                ActivityResultContracts.RequestPermission()
        ) { granted ->

            hasNotificationPermission =
                granted
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),

        verticalArrangement =
            Arrangement.spacedBy(12.dp)
    ) {

        Text(
            text = "Ajustes",
            style =
                MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Text(
            text =
                "Configura las preferencias de Meri Agenda.",
            style =
                MaterialTheme.typography.bodyLarge
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        Text(
            text = "Notificaciones",
            style =
                MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Surface(
            modifier =
                Modifier.fillMaxWidth(),

            shape =
                MaterialTheme.shapes.medium,

            color =
                MaterialTheme
                    .colorScheme
                    .surfaceVariant
        ) {

            Column(
                modifier =
                    Modifier.padding(16.dp),

                verticalArrangement =
                    Arrangement.spacedBy(10.dp)
            ) {

                Text(
                    text =
                        if (
                            hasNotificationPermission
                        ) {
                            "✅ Notificaciones activadas"
                        } else {
                            "🔕 Notificaciones desactivadas"
                        },

                    fontWeight =
                        FontWeight.Bold
                )

                Text(
                    text =
                        "Meri Agenda utilizará las notificaciones para recordar tareas del colegio y la universidad."
                )

                if (
                    !hasNotificationPermission
                ) {

                    Button(
                        modifier =
                            Modifier.fillMaxWidth(),

                        onClick = {

                            if (
                                Build.VERSION.SDK_INT >=
                                Build.VERSION_CODES.TIRAMISU
                            ) {

                                notificationPermissionLauncher
                                    .launch(
                                        Manifest.permission.POST_NOTIFICATIONS
                                    )

                            } else {

                                hasNotificationPermission =
                                    true
                            }
                        }
                    ) {

                        Text(
                            "Activar notificaciones"
                        )
                    }

                } else {

                    OutlinedButton(
                        modifier =
                            Modifier.fillMaxWidth(),

                        onClick = {

                            NotificationHelper
                                .showTestNotification(
                                    context
                                )
                        }
                    ) {

                        Text(
                            "Probar notificación"
                        )
                    }
                }
            }
        }
    }
}