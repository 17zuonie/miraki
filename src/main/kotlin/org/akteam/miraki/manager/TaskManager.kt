package org.akteam.miraki.manager

import org.akteam.miraki.BotMain
import java.util.concurrent.TimeUnit

object TaskManager {
    fun runAsync(task: () -> Unit, delay: Long) {
        BotMain.service.schedule(task, delay, TimeUnit.SECONDS)
    }

    fun runScheduleTaskAsync(task: () -> Unit, firstTimeDelay: Long, period: Long, unit: TimeUnit) {
        BotMain.service.scheduleAtFixedRate(task, firstTimeDelay, period, unit)
    }

    fun runScheduleTaskAsyncIf(task: () -> Unit, delay: Long, period: Long, unit: TimeUnit, condition: Boolean) {
        if (condition) {
            runScheduleTaskAsync(task, delay, period, unit)
        }
    }
}