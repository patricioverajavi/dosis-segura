package com.example.aplicacionmovil;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import java.util.concurrent.TimeUnit;

public class NotificacionHelper {
// no esperar 24 horas ,metodo de prueba
    public static void probarRecordatorioAhora(Context context) {
        Data datos = new Data.Builder()
                .putString(NotificacionWorker.KEY_TITULO, "📋 Dosis Segura")
                .putString(NotificacionWorker.KEY_MENSAJE, "Revisando favoritos...")
                .build();

        OneTimeWorkRequest trabajo = new OneTimeWorkRequest.Builder(
                NotificacionWorker.class)
                .setInputData(datos)
                .build();

        WorkManager.getInstance(context).enqueue(trabajo);
    }


    //notificación directa sin WorkManager para verificar que funciona:
    public static void notificarDirecto(Context context, String titulo, String mensaje) {
        // Crear canal primero
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel canal = new NotificationChannel(
                    NotificacionWorker.CHANNEL_ID,
                    "Medicamentos",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = (NotificationManager)
                    context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (manager != null) manager.createNotificationChannel(canal);
        }

        // Mostrar notificación inmediatamente
        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                context, NotificacionWorker.CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(titulo)
                .setContentText(mensaje)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManager manager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.notify((int) System.currentTimeMillis(), builder.build());
        }
    }

    // ══════════════════════════════════════
    // NOTIFICACIÓN A — Recordatorio diario
    // ══════════════════════════════════════
    public static void programarRecordatorioDiario(Context context) {
        Data datos = new Data.Builder()
                .putString(NotificacionWorker.KEY_TITULO, "📋 Dosis Segura")
                .putString(NotificacionWorker.KEY_MENSAJE,
                        "¿Revisaste tus medicamentos favoritos hoy?")
                .build();

        PeriodicWorkRequest trabajo = new PeriodicWorkRequest.Builder(
                NotificacionWorker.class,
                24, TimeUnit.HOURS)
                .setInputData(datos)
                .build();

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "recordatorio_diario",
                ExistingPeriodicWorkPolicy.KEEP,
                trabajo
        );
    }

    // ══════════════════════════════════════
    // NOTIFICACIÓN B — Al agregar medicamento
    // ══════════════════════════════════════
    public static void notificarMedicamentoAgregado(Context context, String nombreMedicamento) {
        Data datos = new Data.Builder()
                .putString(NotificacionWorker.KEY_TITULO, "💊 Medicamento agregado")
                .putString(NotificacionWorker.KEY_MENSAJE,
                        nombreMedicamento + " fue agregado al catálogo correctamente.")
                .build();

        OneTimeWorkRequest trabajo = new OneTimeWorkRequest.Builder(
                NotificacionWorker.class)
                .setInputData(datos)
                .build();

        WorkManager.getInstance(context).enqueue(trabajo);
    }

    // ══════════════════════════════════════
    // NOTIFICACIÓN C — Al guardar favorito
    // ══════════════════════════════════════
    public static void notificarFavoritoGuardado(Context context, String nombreMedicamento) {
        Data datos = new Data.Builder()
                .putString(NotificacionWorker.KEY_TITULO, "⭐ Favorito guardado")
                .putString(NotificacionWorker.KEY_MENSAJE,
                        nombreMedicamento + " guardado en favoritos. Recuerda revisar sus contraindicaciones.")
                .build();

        OneTimeWorkRequest trabajo = new OneTimeWorkRequest.Builder(
                NotificacionWorker.class)
                .setInputData(datos)
                .build();

        WorkManager.getInstance(context).enqueue(trabajo);
    }
}