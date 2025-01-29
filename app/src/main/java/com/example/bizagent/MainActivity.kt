package com.example.bizagent

import WebClient
import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.telephony.TelephonyCallback
import android.telephony.TelephonyManager


class MainActivity : AppCompatActivity() {
    private lateinit var tvPhoneNumber: TextView
    private var phoneNumber: String = ""
    private lateinit var telephonyManager: TelephonyManager
    private lateinit var telephonyCallback: TelephonyCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dialpad)

        tvPhoneNumber = findViewById(R.id.tvPhoneNumber)
        val btnCall: Button = findViewById(R.id.btnCall)

        val webclient : WebClient = WebClient()
        telephonyCallback = TelephonyCallbackImpl(this, webclient)
        telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.READ_PHONE_STATE), 1)
        }else{
            telephonyManager.registerTelephonyCallback(mainExecutor, telephonyCallback)
        }
        btnCall.setOnClickListener {
            makeCall()
        }
    }

    fun onNumberClick(view: android.view.View) {
        val button = view as Button
        phoneNumber += button.text
        tvPhoneNumber.text = phoneNumber

    }

    private fun makeCall() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CALL_PHONE), 1)
        } else {
            val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$phoneNumber"))
            startActivity(intent)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            makeCall()
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        // Unregister the TelephonyCallback when the activity is destroyed
        telephonyManager.unregisterTelephonyCallback(telephonyCallback)
    }



}
