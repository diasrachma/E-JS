package com.example.ejs

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.ejs.admin.ArsipAdminActivity
import com.example.ejs.pegawai.BuktiRiwayatPegawaiActivity
import com.example.ejs.pegawai.FormPegawaiActivity

class NotificationHelper(private val context: Context) {

    private val notificationManager: NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    private fun createNotificationChannel(channelId: String, channelName: String, importance: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelName, importance)
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showBuktiNotification() {
        val channelId = "channel_bukti"
        val channelName = "Channel Bukti"
        val importance = NotificationManager.IMPORTANCE_HIGH

        createNotificationChannel(channelId, channelName, importance)

        val intent = Intent(context, BuktiRiwayatPegawaiActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification: Notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle("Form Evidence")
            .setContentText("Berhasil mengirim form evidence. Klik untuk melihat bukti riwayat.")
            .setSmallIcon(R.drawable.e_js_mobile)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(1, notification)
    }

    fun showRatingNotification() {
        val channelId = "channel_rating"
        val channelName = "Channel Rating"
        val importance = NotificationManager.IMPORTANCE_HIGH

        createNotificationChannel(channelId, channelName, importance)

        val intent = Intent(context, ArsipAdminActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val actionIntent = Intent(context, LoginActivity::class.java)
        val actionPendingIntent = PendingIntent.getActivity(
            context,
            0,
            actionIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification: Notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle("Rating")
            .setContentText("Berhasil memberikan Rating.")
            .setSmallIcon(R.drawable.e_js_mobile)
            .setContentIntent(pendingIntent)
            .addAction(R.drawable.action_send, "Views", actionPendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(2, notification)
    }

    fun showNpNotification(nama: String) {
        val channelId = "channel_nomor"
        val channelName = "Channel Nomor"
        val importance = NotificationManager.IMPORTANCE_HIGH

        createNotificationChannel(channelId, channelName, importance)

        val actionIntent = Intent(context, FormPegawaiActivity::class.java)
        val actionPendingIntent = PendingIntent.getActivity(
            context,
            0,
            actionIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification: Notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle(nama)
            .setContentText("Terdapat nomor perkara yang belum terkirim!")
            .setSmallIcon(R.drawable.e_js_mobile)
            .setContentIntent(actionPendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(2, notification)
    }
}
