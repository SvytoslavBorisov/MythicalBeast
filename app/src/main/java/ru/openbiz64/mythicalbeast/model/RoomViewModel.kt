package ru.openbiz64.mythicalbeast.model

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import ru.openbiz64.mythicalbeast.dataclass.BeastDataClass
import ru.openbiz64.mythicalbeast.dataclass.BeastFilter
import ru.openbiz64.mythicalbeast.db.MainDataBase


class RoomViewModel(database: MainDataBase): ViewModel() {

    private val dao = database.getDao()

    //==============================================
    val allMinerals: LiveData<List<BeastDataClass>> = dao.getAllBeasts().asLiveData()



    val allFavoriteMinerals: LiveData<List<BeastDataClass>> = dao.getFavoriteBeasts().asLiveData()

    fun insertBeast(noteItem: BeastDataClass) = viewModelScope.launch {
        dao.insertBeast(noteItem)
    }

    fun deleteBeast(id: Int) = viewModelScope.launch {
        dao.deleteBeast(id)
    }

    fun updateBeast(item: BeastDataClass) = viewModelScope.launch {
        dao.updateBeast(item)
    }

    //==============================================
    val searchBeasts = MutableLiveData<List<BeastDataClass>>()

    fun findBeastByFilter(filter :BeastFilter) = viewModelScope.launch {
        searchBeasts.postValue(dao.findBeastByFilter(filter.title, filter.mythology))
    }

    fun findBeastByName(name :String = "") = viewModelScope.launch {
        searchBeasts.postValue(dao.findBeastByName(name))
    }

    val searchFavoriteBeasts = MutableLiveData<List<BeastDataClass>>()

    fun findFavoriteBeastByName(name: String="") = viewModelScope.launch {
        searchFavoriteBeasts.postValue(dao.findFavoriteBeastByName(name))
    }

    fun findBeastByMythology(myth: String="") = viewModelScope.launch {
        searchBeasts.postValue(dao.findBeastByMythology(myth))
    }
    //==============================================
    val mythologyList = MutableLiveData<List<String>>()
    fun getMythologyList() = viewModelScope.launch {
        mythologyList.postValue(dao.getMythologyList())
    }



    class RoomViewModelFactory(private val database: MainDataBase): ViewModelProvider.Factory{
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(RoomViewModel::class.java)){
                @Suppress("UNCHECKED_CAST")
                return RoomViewModel(database) as T
            }
            throw IllegalArgumentException("Unknown ViewModelClass")
        }
    }

}