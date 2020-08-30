package org.akteam.miraki.command

data class CommandProps(
        var name: String,
        var aliases: List<String>?,
        var description: String
)
