package org.akteam.miraki.listener

import net.mamoe.mirai.Bot
import net.mamoe.mirai.event.ListeningStatus
import net.mamoe.mirai.event.events.FriendAddEvent
import net.mamoe.mirai.event.events.NewFriendRequestEvent
import net.mamoe.mirai.event.subscribe
import net.mamoe.mirai.event.subscribeAlways
import org.akteam.miraki.BotVariables
import org.akteam.miraki.model.BotUsers
import org.akteam.miraki.util.BotUtils

object NewFriendListener : MListener {
    private val answerPattern = Regex("^回答:(.*)\$", RegexOption.MULTILINE)
    override fun register(bot: Bot) {
        bot.subscribeAlways<NewFriendRequestEvent> {
            val result = answerPattern.find(message)
            if (result != null) {
                try {
                    val grade = BotUtils.stripGrade(result.value)
                    val year = BotUtils.gradeToYear(grade)
                    BotVariables.logger.debug("[用户] 成功读取年级: 高${grade}|${year}届")
                    bot.subscribe<FriendAddEvent> {
                        if (this.friend.id == this@subscribeAlways.fromId) {
                            BotUsers.add(friend)
                            bot.getFriend(BotVariables.cfg.rootUser).sendMessage("新朋友 :: ${year % 100}届\n${friend.nick}(${friend.id})")
                            BotVariables.logger.info("[用户] 欢迎 ${friend.nick} (${friend.id}) 加入 Aki")
                            return@subscribe ListeningStatus.STOPPED
                        }
                        return@subscribe ListeningStatus.LISTENING
                    }
                    accept()
                } catch (e: IllegalArgumentException) {
                    BotVariables.logger.debug("[用户] 无法读取年级: $message")
                    bot.getFriend(BotVariables.cfg.rootUser).sendMessage("新朋友 :: 错误\n$this\n无法读取年级")
                }
            } else {
                bot.getFriend(BotVariables.cfg.rootUser).sendMessage("新朋友 :: 错误\n$this\n无法读取答案")
            }
        }
    }

    override val name: String = "新朋友"
}