package org.akteam.miraki.commands

import org.akteam.miraki.objects.UserLevel

data class CommandProps(
    var name: String,
    var aliases: List<String>?,
    var description: String,
    var level: UserLevel = UserLevel.NORMAL,
    var permission: String? = null
)
