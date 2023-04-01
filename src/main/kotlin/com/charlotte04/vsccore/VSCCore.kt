package com.charlotte04.vsccore

import com.charlotte04.vsccore.commands.MenuCommand
import com.charlotte04.vsccore.commands.VSCCommand
import com.charlotte04.vsccore.commands.VSCMoneyCommand
import com.charlotte04.vsccore.listeners.PlayerEventListener
import com.charlotte04.vsccore.util.CheckTime
import com.charlotte04.vsccore.util.ConfigItemList.setGuiList
import com.charlotte04.vsccore.util.Massages.consoleMes
import dev.triumphteam.gui.guis.Gui
import dev.triumphteam.gui.guis.GuiItem
import jp.jyn.jecon.Jecon
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Bukkit
import org.bukkit.ChatColor.AQUA
import org.bukkit.ChatColor.GREEN
import org.bukkit.command.CommandExecutor
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin


class VSCCore: JavaPlugin(), Listener {
    /*
    private var hikari: HikariDataSource? = null

    fun database(hikari: HikariDataSource) {
        this.hikari = hikari
    }
     */

    private var hasBeenLoadedAlready = false
    companion object {
        lateinit var plugin: JavaPlugin
        lateinit var jecon: Jecon
        lateinit var mm : MiniMessage
        //ConfigItemList
        lateinit var path:String
        lateinit var guiList:Pair<MutableList<String>,MutableList<Gui>>
        // <["AdminShop","SubMenu"],[<[1,2,3],[ItemStack,ItemStack,ItemStack]>,<[1,2,3],[ItemStack,ItemStack,ItemStack]>]>
        lateinit var itemStackList:Pair<MutableList<String>,MutableList<Pair<MutableList<Int>,MutableList<ItemStack>>>>
        // <["AdminShop","SubMenu"],[<[1,2,3],[GuiItem,GuiItem,GuiItem]>,<[1,2,3],[GuiItem,GuiItem,GuiItem]>]
        lateinit var guiItemList:Pair<MutableList<String>,MutableList<Pair<MutableList<Int>,MutableList<GuiItem>>>>
    }

    override fun onEnable() {
        try{
            monitoredInitialization()
        }catch(e: Throwable) {
            logger.warning("初期化処理に失敗しました。シャットダウンしています…")
            e.printStackTrace()
            Bukkit.shutdown()
        }


    }
    override fun onDisable() {
    }

    private fun  monitoredInitialization(){
        if (hasBeenLoadedAlready) {
            throw IllegalStateException("VSCCoreは2度enableされることを想定されていません！シャットダウンします…")
        }
        //　ここから初期化処理を始める
        plugin = this

        //コンフィグファイルが無いとき、デフォルトコンフィグを保存する
        saveDefaultConfig()
        plugin.saveDefaultConfig();
        config.options().copyDefaults(true);
        plugin.saveConfig();
        //
        mm = MiniMessage.miniMessage()

        //DayLoopManager
        CheckTime.runTaskTimerAsynchronously(this,0L,100L)

        //DBへ接続
        /*
        val hikariConfig = HikariConfig()
        hikariConfig.jdbcUrl = String.format("jdbc:mariadb://%s/%s", config.getString("Database.host"), config.getString("Database.dbname"))
        hikariConfig.username = config.getString("Database.user")
        hikariConfig.password = config.getString("Database.password")
        hikariConfig.isAutoCommit = true

        if (config.getString("Database.type") != "MariaDB"){
            throw IllegalArgumentException("Unknown jdbc")
        }
         */

        //イベントリスナー継承
        regEvent(PlayerEventListener,this)
        //コマンドリスナー継承
        regCommand("charl", VSCCommand)
        regCommand("vs_money",VSCMoneyCommand)
        regCommand("menu", MenuCommand)


        val plugin = Bukkit.getPluginManager().getPlugin("Jecon")
        if (plugin == null || !plugin.isEnabled) {
            // not available
            logger.warning("Jecon is not available.")
        }else{
            jecon = plugin as Jecon
        }
        setGuiList()

        //　ここまで
        hasBeenLoadedAlready = true
        consoleMes("起動しました",GREEN)

    }




    private fun regCommand(name: String, executor: CommandExecutor) {
        getCommand(name)?.run {
            setExecutor(executor)
            consoleMes("/$name を登録しました", AQUA)
        } ?: logger.severe("/$name を登録できませんでした")
    }

    private fun regEvent(executor: Listener, plugin : Plugin){
        server.pluginManager.registerEvents(executor, plugin)
    }

}