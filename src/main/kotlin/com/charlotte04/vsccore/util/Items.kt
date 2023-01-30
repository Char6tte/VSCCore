package com.charlotte04.vsccore.util

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor.*
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta




object Items  {

    fun money(type: String): Any {
        // お金用アイテム
        // ItemStack(Material.POISONOUS_POTATO)

        when (type) {
            "coin" -> {
                val coin = ItemStack(Material.POISONOUS_POTATO)
                val coinMeta: ItemMeta = coin.itemMeta
                coinMeta.setCustomModelData(1)
                coinMeta.displayName(Component.text("100チャコイン").color(GOLD))
                coin.itemMeta = coinMeta
                return coin
            }
            "bill" -> {
                val bill = ItemStack(Material.POISONOUS_POTATO)
                val billMeta: ItemMeta = bill.itemMeta
                billMeta.setCustomModelData(2)
                billMeta.displayName(Component.text("1000チャ紙幣").color(YELLOW))
                bill.itemMeta = billMeta
                return bill
            }
        }

        return false
    }





}