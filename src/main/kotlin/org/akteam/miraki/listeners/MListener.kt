package org.akteam.miraki.listeners

import net.mamoe.mirai.Bot

interface MListener {
    fun register(bot: Bot)
    fun getName(): String
}