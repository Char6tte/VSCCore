package com.charlotte04.vsccore.util

import com.charlotte04.vsccore.VSCCore.Companion.plugin
import com.charlotte04.vsccore.listeners.TimeEventListener.onTimeEvent
import org.bukkit.World
import org.bukkit.scheduler.BukkitRunnable


object CheckTime : BukkitRunnable()  {
    var day = false
    private val world: World = plugin.server.worlds.first()

    override fun run() {
        day = isDay()
        onTimeEvent()
        if (day){
            //consoleMes("昼です",ChatColor.RED)
        }else{
            //consoleMes("夜です",ChatColor.RED)
        }
    }

    private fun isDay() : Boolean {
        return world.time < 12300 || world.time > 23850
    }


}