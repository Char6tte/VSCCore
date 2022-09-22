package com.charlotte04.vsccore

import com.charlotte04.vsccore.VSCCore.Companion.plugin
import org.bukkit.ChatColor

object Massages {

    fun consoleMes(string: String, color: ChatColor){
        plugin.server.consoleSender.sendMessage("[VSC_Core]$color$string")
    }


}