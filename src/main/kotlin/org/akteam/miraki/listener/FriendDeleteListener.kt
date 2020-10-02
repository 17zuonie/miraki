package org.akteam.miraki.listener

import net.mamoe.mirai.Bot
import net.mamoe.mirai.event.events.FriendDeleteEvent
import net.mamoe.mirai.event.subscribeAlways
import org.akteam.miraki.BotVariables
import org.akteam.miraki.model.BotUsers

object FriendDeleteListener : MListener {
    override fun register(bot: Bot) {
        bot.subscribeAlways<FriendDeleteEvent> {
            BotUsers.delete(friend.id)
            BotVariables.logger.info("[用户] ${friend.nick}(${friend.id}) 已不再是好友")
        }
    }

    override val name: String = "好友删除"
}