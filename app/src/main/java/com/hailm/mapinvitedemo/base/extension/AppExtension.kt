package com.hailm.mapinvitedemo.base.extension

import android.content.Context
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import androidx.fragment.app.Fragment
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.Calendar
import kotlin.math.pow
import kotlin.math.sqrt

fun printLog(message: Any?) {
    if (message == null /*|| !BuildConfig.DEBUG*/) {
        return
    }
    val builder: StringBuilder = StringBuilder()
    val stackTraceElement = Thread.currentThread().stackTrace[3]
    builder.append("[DKS - LOG] ")
    builder.append("[ ${stackTraceElement.fileName} -  ${stackTraceElement.methodName}] ----")
    Log.d(builder.toString(), "#$message")
}

/**
 * Copy file from assets to internal storage
 */
fun Context.copyAssets() {
    val assetManager: AssetManager = assets
    var files: Array<String>? = null
    try {
        files = assetManager.list("images")
    } catch (e: IOException) {
        e.printStackTrace()
    }
    if (files != null) {
        for (filename in files) {
            var `in`: InputStream? = null
            var out: OutputStream? = null
            try {
                `in` = assetManager.open("images/$filename")
                out =
                    FileOutputStream(filesDir?.path + File.separator + filename)
                copyFile(`in`, out)
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                if (`in` != null) {
                    try {
                        `in`.close()
                    } catch (e: IOException) {
                    }
                }
                if (out != null) {
                    try {
                        out.flush()
                        out.close()
                    } catch (e: IOException) {
                    }
                }
            }
        }
    }
}

@Throws(IOException::class)
fun copyFile(`in`: InputStream, out: OutputStream) {
    val buffer = ByteArray(1024)
    var read: Int
    while (`in`.read(buffer).also { read = it } != -1) {
        out.write(buffer, 0, read)
    }
}

/**
 * gen date created
 *
 * @return yyyy-MM-dd HH:mm:ss
 */
fun getCreatedDate(): String {
    val calendar = Calendar.getInstance()
    return DateFormatUtils.getDateString(calendar.time, DateFormatUtils.DateFormat.DateTime_Hyphen)
}

fun getCurrentDate(): String {
    val calendar = Calendar.getInstance()
    return DateFormatUtils.getDateString(calendar.time, DateFormatUtils.DateFormat.Date_Hyphen)
}

/**
 * 未送信データ作成時の仮IDを生成
 *
 * @return 仮ID; "N" + yyyyMMddHHmmssSSS
 */
fun generateTempId(): String {
    return "N" + DateFormatUtils.getJSTDateString(
        DateFormatUtils.DateFormat.DateTimeWithMilliSec_NonSeparate
    )
}

/**
 * gen date created
 *
 * @return yyyyMMddHHmmssSSSSS
 */
fun getDateMilliSec(): String {
    return DateFormatUtils.getJSTDateString(
        DateFormatUtils.DateFormat.DateTimeWithMilliSec_NonSeparate
    )
}

fun Context.loadBitmap(fileName: String): Bitmap? {
    val file = File("${this.filesDir.path + File.separator}$fileName.png")

    return if (file.exists()) {
        BitmapFactory.decodeFile(file.absolutePath)
    } else {
        null
    }
}

fun isTablet(context: Context): Boolean {
    return try {
        val dm = context.resources.displayMetrics
        val screenWidth = dm.widthPixels / dm.xdpi
        val screenHeight = dm.heightPixels / dm.ydpi
        val size = sqrt(
            screenWidth.toDouble().pow(2.0) +
                    screenHeight.toDouble().pow(2.0)
        )
        size >= 7
    } catch (t: Throwable) {
        Log.e("Failed", t.toString())
        false
    }
}


fun Fragment.vibratePhone() {
    val vibrator = context?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    if (Build.VERSION.SDK_INT >= 26) {
        vibrator.vibrate(VibrationEffect.createOneShot(300, VibrationEffect.DEFAULT_AMPLITUDE))
    } else {
        vibrator.vibrate(300)
    }
}
