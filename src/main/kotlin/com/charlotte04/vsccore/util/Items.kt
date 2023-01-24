package com.charlotte04.vsccore.util

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor.*
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

object Items  {

    fun money(type: String): Any {
        // お金用アイテム
        // ItemStack(Material.POISONOUS_POTATO)

        when (type) {
            "coin" -> {
                val coin = ItemStack(Material.POISONOUS_POTATO)
                coin.itemMeta.setCustomModelData(1)
                coin.itemMeta.displayName(Component.text("${GOLD}100チャコイン"))
                return coin
            }
            "bill" -> {
                val bill = ItemStack(Material.POISONOUS_POTATO)
                bill.itemMeta.setCustomModelData(2)
                bill.itemMeta.displayName(Component.text("${YELLOW}1000チャ紙幣"))
                return bill
            }
        }

        return false
    }





}