package com.example.aplicacionmovil;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import java.util.List;

public class NotificacionWorker extends Worker {

    public static final String CHANNEL_ID = "canal_medicamentos";
    public static final String KEY_TITULO = "titulo";
    public static final String KEY_MENSAJE = "mensaje";

    public NotificacionWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        // Lee datos REALES de Room
        AppDatabase db = AppDatabase.getDatabase(getApplicationContext());
        List<Medicamento> favoritos = db.medicamentoDao().getFavoritosDirecto();

        String titulo  = "📋 Dosis Segura";
        String mensaje;

        if (favoritos == null || favoritos.isEmpty()) {
            mensaje = "No tienes medicamentos favoritos aún. ¡Agrega uno!";
        } else if (favoritos.size() == 1) {
            mensaje = "Tienes 1 medicamento favorito: " + favoritos.get(0).nombre;
        } else {
            mensaje = "Tienes " + favoritos.size() + " medicamentos favoritos. ¿Los revisaste hoy?";
        }

        crearCanalNotificacion();
        mostrarNotificacion(titulo, mensaje);

        return Result.success();
    }

    private void crearCanalNotificacion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel canal = new NotificationChannel(
                    CHANNEL_ID,
                    "Medicamentos",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            canal.setDescription("Notificaciones del catálogo de medicamentos");
            NotificationManager manager = getApplicationContext()
                    .getSystemService(NotificationManager.class);
            if (manager != null) manager.createNotificationChannel(canal);
        }
    }

    private void mostrarNotificacion(String titulo, String mensaje) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(titulo)
                .setContentText(mensaje)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManager manager = (NotificationManager)
                getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        if (manager != null) {
            manager.notify((int) System.currentTimeMillis(), builder.build());
        }
    }
}