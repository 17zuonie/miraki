package org.akteam.miraki.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import org.akteam.miraki.BotVariables
import java.io.File

object FileUtil {
    private val client = OkHttpClient()
    suspend fun uploadToFFSup(file: File) {
        val formBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart(
                "file", file.name,
                file.asRequestBody("application/octet-stream".toMediaType())
            )
            .build()

        val request = Request.Builder()
            .url("https://upload.ffsup.com/")
            .post(formBody)
            .build()

        withContext(Dispatchers.IO) {
            client.newCall(request).execute().use {
                if (!it.isSuccessful) BotVariables.logger.error(it.toString())
                else BotVariables.logger.debug(it.body!!.string())
            }
        }
    }
}