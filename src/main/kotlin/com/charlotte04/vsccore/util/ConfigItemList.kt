package com.charlotte04.vsccore.util

import com.charlotte04.vsccore.VSCCore.Companion.guiList
import com.charlotte04.vsccore.VSCCore.Companion.itemStackList
import com.charlotte04.vsccore.VSCCore.Companion.jecon
import com.charlotte04.vsccore.VSCCore.Companion.mm
import com.charlotte04.vsccore.VSCCore.Companion.path
import com.charlotte04.vsccore.VSCCore.Companion.plugin
import com.charlotte04.vsccore.commands.MenuCommand.comToString
import com.charlotte04.vsccore.commands.MenuCommand.listToList
import com.charlotte04.vsccore.commands.VSCCommand.config
import com.charlotte04.vsccore.util.Massages.consoleMes
import com.destroystokyo.paper.profile.PlayerProfile
import dev.triumphteam.gui.builder.item.ItemBuilder
import dev.triumphteam.gui.components.GuiType
import dev.triumphteam.gui.guis.Gui
import net.kyori.adventure.text.Component
import org.bukkit.*
import org.bukkit.block.Skull
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.persistence.PersistentDataType
import org.bukkit.profile.PlayerTextures
import java.net.URL
import java.util.*


object ConfigItemList {

    fun setGuiList() {
        if (!config.contains("GUIs")) {
            consoleMes("パネル一覧の設定がありません: GUIs not found", ChatColor.RED)
        } else {
            guiList = Pair(mutableListOf(), mutableListOf())
            itemStackList = Pair(mutableListOf(), mutableListOf())
            path = "GUIs"
            for (guiName in config.getConfigurationSection(path)?.getKeys(false)!!) {
                path = "GUIs"
                path = "$path.$guiName"
                consoleMes(path,ChatColor.RED)

                var rows: Int = 0
                var title: Component = mm.deserialize("")
                var guiType: GuiType = GuiType.CHEST
                var fallback = false

                if (config.contains("$path.rows")) {
                    rows = config.getInt("$path.rows")
                } else {
                    consoleMes("rowsが設定されていません",ChatColor.RED)
                    fallback = true
                }
                if (config.contains("$path.title")) {
                    val getTitle = config.getString("$path.title").toString()
                    title = mm.deserialize(getTitle)
                } else {
                    fallback = true
                    consoleMes("titleが設定されていません",ChatColor.RED)
                }
                if (config.contains("$path.guiType")) {
                    guiType = GuiType.valueOf(config.get("$path.guiType").toString())
                }
                if (!fallback) {
                    var gui = Gui.gui().rows(rows).title(title).type(guiType).disableAllInteractions().create()
                    consoleMes("$rows , $title , $guiType", ChatColor.RED)

                    guiList.first.add(guiName)
                    guiList.second.add(gui)
                    itemStackList.first.add(guiName)
                    //consoleMes(comToString(gui.title()),ChatColor.RED)
                    setItemList(path)
                } else {
                    consoleMes("${guiName}はスキップされました", ChatColor.RED)
                }
            }
        }
    }

