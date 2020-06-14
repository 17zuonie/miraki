package org.akteam.miraki.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import net.mamoe.mirai.message.data.MessageChain
import net.mamoe.mirai.message.data.asMessageChain
import net.mamoe.mirai.message.data.toMessage
import okhttp3.*
import org.akteam.miraki.BotConsts
import org.akteam.miraki.BotMain
import java.io.IOException
import java.util.*
import kotlin.coroutines.resumeWithException

fun String.toMirai(): MessageChain {
    return toMessage().asMessageChain()
}

object BotUtils {
    private var coolDown: MutableMap<Long, Long> = HashMap()

    /**
     * 判断指定QQ号是否仍在冷却中
     *
     * @author NamelessSAMA
     * @param qq 指定的QQ号
     * @return 目标QQ号是否处于冷却状态
     */
    fun isNoCoolDown(qq: Long): Boolean {
        val currentTime = System.currentTimeMillis()
        if (qq == 80000000L) {
            return false
        }

        if (qq == BotConsts.cfg.rootUser) {
            return true
        }

        if (coolDown.containsKey(qq) /*&& !isBotAdmin(qq)*/) {
            val cd = coolDown[qq]
            if (cd != null) {
                if (currentTime - cd < BotConsts.cfg.coolDownTime * 1000) {
                    return false
                } else {
                    coolDown.remove(qq)
                }
            }
        } else {
            coolDown[qq] = currentTime
        }
        return true
    }

    /**
     * 判断指定QQ号是否仍在冷却中
     * (可以自定义命令冷却时间)
     *
     * @author Nameless
     * @param qq 要检测的QQ号
     * @param seconds 自定义冷却时间
     * @return 目标QQ号是否处于冷却状态
     */
    fun isNoCoolDown(qq: Long, seconds: Int): Boolean {
        val currentTime = System.currentTimeMillis()
        if (qq == 80000000L) {
            return false
        }

        if (qq == BotConsts.cfg.rootUser) {
            return true
        }

        if (coolDown.containsKey(qq) /*&& !isBotOwner(qq)*/) {
            if (currentTime - coolDown[qq]!! < seconds * 1000) {
                return false
            } else {
                coolDown.remove(qq)
            }
        } else {
            coolDown[qq] = currentTime
        }
        return true
    }

    fun getRunningTime(): String {
        val remain = System.currentTimeMillis() - BotMain.startTime
        var second = remain / 1000
        val ms = remain - second * 1000
        var minute = 0L
        var hour = 0L
        var day = 0L

        while (second >= 60) {
            minute += second / 60
            second -= minute * 60
        }

        while (minute >= 60) {
            hour += minute / 60
            minute -= hour * 60
        }

        while (hour >= 24) {
            day += hour / 24
            hour -= day * 24
        }

        return day.toString() + "天" + hour + "时" + minute + "分" + second + "秒" + ms + "毫秒"
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
        return newCall(req).await()
    }

    suspend fun OkHttpClient.get(req: Request): Response {
        return newCall(req).await()
    }

    suspend fun Response.readText() = withContext(Dispatchers.IO) { body!!.string() }
}
