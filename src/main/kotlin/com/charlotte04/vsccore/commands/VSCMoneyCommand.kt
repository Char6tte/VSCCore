package com.charlotte04.vsccore.commands

import com.charlotte04.vsccore.util.Items.money
import net.kyori.adventure.text.format.NamedTextColor.*
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object VSCMoneyCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        val player: Player = sender as Player

        if (args?.size != 0){
            when(args?.get(0)){
               "item" -> {
                   val item = money(args[1]) as ItemStack
                   val itemName = item.displayName()

                   player.inventory.addItem(item)
                   sender.sendMessage("$itemName ${GREEN}を取得しました!")
               }
               "" -> {

               }
            }
        }else {
            // /charl　コマンド単体の実行結果
        }
        return true
    }
}