    private fun setItemList(loadPath: String) {
        path = "$loadPath.Items"
        var tempItemPair: Pair<MutableList<Int>, MutableList<ItemStack>> = Pair(mutableListOf(), mutableListOf())
        val ids = config.getConfigurationSection(path)?.getKeys(false)?.toList()
        consoleMes("$ids", ChatColor.RED)

        if (ids != null) {
            repeat(ids.size) {
                path = "$loadPath.Items"
                val configItemID = ids[it]
                //=====================アイテム取得=====================//
                path = "$loadPath.Items.$configItemID"
                val id: Int = configItemID.toInt()
                consoleMes("$configItemID", ChatColor.RED)
                //コンフィグから取得したマテリアル名が存在するか。
                val material = Material.matchMaterial(config.getString("$path.ItemType").toString().uppercase()
                )
                consoleMes("マテリアル: $material", ChatColor.RED)

                if (material != null) {
                    consoleMes("マテリアルチェックok: $material", ChatColor.RED)
                    val itemStack = ItemStack(material)
                    var meta = itemStack.itemMeta

                    //====================メタデータ設定====================//

                    if (config.contains("$path.name")) {
                        meta.displayName(mm.deserialize(config.get("$path.name").toString()))
                    } else {
                        //consoleMes("nameの項目がありません！", ChatColor.RED)
                    }
                    if (config.contains("$path.lore")) {
                        val lore = listToList(config.getStringList("$path.lore"))
                        meta.lore(lore)
                    } else {
                        //consoleMes("loreの項目がありません！", ChatColor.RED)
                    }
                    if (config.contains("$path.CustomModelData")){
                        meta.setCustomModelData(config.getInt(("$path.CustomModelData")))
                    }
                    if (config.contains("$path.Enchants")) {
                        val enchantList = config.getConfigurationSection("$path.Enchants")?.getKeys(false)
                        consoleMes("$enchantList ",ChatColor.RED)
                        if (!enchantList.isNullOrEmpty()) {
                            for (getEnchantName in enchantList){
                                val enchantName = getEnchantName.lowercase()
                                consoleMes("$enchantName",ChatColor.RED)
                                val key = NamespacedKey.minecraft(enchantName)
                                val enchant = Enchantment.getByKey(key) // May be null
                                val level = config.getInt("$path.Enchants.$enchantName.Level")
                                if (enchant != null){
                                    meta.addEnchant(enchant,level,true)
                                }else{
                                    consoleMes("[${configItemID}]Enchant:${enchantName}が見つかりませんでした。 $key, $enchant, $level",ChatColor.RED)
                                }
                            }
                        }
                    }
                    if (config.contains("$path.ItemFlags")){
                        val flagList = config.getList(("$path.ItemFlags"))
                        if (flagList != null) {
                            repeat(flagList.size) {
                                if (it != -1) {
                                    try {
                                        val flagName = ItemFlag.valueOf(flagList[it].toString())
                                        meta.addItemFlags(flagName)
                                    }catch (e:IllegalArgumentException){
                                        consoleMes("[${flagList[it]}]フラグが存在しません",ChatColor.RED)
                                    }
                                }
                            }
                        }

                    }

                    //実装一時停止
                    if(itemStack.type == ItemStack(Material.PLAYER_HEAD).type){
                        if (!config.contains("$path.Head")){

                        }else {
                            if (config.contains("$path.Head.SkullOwner")){
                                config.getString("$path.Head.SkullOwner")
                            }
                            if (config.contains("$path.Head.SkinURL") && config.contains("$path.Head.ID")){
                                val skinUrl =config.getString("$path.Head.SkinURL")
                                val skinID = config.get("$path.Head.ID") as UUID
                                val profile: PlayerProfile = Bukkit.createProfile(skinID)
                                val texture: PlayerTextures = profile.textures
                                val url = URL("http://textures.minecraft.net/texture/$skinUrl")
                                texture.skin = url
                                if (!texture.isEmpty) {
                                    val skullMeta = itemStack.itemMeta as SkullMeta
                                    skullMeta.playerProfile = profile
                                    itemStack.itemMeta = skullMeta
                                }
                            }
                        }
                    }

                    //=====================アイテムへ反映=====================//

                    itemStack.itemMeta = meta

                    tempItemPair.first.add(id)
                    //consoleMes("$id", ChatColor.RED)
                    tempItemPair.second.add(itemStack)
                    //consoleMes("$itemStack", ChatColor.RED)
                    //consoleMes(itemStack.toString(), ChatColor.RED)
                    //player.inventory.setItem(player.inventory.firstEmpty(),itemStack)
                }
            }
        }
        itemStackList.second.add(tempItemPair)
    }

    /*
    GUI存在承認
    ↓
    setGUIItemにguiListのindexとplayerを引き渡す。
    ↓
    itemStackListに入っているアイテムをGUIに設置したい
    ↓
    まず、ItemsのIDとItemStackのリストを取り出したい。
    ↓
    GUIのindexを使い、IDとItemStackのPairを取り出す。
    ↓
    IDとItemStackをPairから取り出す
    ↓
    IDを使用し、configから必要情報を取り出す。
     */

