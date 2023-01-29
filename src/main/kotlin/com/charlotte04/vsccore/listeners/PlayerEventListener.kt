package com.charlotte04.vsccore.listeners

import com.charlotte04.vsccore.VSCCore
import com.charlotte04.vsccore.commands.VSCCommand.config
import com.charlotte04.vsccore.listeners.PlayerEventListener.Msg.local_login
import com.charlotte04.vsccore.util.Massages.consoleMes
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextReplacementConfig
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import org.bukkit.ChatColor.*
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerPreLoginEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.inventory.ItemStack
import java.util.*

@Suppress("KotlinConstantConditions")
object PlayerEventListener : Listener {

    object Msg {
        val local_login = Component.text("{name} さんがログインしました！").color(TextColor.color(NamedTextColor.GREEN))
        val first_login = Component.text("はじめまして！これからよろしくね！").color(TextColor.color(NamedTextColor.AQUA))
        val lifetime_login = Component.text("{n}日目のログインです！ログイン大ボーナスまであと{nd}日").color(TextColor.color(NamedTextColor.AQUA))
    }

    fun sys_replace(match:String,replace:String): TextReplacementConfig {
        return TextReplacementConfig.builder().matchLiteral(match).replacement(replace).build()
    }

    @EventHandler
    fun onPlayerLoginMessage(e: PlayerJoinEvent){
        val player = e.player
        val name= e.player.name

        e.joinMessage(local_login.replaceText(sys_replace("{name}",name)).color(TextColor.color(NamedTextColor.AQUA)))

        /*
        if(!player.hasPlayedBefore()){
            player.sendMessage(first_login)
        }else{
            player.sendMessage(lifetime_login.replaceText(sys_replace("{n}","")).replaceText(sys_replace("{nd}","")))
        }

         */

        player.playSound(player.location, Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f)
       // player.sendMessage(Component.text())
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

    @EventHandler
    fun playerRightClickEvent(e: PlayerInteractEvent){
        val player: Player = e.player
        val uuid : UUID = player.uniqueId
        val jecon = VSCCore.jecon.repository

        val item: ItemStack = e.item ?: return

        if (!item.hasItemMeta()) return
        if ( e.action.isRightClick) {
            //右クリック検知
            if (player.inventory.itemInMainHand.itemMeta.customModelData != 0) {
                //player.inventory.itemInMainHand.type == Material.POISONOUS_POTATO
                //カスタムモデルが設定されているアイテムを検知しました。
                val cmNumber = player.inventory.itemInMainHand.itemMeta.customModelData
                val itemType = player.inventory.itemInMainHand.type.name

                val configPath = "items.id.$itemType.$cmNumber"

                if (config.get(configPath)?.equals("") == true) {
                    consoleMes("該当のコンフィグが見つかりません",RED)
                    return
                }

                val itemName = config.getString("$configPath.name")
                val depositAmount =  config.getDouble("$configPath.Money")

                jecon.deposit(uuid,depositAmount)
                player.sendMessage("$itemName ${AQUA}を使用しました。${GREEN} +${depositAmount}チャル${AQUA} 所持金：" + jecon.format(uuid))

                when(cmNumber){
                    1 ->{
                        jecon.deposit(uuid,100.0)
                        player.sendMessage("" + AQUA + "アイテムを使用しました。" + GREEN +  " +100チャル" + AQUA + " 所持金：" + jecon.format(uuid))
                        player.inventory
                        return
                    }
                    2 ->{
                        player.sendMessage("1000")
                        return
                    }
                    else ->{
                        //ノーマルアイテム
                        return
                    }
                }
            }
        }
    }

}
