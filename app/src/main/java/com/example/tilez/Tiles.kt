package com.example.tilez

import android.app.PendingIntent
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.WifiManager
import android.os.Build
import android.provider.Settings
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService

abstract class SettingsTile(
    private val action: String,
    private val stateAction: String
) : TileService() {

    abstract fun isOn(): Boolean

    private var registered = false

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            refresh()
        }
    }

    override fun onStartListening() {
        refresh()
        val filter = IntentFilter(stateAction)
        if (Build.VERSION.SDK_INT >= 33) {
            registerReceiver(receiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            registerReceiver(receiver, filter)
        }
        registered = true
    }

    override fun onStopListening() {
        if (registered) {
            try {
                unregisterReceiver(receiver)
            } catch (e: Exception) {
            }
            registered = false
        }
    }

    private fun refresh() {
        val tile = qsTile ?: return
        val on = try { isOn() } catch (e: Exception) { false }
        tile.state = if (on) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
        if (Build.VERSION.SDK_INT >= 29) {
            tile.subtitle = if (on) "On" else "Off"
        }
        tile.updateTile()
    }

    override fun onClick() {
        val intent = Intent(action).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        if (Build.VERSION.SDK_INT >= 34) {
            val pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
            startActivityAndCollapse(pi)
        } else {
            @Suppress("DEPRECATION")
            startActivityAndCollapse(intent)
        }
    }
}

class WifiTile : SettingsTile(
    Settings.ACTION_WIFI_SETTINGS,
    WifiManager.WIFI_STATE_CHANGED_ACTION
) {
    override fun isOn(): Boolean {
        val wm = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        return wm.isWifiEnabled
    }
}

class BluetoothTile : SettingsTile(
    Settings.ACTION_BLUETOOTH_SETTINGS,
    BluetoothAdapter.ACTION_STATE_CHANGED
) {
    override fun isOn(): Boolean = try {
        val bm = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bm.adapter?.isEnabled == true
    } catch (e: SecurityException) {
        Settings.Global.getInt(contentResolver, "bluetooth_on", 0) == 1
    }
}
