package com.cris.meriagenda.ui.screens

import android.Manifest
import android.app.AlarmManager
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
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

    /*
     * PERMISO DE NOTIFICACIONES
     */
    fun checkNotificationPermission(): Boolean {

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
            checkNotificationPermission()
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

    /*
     * PERMISO DE ALARMAS EXACTAS
     */
    fun checkExactAlarmPermission(): Boolean {

        if (
            Build.VERSION.SDK_INT <
            Build.VERSION_CODES.S
        ) {
            return true
        }

        val alarmManager =
            context.getSystemService(
                AlarmManager::class.java
            )

        return alarmManager
            .canScheduleExactAlarms()
    }

    var hasExactAlarmPermission by remember {
        mutableStateOf(
            checkExactAlarmPermission()
        )
    }

    val exactAlarmLauncher =
        rememberLauncherForActivityResult(
            contract =
                ActivityResultContracts
                    .StartActivityForResult()
        ) {

            hasExactAlarmPermission =
                checkExactAlarmPermission()
        }

    /*
     * INTERFAZ
     */
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
            fontWeight =
                FontWeight.Bold
        )

        Text(
            text =
                "Configura las preferencias de Meri Agenda.",
            style =
                MaterialTheme.typography.bodyLarge
        )

        Spacer(
            modifier =
                Modifier.height(12.dp)
        )

        /*
         * SECCIÓN NOTIFICACIONES
         */
        Text(
            text = "Notificaciones",
            style =
                MaterialTheme.typography.titleLarge,
            fontWeight =
                FontWeight.Bold
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

        /*
         * AQUÍ VA LA NUEVA SECCIÓN
         * DE ALARMAS EXACTAS
         */
        Spacer(
            modifier =
                Modifier.height(12.dp)
        )

        Text(
            text =
                "Alarmas y recordatorios",
            style =
                MaterialTheme.typography.titleLarge,
            fontWeight =
                FontWeight.Bold
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
                            hasExactAlarmPermission
                        ) {

                            "✅ Recordatorios exactos activados"

                        } else {

                            "⏰ Falta permitir alarmas exactas"
                        },

                    fontWeight =
                        FontWeight.Bold
                )

                Text(
                    text =
                        "Permite que Meri Agenda avise a la hora exacta de cada tarea."
                )

                if (
                    !hasExactAlarmPermission
                ) {

                    Button(
                        modifier =
                            Modifier.fillMaxWidth(),

                        onClick = {

                            if (
                                Build.VERSION.SDK_INT >=
                                Build.VERSION_CODES.S
                            ) {

                                val intent =
                                    Intent(
                                        Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
                                    ).apply {

                                        data =
                                            Uri.parse(
                                                "package:${context.packageName}"
                                            )
                                    }

                                exactAlarmLauncher
                                    .launch(intent)
                            }
                        }
                    ) {

                        Text(
                            "Permitir alarmas y recordatorios"
                        )
                    }
                }
            }
        }

        Spacer(
            modifier =
                Modifier.height(30.dp)
        )
    }
}