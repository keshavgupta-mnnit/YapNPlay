package com.kglabs28.yapnplay.appengine.preview

import androidx.compose.runtime.Composable
import androidx.compose.ui.ImageComposeScene
import androidx.compose.ui.unit.Density
import androidx.compose.ui.use
import com.kglabs28.yapnplay.appengine.appmanager.AppManager
import com.kglabs28.yapnplay.appengine.appmanager.JvmConfigStore
import com.kglabs28.yapnplay.appengine.appmanager.helpers.BackgroundsAppManagerHelper
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import org.jetbrains.skia.EncodedImageFormat
import org.koin.compose.KoinApplication
import org.koin.dsl.module
import java.io.File

/**
 * Draws key screens with sample data to PNG files at a landscape phone's
 * size (about 800 × 360 dp at 2.5×), so layouts can be checked by eye
 * before they reach a device. Run with `./gradlew :appengine:renderScreens`;
 * the files land in appengine/build/screens. Not a test: nothing passes or
 * fails.
 */
fun main(args: Array<String>) {
    val out = File(args.firstOrNull() ?: "build/screens").apply { mkdirs() }
    PreviewScreens.all.forEach { (name, screen) ->
        render(File(out, "$name.png")) { screen() }
        println("rendered ${out.resolve("$name.png")}")
    }
}

private const val WIDTH_DP = 800
private const val HEIGHT_DP = 360
private const val DENSITY = 2.5f
/** Frames drawn before saving, with a pause between, so images and fonts finish loading. */
private const val FRAMES = 12
private const val FRAME_MS = 150L

private fun render(file: File, content: @Composable () -> Unit) {
    val width = (WIDTH_DP * DENSITY).toInt()
    val height = (HEIGHT_DP * DENSITY).toInt()
    ImageComposeScene(width, height, Density(DENSITY)) {
        KoinApplication(application = { modules(previewModule) }) {
            YapNPlayTheme { content() }
        }
    }.use { scene ->
        var image = scene.render(0)
        repeat(FRAMES) { frame ->
            Thread.sleep(FRAME_MS)
            image = scene.render((frame + 1) * FRAME_MS * 1_000_000)
        }
        val png = image.encodeToData(EncodedImageFormat.PNG) ?: error("could not encode $file")
        file.writeBytes(png.bytes)
    }
}

/** Just enough of the app for the artwork helpers (built-in images; no network). */
private val previewModule = module {
    single { AppManager(JvmConfigStore()) }
    single { BackgroundsAppManagerHelper(get()) }
}
