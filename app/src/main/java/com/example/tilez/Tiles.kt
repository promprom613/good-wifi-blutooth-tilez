package com.example.tilez

import android.app.PendingIntent
import android.content.Intent
import android.provider.Settings
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService

abstract class SettingsTile(private val action: String) : TileService() {

    override fun onStartListening() {
        qsTile?.let {
            it.state = Tile.STATE_INACTIVE
            it.updateTile()
        }
    }

    override fun onClick() {
        val intent = Intent(action).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        if (android.os.Build.VERSION.SDK_INT >= 34) {
            val pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
            startActivityAndCollapse(pi)
        } else {
            @Suppress("DEPRECATION")
            startActivityAndCollapse(intent)
        }
    }
}

class WifiTile : SettingsTile(Settings.ACTION_WIFI_SETTINGS)
class BluetoothTile : SettingsTile(Settings.ACTION_BLUETOOTH_SETTINGS)
