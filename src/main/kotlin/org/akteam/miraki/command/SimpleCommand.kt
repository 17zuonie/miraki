package org.akteam.miraki.command

/**
 * 通用命令接口
 * 支持任意环境下处理命令
 *
 * @author StarWishsama
 */
interface SimpleCommand {
    /** 命令属性 */
    val props: CommandProps

    /** 命令帮助文本 必写 不敢自己都看不懂哦 */
    val help: String
}