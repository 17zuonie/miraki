package org.akteam.miraki.listener

import net.mamoe.mirai.Bot

interface MListener {
    fun register(bot: Bot)
    val name: String
}