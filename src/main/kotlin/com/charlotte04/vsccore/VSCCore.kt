package com.charlotte04.vsccore

import com.charlotte04.vsccore.Massages.consoleMes
import com.charlotte04.vsccore.commands.VSCCommand
import com.charlotte04.vsccore.listeners.PlayerEventListener
import org.bukkit.Bukkit
import org.bukkit.ChatColor.*
import org.bukkit.command.CommandExecutor
import org.bukkit.event.Listener
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin


class VSCCore: JavaPlugin(), Listener {
    private var hasBeenLoadedAlready = false
    companion object {
        lateinit var plugin: JavaPlugin
    }

    override fun onEnable() {
        try{
            monitoredInitialization()
        }catch(e: Throwable) {
            logger.warning("初期化処理に失敗しました。シャットダウンしています…")
            e.printStackTrace()
            Bukkit.shutdown()
        }
    }

    private fun  monitoredInitialization(){
        if (hasBeenLoadedAlready) {
            throw IllegalStateException("VSCCoreは2度enableされることを想定されていません！シャットダウンします…")
        }
        //　ここから初期化処理を始める
        plugin = this
        //デフォルトコンフィグを保存する
        //saveDefaultConfig()

        //

        //イベントリスナー継承
        regEvent(PlayerEventListener,this)
        //コマンドリスナー継承
        regCommand("vsc", VSCCommand)

        //　ここまで
        hasBeenLoadedAlready = true
        consoleMes("起動しました",GREEN)
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