package com.charlotte04.vsccore.commands

import com.charlotte04.vsccore.VSCCore
import com.charlotte04.vsccore.util.Items.money
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
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
                   var itemName = Component.text(item.type.name)
                   if(item.itemMeta.hasDisplayName()) {
                       itemName = item.itemMeta.displayName() as TextComponent
                   }



                   player.inventory.addItem(item)
                   sender.sendMessage(itemName.append(Component.text("を取得しました！").color(GREEN)))
               }
               "set" -> {
                   var dep = VSCCore.jecon.repository.get(player.uniqueId)
                   sender.sendMessage(player.uniqueId.toString() + " " +  dep.toString())

                   VSCCore.jecon.repository.deposit(player.uniqueId,100.00)

                   dep = VSCCore.jecon.repository.get(player.uniqueId)
                   sender.sendMessage(dep.toString())
               }
            }
        }else {
            // /charl　コマンド単体の実行結果
        }
        return true
    }
}