package com.charlotte04.vsccore.listeners

import com.charlotte04.vsccore.VSCCore.Companion.trueOffHand
import com.charlotte04.vsccore.util.Massages.consoleMes
import io.papermc.paper.event.player.PlayerInventorySlotChangeEvent
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

object TimeEventListener : Listener {
    //Pair<PlayerのUUID, ItemのDataContainerのUUID>

    fun onTimeEvent(){
    }

    /*
    エフェクトシステム
    メニューでエフェクトをオンオフするように
    ここではエフェクトとカウントを管理したい
    カウントは1秒単位で進む

    必要な変数
    使用したプレイヤー名とエフェクト名と秒数

    必要なシステム
    ・使用したことを検知
    エフェクトが存在するかのチェック

    ・
     */

    fun effectSystem(){

    }

    /*
    プレイヤーがオフハンドにアイテムを持つと発動する
    */

    @EventHandler
    fun onChangeOffHandItem(e: PlayerInventorySlotChangeEvent){

        val oldSlot = e.slot
        val player = e.player
        val pUuid = e.player.uniqueId
        val trigger = e.shouldTriggerAdvancements()
        consoleMes("Trigger: $trigger", ChatColor.RED)
        if (trueOffHand.first.indexOf(pUuid) != -1){
            //オフハンドアイテムが有効だった場合：
            return
        }
        if (player.inventory.itemInOffHand.type == Material.AIR){
            consoleMes("オフハンドは空です", ChatColor.RED)
            return
        }
        if(e.newItemStack == player.inventory.itemInOffHand){
            consoleMes("オフハンドにあるものと持ち上げたものは同一です", ChatColor.RED)
            return
        }
        if (!player.inventory.itemInOffHand.hasItemMeta()) {
            consoleMes("オフハンドのメタは空です", ChatColor.RED)
            return
        }
        if (player.inventory.itemInOffHand.itemMeta.hasCustomModelData()) {
            consoleMes("${player.inventory.itemInOffHand}", ChatColor.RED)
            return
        }
    }

}