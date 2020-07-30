package org.akteam.miraki.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import net.mamoe.mirai.message.data.LightApp
import net.mamoe.mirai.message.data.MessageChain
import net.mamoe.mirai.message.data.asMessageChain
import net.mamoe.mirai.message.data.toMessage
import okhttp3.*
import org.akteam.miraki.BotMain
import java.io.IOException
import java.time.Duration
import java.time.LocalDateTime
import kotlin.coroutines.resumeWithException

fun String.toMirai(): MessageChain {
    return toMessage().asMessageChain()
}

object BotUtils {
    fun getRunningTime(): String {
        val remain = Duration.between(BotMain.startTime, LocalDateTime.now())
        return "${remain.toDaysPart()}天${remain.toHoursPart()}时${remain.toMinutesPart()}分${remain.toSecondsPart()}秒${remain.toMillisPart()}毫秒"
    }

    fun List<String>.getRestString(startAt: Int): String {
        val sb = StringBuilder()
        if (this.size == 1) {
            return this[0]
        }

        for (index in startAt until this.size) {
            sb.append(this[index]).append(" ")
        }
        return sb.toString().trim()
    }

    fun sendLinkCard(
        title: String,
        desc: String,
        jumpUrl: String,
        prompt: String = "[分享]一条链接",
        preview: String = BotMain.bot.selfQQ.avatarUrl,
        tag: String = "Aki"
    ) = LightApp(
        """
            {
                "app": "com.tencent.structmsg",
                "desc": "新闻",
                "view": "news",
                "ver": "0.0.0.1",
                "prompt": "$prompt",
                "meta": {
                    "news": {
                        "action": "",
                        "android_pkg_name": "",
                        "app_type": 1,
                        "appid": 1103188687,
                        "desc": "$desc",
                        "jumpUrl": "$jumpUrl",
                        "preview": "$preview",
                        "source_icon": "",
                        "source_url": "",
                        "tag": "$tag",
                        "title": "$title"
                    }
                }
            }
        """.trimIndent()
    )

    @ExperimentalCoroutinesApi
    suspend fun Call.await(recordStack: Boolean = false): Response {
        val callStack = if (recordStack) {
            IOException().apply {
                // Remove unnecessary lines from stacktrace
                // This doesn't remove await$default, but better than nothing
                stackTrace = stackTrace.copyOfRange(1, stackTrace.size)
            }
        } else {
            null
        }
        return suspendCancellableCoroutine { continuation ->
            enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    continuation.resume(response) {
                        response.close()
                    }
                }

                override fun onFailure(call: Call, e: IOException) {
                    // Don't bother with resuming the continuation if it is already cancelled.
                    if (continuation.isCancelled) return
                    callStack?.initCause(e)
                    continuation.resumeWithException(callStack ?: e)
                }
            })

            continuation.invokeOnCancellation {
                try {
                    cancel()
                } catch (ex: Throwable) {
                    //Ignore cancel exception
                }
            }
        }
    }

    suspend fun OkHttpClient.get(url: String): Response {
        val req = Request.Builder()
            .url(url)
            .build()
        return withContext(Dispatchers.IO) { newCall(req).await() }
    }

    suspend fun OkHttpClient.get(req: Request): Response {
        return withContext(Dispatchers.IO) { newCall(req).await() }
    }

    suspend fun Response.readText() = withContext(Dispatchers.IO) { body!!.string() }
}
