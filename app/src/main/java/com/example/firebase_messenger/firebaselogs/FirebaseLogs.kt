package com.example.firebase_messenger.firebaselogs

import android.app.ActivityManager
import android.os.Build
import android.os.Debug
import android.os.Debug.MemoryInfo
import android.os.Environment
import com.example.firebase_messenger.MainActivity
import com.example.firebase_messenger.User
import com.google.firebase.database.FirebaseDatabase


class Ram(
    val headSize: String? = null,
    val usedMemoryInBytes: String? = null,
    val usedMemoryInPercentage: String? = null,
)

class LogInfo(
    val messageLength: Int? = null,
    val userName: String? = null,
    val ram: Ram? = null,
    val osVersion: String? = null,
) {
    override fun toString(): String {
        return "" +
            "RAM: HeadSize=${this.ram?.headSize} " +
            "UsedMemoryInBytes=${this.ram?.usedMemoryInBytes} " +
            "UsedMemoryInPercentage=${this.ram?.usedMemoryInPercentage} " +
            "MessageLength=${this.messageLength} " +
            "UserName=${this.userName} " +
            "OS Version=${osVersion}"

    }
}

class FirebaseLogs {

    public fun sendLog(database: FirebaseDatabase, message: String, userName: String): LogInfo {
        val myRef = database.getReference("logs")

        val log = LogInfo(
            messageLength = message.length,
            userName,
            ram = getMemory(),
            osVersion = getVersionOC()
        )
        myRef.child(myRef.push().key ?: "default").setValue(log)

        return log
    }

    private fun getMemory(): Ram {
        val nativeHeapSize = Debug.getNativeHeapSize()
        val nativeHeapFreeSize = Debug.getNativeHeapFreeSize()
        val usedMemInBytes = nativeHeapSize - nativeHeapFreeSize
        val usedMemInPercentage = usedMemInBytes * 100 / nativeHeapSize

        return Ram(
            headSize = nativeHeapSize.toString(),
            usedMemoryInBytes = usedMemInBytes.toString(),
            usedMemoryInPercentage = usedMemInPercentage.toString()
        )
    }

    private fun getVersionOC(): String {
        return Build.VERSION.SDK_INT.toString()
    }

}