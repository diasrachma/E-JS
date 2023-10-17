package com.example.ejs.helper

// VideoHelper.kt
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import androidx.core.content.FileProvider
import com.example.ejs.pegawai.FormPegawaiActivity
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class VideoHelper(private val context: Context) {
    private var videoFile: File? = null
    private var videoFileUri: Uri? = null

    fun startRecordingVideo() {
        val videoIntent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        if (videoIntent.resolveActivity(context.packageManager) != null) {
            try {
                videoFile = createVideoFile()
                val videoUri = FileProvider.getUriForFile(
                    context,
                    context.packageName + ".provider",
                    videoFile!!
                )
                videoFileUri = videoUri
                videoIntent.putExtra(MediaStore.EXTRA_OUTPUT, videoUri)
                (context as FormPegawaiActivity).startActivityForResult(videoIntent, RC_CAMERA)
            } catch (e: Exception) {
                Log.e("VideoHelper", "Failed to start video recording: ${e.localizedMessage}")
            }
        }
    }

    private fun createVideoFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_MOVIES)
        val videoFileName = "VID_$timeStamp.mp4"
        return File(storageDir, videoFileName)
    }

    fun getVideoFileUri(): Uri? {
        return videoFileUri
    }

    fun encodeVideoToBase64(): String {
        val videoUri = getVideoFileUri()
        if (videoUri != null) {
            val inputStream = BufferedInputStream(context.contentResolver.openInputStream(videoUri))
            val outputStream = ByteArrayOutputStream()

            val buffer = ByteArray(1024)
            var bytesRead: Int
            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                outputStream.write(buffer, 0, bytesRead)
            }

            val videoBytes = outputStream.toByteArray()
            val base64Video = Base64.encodeToString(videoBytes, Base64.DEFAULT)

            outputStream.close()
            inputStream.close()

            return base64Video
        }
        return ""
    }

    companion object {
        const val RC_CAMERA = 300
    }
}