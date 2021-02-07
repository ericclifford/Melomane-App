package main.app.logging

import android.util.Log
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*
import timber.log.Timber


class TimberRemoteTree(private val deviceDetails: DeviceDetails) :  Timber.DebugTree() {
    private val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS a zzz", Locale.getDefault())
    private val date = dateFormat.format(Date(System.currentTimeMillis()))

    private val logRef = Firebase.database.getReference("logs/$date/${deviceDetails.deviceId}")

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        val timestamp = System.currentTimeMillis()
        val time = timeFormat.format(Date(timestamp))
        val remoteLog = LogData(priorityAsString(priority), tag, message, t.toString(), time)

        with(logRef) {
            updateChildren(mapOf(Pair("-DeviceDetails", deviceDetails)))
            child(timestamp.toString()).setValue(remoteLog)
        }
    }

    private fun priorityAsString(priority: Int): String = when (priority) {
        Log.VERBOSE -> "VERBOSE"
        Log.DEBUG -> "DEBUG"
        Log.INFO -> "INFO"
        Log.WARN -> "WARN"
        Log.ERROR -> "ERROR"
        Log.ASSERT -> "ASSERT"
        else -> priority.toString()
    }
}