package org.akteam.miraki.tasks

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import me.liuwj.ktorm.dsl.eq
import me.liuwj.ktorm.entity.count
import me.liuwj.ktorm.entity.filter
import me.liuwj.ktorm.entity.forEach
import me.liuwj.ktorm.entity.sequenceOf
import net.mamoe.mirai.getFriendOrNull
import org.akteam.miraki.BotVariables
import org.akteam.miraki.api.ChunHuiApi
import org.akteam.miraki.model.BotUsers
import org.akteam.miraki.model.ChunHuiNotice

object ChunHuiNoticeUpdater : Runnable {
    var latestNotice: ChunHuiNotice? = null

    override fun run() {
        if (latestNotice != null) {
            runBlocking {
                val newNotice = ChunHuiApi.fetchNotice()
                if (newNotice != latestNotice) {
                    latestNotice = newNotice
                    val seq = BotVariables.db.sequenceOf(BotUsers).filter { it.subChunHuiNotice eq true }
                    BotVariables.logger.info("[春晖网] 获取到新通知 ${newNotice.titleWithAuthor} | 正在推送给 ${seq.count()} 位用户")
                    seq.forEach {
                        val f = BotVariables.bot.getFriendOrNull(it.qq)
                        f?.run {
                            sendMessage(
                                """
                                校园公告@春晖：
                                ${newNotice.titleWithAuthor}
                                ${newNotice.date} | ${newNotice.relativeDate}
                            """.trimIndent()
                            )

                            delay(1500L + (400..500).random())
                        }
                    }
                }
            }
        } else {
            latestNotice = runBlocking { ChunHuiApi.fetchNotice() }
        }
    }
}