package com.charlotte04.vsccore.listeners

import com.charlotte04.vsccore.util.CheckTime.day
import java.util.*

object TimeEventListener {
    //Pair<PlayerのUUID, ItemのDataContainerのUUID>
    public var changeList:MutableList<Pair<UUID,UUID>> = mutableListOf()


    /*
    プレイヤーがオフハンドにアイテムを持つ
    シフト右クリックをすると発動をトグルできる

    ・発動


     */

    fun onTimeEvent(){
        if(day){

        }else{

        }
    }
}