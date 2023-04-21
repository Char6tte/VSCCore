package com.charlotte04.vsccore

import com.charlotte04.vsccore.commands.MenuCommand
import com.charlotte04.vsccore.commands.VSCCommand
import com.charlotte04.vsccore.commands.VSCMoneyCommand
import com.charlotte04.vsccore.listeners.PlayerEventListener
import com.charlotte04.vsccore.listeners.TimeEventListener
import com.charlotte04.vsccore.util.CheckTime
import com.charlotte04.vsccore.util.ConfigItemList.setGuiList
import com.charlotte04.vsccore.util.Massages.consoleMes
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import dev.triumphteam.gui.guis.Gui
import dev.triumphteam.gui.guis.GuiItem
import jp.jyn.jecon.Jecon
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Bukkit
import org.bukkit.ChatColor.*
import org.bukkit.command.CommandExecutor
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.util.*


class VSCCore: JavaPlugin(), Listener {

    lateinit var dataSource: HikariDataSource

    fun database(hikari: HikariDataSource) {
        this.dataSource = hikari
    }


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
        //
        lateinit var trueOffHand:Pair<MutableList<UUID>,MutableList<UUID>>
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
        dataSource.close()
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
        //1秒ごとに実行する非同期タイマー
        CheckTime.runTaskTimerAsynchronously(this,0L,20L)

        //変数初期化
        trueOffHand = Pair(mutableListOf(), mutableListOf())

        //DBへ接続
        var dbType = "sqlite"
        val dbTypes: List<String> = listOf("sqlite","mariadb","mysql")


        if(!config.contains("Database")){
            consoleMes("データベースの項目がありません。デフォルト設定のSqliteが使用されます。",RED)
        }
        if(!config.contains("Database.type")){
            consoleMes("データベースタイプが設定されていません。デフォルト設定のSqliteが使用されます。",RED)
        }else{
            if (dbTypes.indexOf(config.getString("Database.type")?.lowercase() ?: "") == -1){
                consoleMes("データベースタイプが間違っています。デフォルト設定のSqliteが使用されます。",RED)
            }else{
                dbType = config.getString("Database.type").toString().lowercase()
            }
        }
        // HikariCPの設定を行う
        when(dbType){
            "sqlite" -> {
                val hikariConfig = HikariConfig()
                val dbFile = File(dataFolder, config.getString("Database.path").toString())
                hikariConfig.jdbcUrl = "jdbc:sqlite:${dbFile.absolutePath}"
                hikariConfig.driverClassName = "org.sqlite.JDBC"
                hikariConfig.connectionTestQuery = "SELECT 1"
                hikariConfig.maximumPoolSize = 10
                hikariConfig.isAutoCommit = true

                hikariConfig.maximumPoolSize = 10 // 最大プール数
                hikariConfig.connectionTimeout = 3000 // 接続タイムアウト
                hikariConfig.idleTimeout = 60000 // アイドルタイムアウト
                hikariConfig.maxLifetime = 1800000 // 最大生存時間
                consoleMes("SqliteDBに接続しました。",GREEN)
                dataSource = HikariDataSource(hikariConfig)
            }
            "mariadb","mysql" -> {
                val hikariConfig = HikariConfig()
                hikariConfig.jdbcUrl = String.format("jdbc:mariadb://%s/%s", config.getString("Database.host"), config.getString("Database.dbname"))
                hikariConfig.username = config.getString("Database.user")
                hikariConfig.password = config.getString("Database.password")
                hikariConfig.isAutoCommit = true

                hikariConfig.maximumPoolSize = 10 // 最大プール数
                hikariConfig.connectionTimeout = 3000 // 接続タイムアウト
                hikariConfig.idleTimeout = 60000 // アイドルタイムアウト
                hikariConfig.maxLifetime = 1800000 // 最大生存時間
                consoleMes("MariaDBに接続しました。",GREEN)
                dataSource = HikariDataSource(hikariConfig)
            }
        }



        //イベントリスナー継承
        regEvent(PlayerEventListener,this)
        regEvent(TimeEventListener,this)
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

        //guiセットアップ
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