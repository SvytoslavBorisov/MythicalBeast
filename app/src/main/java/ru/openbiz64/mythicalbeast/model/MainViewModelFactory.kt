package ru.openbiz64.mythicalbeast.model

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.openbiz64.mythicalbeast.R
import ru.openbiz64.mythicalbeast.dataclass.BeastDataClass

class MainViewModelFactory(private val context: Context, private val fileName: String): ViewModelProvider.Factory {

    private val beastList: ArrayList<BeastDataClass>  = DataJsonLoader.getBeastData(fileName, context)
    private var mythology: MutableSet<String> = mutableSetOf()
    init {
        mythology.add(context.getString(R.string.all_category))
        var tmp: ArrayList<String> = arrayListOf()
        beastList.sortedBy {it.mythology}
        beastList.forEach {
            tmp.add(it.mythology)
        }
        tmp.sort()
        mythology.addAll(tmp)
    }
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainViewModel() as T
    }
}