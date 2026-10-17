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
import android.os.IBinder
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.NotificationCompat
import kersenx.ilman.corp.tweaks.MemoryLoosener
import kersenx.ilman.corp.tweaks.PerformanceManager
import kersenx.ilman.corp.tweaks.PotatoGraphicsManager
import kersenx.ilman.corp.tweaks.ResolutionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AxCornerService : Service() {

    private lateinit var windowManager: WindowManager
    private var cornerView: View? = null
    private var menuView: View? = null
    private val scope = CoroutineScope(Dispatchers.Main)

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        startForegroundNotification()
        createFloatingCornerHandle()
    }

    private fun startForegroundNotification() {
        val channelId = "kersen_corner_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Kersen X Corner Overlay",
                NotificationManager.IMPORTANCE_LOW
            )
            val nm = getSystemService(NotificationManager::class.java)
            nm.createNotificationChannel(channel)
        }

        val notification: Notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Kersen X Corner Aktif")
            .setContentText("Panel melayang in-game sedang berjalan")
            .setSmallIcon(android.R.drawable.ic_menu_compass)
            .build()

        startForeground(101, notification)
    }

    private fun createFloatingCornerHandle() {
        val layoutType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_PHONE
        }

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            layoutType,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 0
            y = 300
        }

        val handle = TextView(this).apply {
            text = "⚡"
            textSize = 18f
            setTextColor(Color.WHITE)
            setPadding(24, 20, 24, 20)
            val bg = GradientDrawable().apply {
                setColor(Color.parseColor("#E50914"))
                cornerRadius = 32f
            }
            background = bg
        }

        handle.setOnTouchListener(object : View.OnTouchListener {
            private var initX = 0
            private var initY = 0
            private var touchX = 0f
            private var touchY = 0f
            private var isClick = true

            override fun onTouch(v: View?, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        initX = params.x
                        initY = params.y
                        touchX = event.rawX
                        touchY = event.rawY
                        isClick = true
                        return true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        val dx = (event.rawX - touchX).toInt()
                        val dy = (event.rawY - touchY).toInt()
                        if (Math.abs(dx) > 10 || Math.abs(dy) > 10) isClick = false
                        params.x = initX + dx
                        params.y = initY + dy
                        windowManager.updateViewLayout(handle, params)
                        return true
                    }
                    MotionEvent.ACTION_UP -> {
                        if (isClick) {
                            toggleCornerMenu()
                        }
                        return true
                    }
                }
                return false
            }
        })

        cornerView = handle
        windowManager.addView(cornerView, params)
    }

    private fun toggleCornerMenu() {
        if (menuView != null) {
            windowManager.removeView(menuView)
            menuView = null
            return
        }

        val layoutType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_PHONE
        }

        val params = WindowManager.LayoutParams(
            600,
            WindowManager.LayoutParams.WRAP_CONTENT,
            layoutType,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.CENTER
        }

        val menuLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 32, 32, 32)
            background = GradientDrawable().apply {
                setColor(Color.parseColor("#CC12171E"))
                cornerRadius = 24f
                setStroke(2, Color.parseColor("#00E5FF"))
            }
        }

        val title = TextView(this).apply {
            text = "⚡ KERSEN X CORNER"
            textSize = 16f
            setTextColor(Color.parseColor("#00E5FF"))
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, 20)
        }
        menuLayout.addView(title)

        fun createBtn(label: String, colorHex: String, onClick: () -> Unit): Button {
            return Button(this).apply {
                text = label
                setTextColor(Color.WHITE)
                background = GradientDrawable().apply {
                    setColor(Color.parseColor(colorHex))
                    cornerRadius = 16f
                }
                setOnClickListener { onClick() }
            }
        }

        // 1. Quick Boost
        menuLayout.addView(createBtn("⚡ Quick Boost (RAM)", "#1F2937") {
            scope.launch {
                MemoryLoosener.quickBoost()
                Toast.makeText(this@AxCornerService, "RAM Dioptimalkan!", Toast.LENGTH_SHORT).show()
            }
        })

        // 2. Toggle Mode X
        menuLayout.addView(createBtn("🔥 Mode X (ROG Performance)", "#DC2626") {
            scope.launch {
                if (PerformanceManager.isModeXActive) {
                    PerformanceManager.disableModeX()
                    Toast.makeText(this@AxCornerService, "Mode X Dinonaktifkan", Toast.LENGTH_SHORT).show()
                } else {
                    PerformanceManager.enableModeX()
                    Toast.makeText(this@AxCornerService, "Mode X Aktif (4x MSAA & GPU Boost)", Toast.LENGTH_SHORT).show()
                }
            }
        })

        // 3. Toggle Grafik MC
        menuLayout.addView(createBtn("🎮 Toggle Grafik MC (Potato)", "#059669") {
            scope.launch {
                if (PotatoGraphicsManager.isPotatoActive) {
                    PotatoGraphicsManager.disablePotatoGraphics()
                    Toast.makeText(this@AxCornerService, "Grafik Normal", Toast.LENGTH_SHORT).show()
                } else {
                    PotatoGraphicsManager.enablePotatoGraphics()
                    Toast.makeText(this@AxCornerService, "Grafik MC Aktif (Ultra Smooth)!", Toast.LENGTH_SHORT).show()
                }
            }
        })

        // 4. Reset & Close
        menuLayout.addView(createBtn("❌ Reset Reso & Tutup Menu", "#4B5563") {
            scope.launch {
                ResolutionManager.reset()
                PerformanceManager.disableModeX()
                Toast.makeText(this@AxCornerService, "Tampilan Direset", Toast.LENGTH_SHORT).show()
                toggleCornerMenu()
            }
        })

        menuView = menuLayout
        windowManager.addView(menuView, params)
    }

    override fun onDestroy() {
        super.onDestroy()
        cornerView?.let { windowManager.removeView(it) }
        menuView?.let { windowManager.removeView(it) }
    }
}
