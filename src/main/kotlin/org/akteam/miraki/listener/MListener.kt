package org.akteam.miraki.listener

import net.mamoe.mirai.Bot

interface MListener {
    fun register(bot: Bot)
    fun getName(): String
}