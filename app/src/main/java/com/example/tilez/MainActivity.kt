package com.example.tilez

import android.app.Activity
import android.os.Bundle
import android.widget.TextView

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(TextView(this).apply {
            text = "Add the Wi-Fi and Bluetooth tiles from the Quick Settings edit panel."
            setPadding(48, 96, 48, 48)
        })
    }
}
