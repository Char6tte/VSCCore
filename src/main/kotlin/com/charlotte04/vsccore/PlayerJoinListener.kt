package com.charlotte04.vsccore

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerPreLoginEvent

object PlayerJoinListener : Listener {

    @EventHandler
    fun onPlayerPreLoginEvent(event: AsyncPlayerPreLoginEvent){
        var uuid = event.uniqueId
        var name = event.name
    }

}
