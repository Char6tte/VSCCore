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
import org.bukkit.Material.*
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockDropItemEvent
import org.bukkit.event.player.AsyncPlayerPreLoginEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.inventory.ItemStack
import java.util.*

@Suppress("KotlinConstantConditions")
object PlayerEventListener : Listener {

    object Msg {
        val local_login = Component.text("{name} さんがログインしました！").color(TextColor.color(NamedTextColor.GREEN))
        //val first_login = Component.text("はじめまして！これからよろしくね！").color(TextColor.color(NamedTextColor.AQUA))
        //val lifetime_login = Component.text("{n}日目のログインです！ログイン大ボーナスまであと{nd}日").color(TextColor.color(NamedTextColor.AQUA))
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
                    println("PlayerDataの読み込み中に例外が発生しました。再接続してください。")
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
            if (!player.inventory.itemInMainHand.itemMeta.hasCustomModelData()){
                //consoleMes("アイテムにモデルデータが設定されていません",RED)
                return
            }
            if (player.inventory.itemInMainHand.itemMeta.customModelData != 0) {
                //player.inventory.itemInMainHand.type == Material.POISONOUS_POTATO
                //カスタムモデルが設定されているアイテムを検知しました。
                val cmNumber = player.inventory.itemInMainHand.itemMeta.customModelData
                val itemType = player.inventory.itemInMainHand.type.name

                val configPath = "items.id.$itemType.$cmNumber"

                if (!config.contains("items.id.$itemType",false)) {
                    consoleMes("該当のアイテム設定が見つかりません id:$itemType",RED)
                    return
                }
                if (!config.contains(configPath,false) ) {
                    consoleMes("該当のカスタムデータが見つかりません id:$cmNumber",RED)
                    return
                }
                if (!config.contains("${configPath}.Money",false) ){
                    consoleMes("お金が設定されていません。",RED)
                }else{
                    val depositAmount =  config.getDouble("$configPath.Money")
                    val itemName = config.getString("$configPath.name")
                    jecon.deposit(uuid,depositAmount)
                    player.sendMessage("$itemName ${AQUA}を使用しました。${GREEN} +${depositAmount}チャル${AQUA} 所持金：" + jecon.get(uuid).toString() + "チャル")
                }
            }
        }
    }


    @EventHandler
    fun blakeMoney(e: BlockDropItemEvent){
        val jecon = VSCCore.jecon.repository
        //ドロップした
        val player = e.player
        val random1 = (1..10).random()
        val random2 = (1..10).random()
        val chance = config.getInt("BlakeGetMoneyChance")
        val uuid = player.uniqueId
        //consoleMes("$random1, $random2",RED)
        if (random1 % chance == 0 && random2 % chance == 0 && e.items.isNotEmpty()){
            val blockName = e.items[0].name.lowercase(Locale.getDefault())
            consoleMes("${player.name}:${blockName}Hit! $random1 , $random2", GREEN)
            val path = "WorkRewordsItems.${blockName}"
            if (config.contains(path, false)) {
                val maxVal = config.getInt(path)
                val depositAmount = (1..maxVal).random() * 100
                consoleMes("${blockName}: Config Hit!", GREEN)
                player.sendMessage("${GREEN}[妖精]${AQUA}作業おつかれさま！！ほらどーぞ！ 所持金：${GOLD}+${depositAmount}チャル")
                jecon.deposit(uuid, depositAmount.toDouble())
            }
            }
        }
}
