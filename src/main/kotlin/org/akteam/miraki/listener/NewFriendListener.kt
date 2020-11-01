package org.akteam.miraki.listener

import net.mamoe.mirai.Bot
import net.mamoe.mirai.event.ListeningStatus
import net.mamoe.mirai.event.events.FriendAddEvent
import net.mamoe.mirai.event.events.NewFriendRequestEvent
import net.mamoe.mirai.event.subscribe
import net.mamoe.mirai.event.subscribeAlways
import org.akteam.miraki.BotVariables
import org.akteam.miraki.model.BotUser
import org.akteam.miraki.model.BotUsers
import org.akteam.miraki.model.UserLevel
import org.akteam.miraki.util.BotUtils
import java.time.Instant

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
                            BotUsers.add(BotUser {
                                qq = friend.id
                                level = UserLevel.NORMAL
                                subChunHuiNotice = false
                                graduateYear = year
                            })
                            bot.getGroup(BotVariables.cfg.newFriendGroup)
                                    .sendMessage("${Instant.now()}\n新朋友 :: ${friend.nick}(${friend.id})\n请手动添加备注 ${year % 100}届\n")
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