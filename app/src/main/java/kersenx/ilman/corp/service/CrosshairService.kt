package kersenx.ilman.corp.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import androidx.core.app.NotificationCompat

class CrosshairService : Service() {

    private lateinit var windowManager: WindowManager
    private var crosshairView: View? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        startNotification()
        addCrosshair()
    }

    private fun startNotification() {
        val channelId = "kersen_crosshair_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val ch = NotificationChannel(channelId, "Crosshair Overlay", NotificationManager.IMPORTANCE_LOW)
            getSystemService(NotificationManager::class.java).createNotificationChannel(ch)
        }
        val notif: Notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Crosshair Kersen X Aktif")
            .setSmallIcon(android.R.drawable.ic_menu_compass)
            .build()
        startForeground(102, notif)
    }

    private fun addCrosshair() {
        val layoutType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_PHONE
        }

        val params = WindowManager.LayoutParams(
            120, 120, layoutType,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.CENTER
        }

        crosshairView = object : View(this) {
            private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.parseColor("#00FF66")
                strokeWidth = 4f
                style = Paint.Style.STROKE
            }
            private val dotPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.RED
                style = Paint.Style.FILL
            }

            override fun onDraw(canvas: Canvas) {
                super.onDraw(canvas)
                val cx = width / 2f
                val cy = height / 2f
                canvas.drawCircle(cx, cy, 3f, dotPaint)
                canvas.drawLine(cx - 24, cy, cx - 8, cy, paint)
                canvas.drawLine(cx + 8, cy, cx + 24, cy, paint)
                canvas.drawLine(cx, cy - 24, cx, cy - 8, paint)
                canvas.drawLine(cx, cy + 8, cx, cy + 24, paint)
            }
        }

        windowManager.addView(crosshairView, params)
    }

    override fun onDestroy() {
        super.onDestroy()
        crosshairView?.let { windowManager.removeView(it) }
    }
}
