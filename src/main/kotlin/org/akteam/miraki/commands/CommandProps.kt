package org.akteam.miraki.commands

data class CommandProps(
    var name: String,
    var aliases: List<String>?,
    var description: String
//    var permission: String,
//    var level: UserLevel
)
