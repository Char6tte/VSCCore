package com.charlotte04.vsccore.listeners

import com.charlotte04.vsccore.Massages.consoleMes
import com.charlotte04.vsccore.listeners.PlayerEventListener.Msg.first_login
import com.charlotte04.vsccore.listeners.PlayerEventListener.Msg.lifetime_login
import com.charlotte04.vsccore.listeners.PlayerEventListener.Msg.local_login
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextReplacementConfig
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.NamedTextColor.*
import net.kyori.adventure.text.format.TextColor
import org.bukkit.ChatColor.GREEN
import org.bukkit.Sound
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerPreLoginEvent
import org.bukkit.event.player.PlayerJoinEvent

@Suppress("KotlinConstantConditions")
object PlayerEventListener : Listener {

    object Msg {
        val local_login = Component.text("{name} さんがログインしました！").color(TextColor.color(NamedTextColor.GREEN))
        val first_login = Component.text("はじめまして！これからよろしくね！").color(TextColor.color(AQUA))
        val lifetime_login = Component.text("{n}日目のログインです！ログイン大ボーナスまであと{nd}日").color(TextColor.color(AQUA))
    }

    fun sys_replace(match:String,replace:String): TextReplacementConfig {
        return TextReplacementConfig.builder().matchLiteral(match).replacement(replace).build()
    }

    @EventHandler
    fun onPlayerLoginMessage(e: PlayerJoinEvent){
        val player = e.player
        val name= e.player.name

        e.joinMessage(local_login.replaceText(sys_replace("{name}",name)).color(TextColor.color(AQUA)))


        if(!player.hasPlayedBefore()){
            player.sendMessage(first_login)
        }else{
            player.sendMessage(lifetime_login.replaceText(sys_replace("{n}","")).replaceText(sys_replace("{nd}","")))
        }

        player.playSound(player.location, Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f)
        player.sendMessage(Component.text())
    }

    @EventHandler
    fun onPlayerPreLogin(e: AsyncPlayerPreLoginEvent) {
        val maxTry = 2
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
