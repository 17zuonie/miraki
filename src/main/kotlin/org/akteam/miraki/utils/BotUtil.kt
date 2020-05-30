package org.akteam.miraki.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.mamoe.mirai.message.data.MessageChain
import net.mamoe.mirai.message.data.asMessageChain
import net.mamoe.mirai.message.data.toMessage
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.akteam.miraki.BotConsts
import org.akteam.miraki.BotMain
import java.util.*

fun String.toMirai(): MessageChain {
    return toMessage().asMessageChain()
}

object BotUtil {
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

    suspend fun OkHttpClient.get(url: String): Response {
        val req = Request.Builder()
            .url(url)
            .build()
        return withContext(Dispatchers.IO) {
            newCall(req).execute()
        }
    }

    suspend fun OkHttpClient.get(req: Request): Response {
        return withContext(Dispatchers.IO) {
            newCall(req).execute()
        }
    }

    suspend fun Response.readText() = withContext(Dispatchers.IO) { body!!.string() }
}
