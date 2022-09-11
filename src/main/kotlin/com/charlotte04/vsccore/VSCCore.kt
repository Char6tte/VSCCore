package com.charlotte04.vsccore

import com.charlotte04.vsccore.listeners.PlayerEventListener
import com.charlotte04.vsccore.Massages.consoleMes
import com.charlotte04.vsccore.commands.VSCCommand
import org.bukkit.ChatColor.*
import org.bukkit.command.CommandExecutor
import org.bukkit.event.Listener
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin

class VSCCore: JavaPlugin(), Listener {

    companion object {
        lateinit var plugin: JavaPlugin
    }

    override fun onEnable() {
        plugin = this
        consoleMes("Enabled",GREEN)
        regEvent(PlayerEventListener,this)
        regCommand("vsc", VSCCommand)
    }

    @Suppress("SameParameterValue")
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