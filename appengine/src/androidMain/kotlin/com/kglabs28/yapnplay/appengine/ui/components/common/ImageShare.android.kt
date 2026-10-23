package com.kglabs28.yapnplay.appengine.ui.components.common

import android.content.ClipData
import android.content.Intent
import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import com.kglabs28.yapnplay.appengine.core.common.CrashReporter
import java.io.File

/** Saves the picture in the app's cache and opens the share sheet with it and the message. */
@Composable
actual fun rememberImageShare(): (image: ImageBitmap, text: String) -> Unit {
    val context = LocalContext.current
    return remember(context) {
        { image, text ->
            try {
                val dir = File(context.cacheDir, SHARE_DIR).apply { mkdirs() }
                val file = File(dir, FILE_NAME)
                file.outputStream().use { image.asAndroidBitmap().compress(Bitmap.CompressFormat.PNG, QUALITY, it) }
                val uri = FileProvider.getUriForFile(context, context.packageName + AUTHORITY_SUFFIX, file)
                val send = Intent(Intent.ACTION_SEND)
                    .setType(MIME)
                    .putExtra(Intent.EXTRA_STREAM, uri)
                    .putExtra(Intent.EXTRA_TEXT, text)
                    .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                // Also as clip data: newer Android grants the picture to the chosen app (and previews it) through it.
                send.clipData = ClipData.newRawUri(null, uri)
                context.startActivity(Intent.createChooser(send, null).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            } catch (e: Exception) {
                CrashReporter.record(e, "ImageShare")
            }
        }
    }
}

private const val SHARE_DIR = "shared"
private const val FILE_NAME = "yapnplay-result.png"
private const val AUTHORITY_SUFFIX = ".fileprovider"
private const val MIME = "image/png"
private const val QUALITY = 100