    fun setGuiItem(guiIndex: Int, player: Player) {
        val guiName = guiList.first[guiIndex]
        val gui = guiList.second[guiIndex]
        val tempItemList = itemStackList.second[guiIndex]
        //consoleMes("${tempItemList.first}",ChatColor.RED)
        repeat(tempItemList.first.size) {
            val configItemID = tempItemList.first[it]
            val getItemStack = tempItemList.second[it]
            val itemStack = getItemStack
            val itemStackAmount = itemStack.amount
            path = "GUIs.$guiName.Items.$configItemID"
            //consoleMes("$itemStack",ChatColor.RED)
            val guilore = listToList(config.getStringList("$path.lore"))
            if (config.contains("$path.price", false)) {
                guilore.add(mm.deserialize("ID：$configItemID"))
                guilore.add(mm.deserialize("<color:aqua>価格：${"%,d".format(config.getInt("$path.price"))}"))
            }

            val guiItem = ItemBuilder.from(itemStack).lore(guilore as List<Component?>).amount(itemStackAmount).asGuiItem { event: InventoryClickEvent ->
                path = "GUIs.$guiName.Items.$configItemID"
                //====================クリックイベント設定====================//
                //consoleMes("Click!!",ChatColor.RED)
                //変数初期化
                var price: Double = 0.0

                if (config.contains("$path.price", false)) {
                    //consoleMes("購入処理",ChatColor.RED)
                    price = config.getDouble("$path.price")
                    if (jecon.repository.has(player.uniqueId, price)) {
                        //consoleMes("決済画面",ChatColor.RED)
                        player.playSound(player.location, Sound.BLOCK_NOTE_BLOCK_BIT, 150f, 1f)
                        Thread.sleep(10)
                        player.playSound(player.location, Sound.BLOCK_NOTE_BLOCK_BIT, 150f, 1.5f)
                        if (guiList.first.contains("transactionGUI")){
                            val getGUI = guiList.first[guiIndex]
                            transactionGUI(player,itemStack,price,getGUI)
                        }else{
                            consoleMes("決済画面が実装されていません。",ChatColor.RED)
                        }
                    } else {
                        consoleMes("所持金不足",ChatColor.RED)
                        player.sendMessage(mm.deserialize("<color:red>お金が足りません！"))
                        player.playSound(player.location, Sound.BLOCK_NOTE_BLOCK_BIT, 150f, 1f)
                        Thread.sleep(10)
                        player.playSound(player.location, Sound.BLOCK_NOTE_BLOCK_BIT, 150f, 0.5f)
                    }
                }
                if (config.contains("$path.next", false)) {
                    val index = guiList.first.indexOf(config.getString("$path.next"))
                    if (index == -1){
                        consoleMes("ページが見つかりません。",ChatColor.RED)
                    }else{
                        setGuiItem(index,player)
                        guiList.second[index].open(player)
                    }
                }
            }
            //consoleMes("$itemStack",ChatColor.RED)
            //====================GUIのスロット設定====================//
            if(config.contains("$path.fill",false)) {
                //consoleMes("fill処理",ChatColor.RED)
                if (config.contains("$path.fill.location",false)) {
                    when(config.getString("$path.fill.location")) {
                        "Top" -> {
                            gui.filler.fillTop(guiItem)
                        }
                        "Bottom" -> {
                            gui.filler.fillBottom(guiItem)
                        }
                        "Point" ->{
                            if (config.contains("$path.fill.rowfrom",false) && config.contains("$path.fill.colfrom",false) && config.contains("$path.fill.rowto",false) && config.contains("$path.fill.colto",false)){
                                val rowFrom = config.getInt("$path.fill.rowfrom")
                                val colFrom = config.getInt("$path.fill.colfrom")
                                val rowTo = config.getInt("$path.fill.rowto")
                                val colTo = config.getInt("$path.fill.colto")
                                gui.filler.fillBetweenPoints(rowFrom,colFrom,rowTo,colTo,guiItem)
                            }

                        }
                        else ->{
                            consoleMes("FillLocationが存在しません。",ChatColor.RED)
                        }
                    }
                }
            }
            if (config.contains("$path.X", false) && config.contains("$path.Y", false)) {
                //consoleMes("${config.getInt("$path.Y")},${config.getInt("$path.X")}", ChatColor.RED)
                gui.setItem(config.getInt("$path.Y"), config.getInt("$path.X"), guiItem)
            }
        }
        guiList.second[guiIndex] = gui
    }

