package com.charlotte04.vsccore

import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.ChatColor.*
import org.bukkit.command.CommandExecutor
import org.bukkit.event.Listener
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin

class VSCCore: JavaPlugin() {
    override fun onEnable() {
        consoleMes("Enabled",GREEN)
        regEvent(PlayerJoinListener,this)
    }


    open fun consoleMes(string: String, color: ChatColor){
        this.server.consoleSender.sendMessage("[VSC_Core]$color$string")
    }

    private fun regCommand(name: String, executor: CommandExecutor) {
        getCommand(name)?.run {
            setExecutor(executor)
            consoleMes("/$name を登録しました", AQUA)
        } ?: logger.severe("/$name を登録できませんでした")
    }

    private fun regEvent(executor: Listener, plugin : Plugin){
        server.pluginManager.registerEvents(executor, plugin)
    }
}