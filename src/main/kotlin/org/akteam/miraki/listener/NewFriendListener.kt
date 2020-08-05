package org.akteam.miraki.listener

import net.mamoe.mirai.Bot
import net.mamoe.mirai.event.events.FriendAddEvent
import net.mamoe.mirai.event.events.NewFriendRequestEvent
import net.mamoe.mirai.event.subscribeAlways
import org.akteam.miraki.BotVariables
import org.akteam.miraki.model.BotUsers

object NewFriendListener : MListener {
    override fun register(bot: Bot) {
        bot.subscribeAlways<NewFriendRequestEvent> {
            accept()
        }
        bot.subscribeAlways<FriendAddEvent> {
            BotUsers.add(friend)
            BotVariables.logger.info("[用户] 欢迎 ${friend.nick} (${friend.id}) 加入 Aki")
        }
    }

    override val name: String = "新朋友"
}