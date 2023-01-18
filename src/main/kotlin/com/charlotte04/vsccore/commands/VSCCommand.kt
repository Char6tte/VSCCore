package com.charlotte04.vsccore.commands

import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.ChatColor.*
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.scoreboard.Criteria.DUMMY
import org.bukkit.scoreboard.DisplaySlot
import org.bukkit.scoreboard.Score


object VSCCommand : CommandExecutor {

    private fun setScoreBoard(player: Player) {
        val board = Bukkit.getScoreboardManager().newScoreboard
        val obj = board.registerNewObjective("UI_Sidebar" , DUMMY, Component.text("${AQUA}=== ちゃるくら！ ==="))
        obj.displaySlot = DisplaySlot.SIDEBAR

        val onlineName: Score = obj.getScore("")
        onlineName.score = 15

        val onlineCounter = board.registerNewTeam("onlineCounter")

        onlineCounter.addEntry("$BLACK" + WHITE)

        if (Bukkit.getOnlinePlayers().isEmpty()) {
            onlineCounter.prefix(Component.text("${GRAY}Online: "+"${DARK_RED}0${RED}/" + DARK_RED + Bukkit.getMaxPlayers()))
        } else {
            onlineCounter.prefix(Component.text("${GRAY}Online: "+"$DARK_RED" + Bukkit.getOnlinePlayers().size +"${RED}/" + DARK_RED + Bukkit.getMaxPlayers()))
        }

        obj.getScore("$BLACK" + WHITE).score = 14

        player.scoreboard = board
    }
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        sender.sendMessage("VSCCore")
        val player:Player = sender as Player


        setScoreBoard(player)


        return true
    }

}
