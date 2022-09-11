package com.charlotte04.vsccore.listeners

import com.charlotte04.vsccore.Massages.consoleMes
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import org.bukkit.ChatColor.GREEN
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerPreLoginEvent
import org.bukkit.event.player.PlayerJoinEvent

@Suppress("KotlinConstantConditions")
object PlayerEventListener : Listener {

    @EventHandler
    fun onPlayerLoginMessage(e: PlayerJoinEvent){
        val player = e.player
        val name= e.player.name

        e.joinMessage(Component.text("$name さんがログインしました！").color(TextColor.color(255 , 215, 0)))

        if(!player.hasPlayedBefore()){
            player.sendMessage(Component.text("082です！これからよろしくね！").color(TextColor.color(0, 255, 215)))
        }else{
            player.sendMessage(Component.text("n日目のログインです！ログイン大ボーナスまであとn日").color(TextColor.color(0, 255, 215)))
        }

        player.sendMessage(Component.text())
    }

    @EventHandler
    fun onPlayerPreLogin(e: AsyncPlayerPreLoginEvent) {
        val maxTry = 3
        val errorMessage = "ユーザー情報取得できなかったからキックしたよ"

        for (tryCount in 1 until maxTry) {
            val isLastTry = tryCount == maxTry

            try {
                val name = e.name
                val uuId = e.uniqueId
                consoleMes("name: $name, uuid: $uuId", GREEN)
                return
            } catch (ex: Exception) {
                if (isLastTry) {
                    println("Caught exception while loading PlayerData.")
                    ex.printStackTrace()
                    e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, Component.text(errorMessage))
                    return
                }
            }

        }
    }

}
