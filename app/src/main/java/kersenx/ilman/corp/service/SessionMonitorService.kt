package kersenx.ilman.corp.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Debug
import android.os.IBinder
import android.view.Gravity
import android.view.WindowManager
import android.widget.TextView
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.*

class SessionMonitorService : Service() {

    private lateinit var windowManager: WindowManager
    private var monitorView: TextView? = null
    private val scope = CoroutineScope(Dispatchers.Main)
    private var isRunning = true

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        startNotification()
        addMonitorView()
        startUpdater()
    }

    private fun startNotification() {
        val channelId = "kersen_monitor_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val ch = NotificationChannel(channelId, "Monitor HUD", NotificationManager.IMPORTANCE_LOW)
            getSystemService(NotificationManager::class.java).createNotificationChannel(ch)
        }
        val notif: Notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Monitor Sesi Kersen X Aktif")
            .setSmallIcon(android.R.drawable.ic_menu_info_details)
            .build()
        startForeground(103, notif)
    }

    private fun addMonitorView() {
        val layoutType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_PHONE
        }

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            layoutType,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.END
            x = 24
            y = 24
        }

        val tv = TextView(this).apply {
            text = "Kersen X | FPS: -- | RAM: -- MB"
            setTextColor(Color.parseColor("#00E5FF"))
            textSize = 11f
            setPadding(16, 8, 16, 8)
            background = GradientDrawable().apply {
                setColor(Color.parseColor("#AA0D1117"))
                cornerRadius = 16f
                setStroke(1, Color.parseColor("#00E5FF"))
            }
        }
        monitorView = tv
        windowManager.addView(monitorView, params)
    }

    private fun startUpdater() {
        scope.launch {
            while (isRunning) {
                val memInfo = Debug.MemoryInfo()
                Debug.getMemoryInfo(memInfo)
                val usedRamMb = memInfo.totalPss / 1024
                monitorView?.text = "Kersen X | FPS: 60 | RAM: ${usedRamMb}MB"
                delay(1000)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        isRunning = false
        scope.cancel()
        monitorView?.let { windowManager.removeView(it) }
    }
}
