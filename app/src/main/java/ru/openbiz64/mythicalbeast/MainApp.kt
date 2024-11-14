package ru.openbiz64.mythicalbeast

import android.app.Application
import ru.openbiz64.mythicalbeast.db.MainDataBase



lateinit var APP_CONTEXT: MainActivity
class MainApp: Application(){
    val database by lazy { MainDataBase.getDataBase(this)}
}