package com.charlotte04.vsccore.commands

import com.charlotte04.vsccore.VSCCore.Companion.guiList
import com.charlotte04.vsccore.VSCCore.Companion.mm
import com.charlotte04.vsccore.util.ConfigItemList.setGuiItem
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.Sound
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.lang.Thread.sleep


object MenuCommand : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        val player = sender as Player
        val menuIndex = guiList.first.indexOf("MainMenu")
        setGuiItem(menuIndex,player)
        player.playSound(player.location, Sound.BLOCK_NOTE_BLOCK_BELL, 1f, 1.0f)
        sleep(30)
        player.playSound(player.location, Sound.BLOCK_NOTE_BLOCK_BELL, 1f, 1.5f)
        sleep(30)
        player.playSound(player.location, Sound.BLOCK_NOTE_BLOCK_BELL, 1f, 2.0f)
        sleep(30)
        guiList.second[menuIndex].open(player)
        return true
    }

    /*
    メニュー作成
    ・10*4のGUIを作成する
    ・すべてのアイテムをとれないようにする。
    ・アイテムのLureで情報を表示する。
    ・カスタムショップを作成する。
     */

    private fun loretoList(str: String,player: Player): List<Component> {
        val cnt = 0
        val strList = str.split("<newline>")
        val comList: MutableList<Component> =  mutableListOf()
        for(line in strList){
            comList.add(mm.deserialize(line))
        //plus()
        }
        return comList
    }

    fun listToList(list: List<String>): MutableList<Component> {
        val comList: MutableList<Component> =  mutableListOf()
        for(line in list){
            comList.add(mm.deserialize(line))
            //plus()
        }
        return comList
    }
    fun comToString(component: Component): String {
        return PlainTextComponentSerializer.plainText().serialize(component)
    }
}