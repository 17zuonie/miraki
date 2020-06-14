package org.akteam.miraki.listener

import net.mamoe.mirai.Bot
import net.mamoe.mirai.contact.nameCardOrNick
import net.mamoe.mirai.event.events.MemberJoinEvent
import net.mamoe.mirai.event.events.NewFriendRequestEvent
import net.mamoe.mirai.event.subscribeAlways
import org.akteam.miraki.BotConsts
import org.akteam.miraki.BotMain
import org.akteam.miraki.model.BotUsers

object NewFriendListener : MListener {
    override fun register(bot: Bot) {
        bot.subscribeAlways<NewFriendRequestEvent> {
            val group = BotMain.bot.getGroup(BotConsts.cfg.botMainGroup)
            if (group.contains(fromId)) {
                accept()
            } else {
                BotMain.logger.info("[用户] 拒绝来自 $fromNick ($fromId) 的好友请求")
                reject()
            }
        }
        bot.subscribeAlways<MemberJoinEvent> {
            if (group.id == BotConsts.cfg.botMainGroup) {
                BotUsers.add(member)
                BotMain.logger.info("[用户] 欢迎 ${member.nameCardOrNick} (${member.id}) 加入 Aki")
            }
        }
    }

    override val name: String = "新朋友"
}