    fun transactionGUI(player: Player, item:ItemStack,price:Double,fallbackGUIName:String){
        setGuiItem(guiList.first.indexOf("transactionGUI"),player)
        var path = "GUIs.transactionGUI"
        val gui = guiList.second[guiList.first.indexOf("transactionGUI")]

        val view = ItemBuilder.from(item).asGuiItem()

        var itemIdIndex: Int = 0
        var itemID: Int = 0
        repeat(item.itemMeta.lore()?.size ?: 0){
            val newline = comToString(item.itemMeta.lore()?.get(it) ?: mm.deserialize(""))
            if (newline.startsWith("ID：")){
                val tempItemID = newline.replace("ID：","")
                itemID = tempItemID.toInt()
                itemIdIndex =  itemStackList.second[itemStackList.first.indexOf(fallbackGUIName)].first.indexOf(tempItemID.toInt())
                //consoleMes("found ID: $tempItemID",ChatColor.RED)
                consoleMes("found itemID: $itemID",ChatColor.RED)
            }
        }
        var returnItem = itemStackList.second[itemStackList.first.indexOf(fallbackGUIName)].second[itemIdIndex]
        val meta = returnItem.itemMeta
        if (meta.hasLore()){
            val removeLore :List<Component> = listOf()
            meta.lore(removeLore)
            var uuid = UUID.randomUUID().toString()

            if (meta.persistentDataContainer.has(NamespacedKey(plugin,"mf-gui"))){
                meta.persistentDataContainer.remove(NamespacedKey(plugin,"mf-gui"))
            }
            if (config.contains("GUIs.$fallbackGUIName.Items.$itemID.DataContainer")) {
                 uuid = config.getString("GUIs.$fallbackGUIName.Items.$itemID.DataContainer").toString()
            }else{
                config.set("GUIs.$fallbackGUIName.Items.$itemID.DataContainer", uuid)
                plugin.saveConfig()
            }
            meta.persistentDataContainer.set(NamespacedKey(plugin, "item-UUID"), PersistentDataType.STRING, uuid)
            val dataContainer = meta.persistentDataContainer
            consoleMes("\n$dataContainer",ChatColor.RED)
            consoleMes("$returnItem",ChatColor.RED)
        }
        if (config.contains("GUIs.$fallbackGUIName.Items.$itemID.lore")) {
            val lore = listToList(config.getStringList("GUIs.$fallbackGUIName.Items.$itemID.lore"))
            meta.lore(lore)
        }
        returnItem.itemMeta = meta

        if (config.contains("GUIs.$fallbackGUIName.Items.$itemID.VanillaItem")){
            consoleMes("Vanillaアイテム：有効",ChatColor.RED)
            if (config.getBoolean(("GUIs.$fallbackGUIName.Items.$itemID.VanillaItem"))) {
                val configGetItemType = config.getString("GUIs.$fallbackGUIName.Items.$itemID.ItemType").toString()
                val material = Material.matchMaterial(configGetItemType)
                if (material != null) {
                    returnItem = ItemStack(material)
                }
                consoleMes("$configGetItemType",ChatColor.RED)
                consoleMes("Vanillaアイテムを配布しました",ChatColor.RED)
            }
        }else{
            consoleMes("Vanillaアイテム：無効",ChatColor.RED)
        }

        consoleMes("$returnItem",ChatColor.RED)

        if (!jecon.repository.has(player.uniqueId,price)) return //購入者の所持金が商品の金額未満の場合、キャンセル。ダブルチェックなだけなので、特に購入者に明示処理は行わない。
        gui.addSlotAction(3,2){ runBuy(player,returnItem,price,fallbackGUIName) }
        gui.addSlotAction(3,3){ runBuy(player,returnItem,price,fallbackGUIName) }

        gui.setItem(3,5, view)

        gui.addSlotAction(3,7){ runCansel(player,returnItem,fallbackGUIName) }
        gui.addSlotAction(3,8){ runCansel(player,returnItem,fallbackGUIName) }

        gui.update()

        gui.open(player)

        returnItem.amount = 1
    }

    fun runBuy(player: Player, item:ItemStack, price:Double, fallbackGUIName:String){
        if (!jecon.repository.has(player.uniqueId,price)) {
            item.amount = 1
            player.sendMessage(mm.deserialize("<color:red>お金が足りません！"))
            player.playSound(player.location, Sound.BLOCK_NOTE_BLOCK_BIT, 150f, 1f)
            Thread.sleep(10)
            player.playSound(player.location, Sound.BLOCK_NOTE_BLOCK_BIT, 150f, 0.5f)
            setGuiItem(guiList.first.indexOf(fallbackGUIName), player)
            guiList.second[guiList.first.indexOf(fallbackGUIName)].open(player)
        }else {
            if (!player.inventory.isEmpty) {
                jecon.repository.withdraw(player.uniqueId, price)
                player.inventory.addItem(item)
                player.playSound(player.location, Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 1f)
                val itemName = comToString(item.displayName())
                consoleMes("[${player.name}]${itemName}を購入しました", ChatColor.RED)
            }else{
                player.sendMessage(mm.deserialize("<color:red>インベントリが満タンです。"))
                player.playSound(player.location, Sound.BLOCK_NOTE_BLOCK_BIT, 150f, 1f)
                Thread.sleep(10)
                player.playSound(player.location, Sound.BLOCK_NOTE_BLOCK_BIT, 150f, 0.5f)
            }
            //player.sendMessage("$slot, $amount")
            //player.sendMessage("$slot, $amount, ${returnItem.amount} ")
        }
    }


    fun runCansel(player: Player, item:ItemStack, fallbackGUIName:String) {
        player.playSound(player.location, Sound.BLOCK_NOTE_BLOCK_BIT, 1f, 1f)
        Thread.sleep(50)
        player.playSound(player.location, Sound.BLOCK_NOTE_BLOCK_BIT, 1f, 0.5f)
        setGuiItem(guiList.first.indexOf(fallbackGUIName),player)
        guiList.second[guiList.first.indexOf(fallbackGUIName)].open(player)
    }

}

