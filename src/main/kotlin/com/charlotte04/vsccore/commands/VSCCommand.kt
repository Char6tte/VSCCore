package com.charlotte04.vsccore.commands

import com.charlotte04.vsccore.VSCCore.Companion.guiList
import com.charlotte04.vsccore.VSCCore.Companion.mm
import com.charlotte04.vsccore.VSCCore.Companion.plugin
import com.charlotte04.vsccore.util.ConfigItemList.setGuiItem
import com.charlotte04.vsccore.util.ConfigItemList.setGuiList
import com.charlotte04.vsccore.util.ConfigItemList.transactionGUI
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack


object VSCCommand : CommandExecutor {
    //コンフィグ継
    var config = plugin.config
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        val player:Player = sender as Player
        if (!args.isNullOrEmpty()){
            when(args[0]){
                "reload" -> {
                    sender.sendMessage(mm.deserialize("<color:red>Reload実行しました。"))
                    plugin.reloadConfig()
                    plugin.saveDefaultConfig()
                    config = plugin.config
                    config.options().copyDefaults(true)
                    plugin.saveConfig()

                    setGuiList()
                }
                "set" -> {
                    setGuiList()
                }
                "list" -> {
                    if (guiList.first.isNotEmpty()){
                        sender.sendMessage(guiList.first.toString())
                    }
                }
                "open" ->{
                    if (args.size == 2){
                        if (guiList.first.isEmpty()){
                            sender.sendMessage(mm.deserialize("<color:red>GUI情報がありません。設定するか、ロードし直してください。"))
                        }
                        if (guiList.first.contains(args[1])){
                            val idx = guiList.first.indexOf(args[1])
                            val guiName = guiList.first[idx]
                            sender.sendMessage(mm.deserialize("<color:green>[${guiName}]を開きました。"))
                            setGuiItem(idx,player)
                            guiList.second[idx].open(player)
                        }
                    }else{
                        sender.sendMessage(mm.deserialize("<color:red>引数が足らないか、多いです。例 : /charl open <GuiName>"))
                    }
                }
                "temp" ->{
                    transactionGUI(player, ItemStack(Material.STONE_AXE),100.0,"AdminShop")
                }
                else ->{
                    sender.sendMessage(mm.deserialize("<color:red>その引数は存在していません。"))
                }
            }
        }
        return true
    }
}
