package ru.openbiz64.mythicalbeast.model

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.converter.gson.GsonConverterFactory
import ru.openbiz64.mythicalbeast.APP_CONTEXT
import ru.openbiz64.mythicalbeast.R
import ru.openbiz64.mythicalbeast.dataclass.ArticleDataClass
import ru.openbiz64.mythicalbeast.dataclass.CommonConst
import ru.openbiz64.mythicalbeast.dataclass.FilterDataClass
import ru.openbiz64.mythicalbeast.dataclass.BeastDataClass
import ru.openbiz64.mythicalbeast.dataclass.GameDataClass
import ru.openbiz64.mythicalbeast.retrofit.ArticleList
import ru.openbiz64.mythicalbeast.retrofit.RetrofitAPI
import java.io.IOException
import java.util.Locale
import kotlin.collections.ArrayList


open class MainViewModel(): ViewModel() {
    val articleData: MutableLiveData<ArrayList<ArticleDataClass>> by lazy { MutableLiveData()}
    val gameData: MutableLiveData<ArrayList<GameDataClass>> by lazy { MutableLiveData()}
    val newArticle: MutableLiveData<Int> by lazy { MutableLiveData()}

    private val reserveGameData: ArrayList<GameDataClass> by lazy { arrayListOf()}
    private val reserveData:ArrayList<ArticleDataClass> by lazy { arrayListOf() }
    private val articleCategory:ArrayList<String> = arrayListOf()
    val mythologylist: MutableLiveData<ArrayList<String>> by lazy { MutableLiveData()}

    private var currentFragmentId: Int  = R.id.id_beast

    fun getCurrentFragmentId(): Int{
        return currentFragmentId
    }

    fun setCurrentFragmentId(id: Int){
        currentFragmentId = id
    }

    fun setMythologyList(list: ArrayList<String>){

        mythologylist.postValue(list)
    }

    fun getArticleCategory():ArrayList<String>{
        return articleCategory
    }

    init {

        mythologylist.value = arrayListOf()
        articleData.value = arrayListOf()
        gameData.value = arrayListOf()
        Log.d("MyLog", "init MainViewModel")

        loadArticalsFromInternet()

    }

        fun loadArticalsFromInternet(){
            CoroutineScope(Dispatchers.IO).launch {

                // считываем количество старых историй
                val sharedPref: SharedPreferences = APP_CONTEXT.getSharedPreferences(CommonConst.sharedBlock, Context.MODE_PRIVATE)
                val articleCount = sharedPref.getInt("articleCount", 0)

                //Log.d("MyLog", "Loading Articals from phone")
                //setArticleData(DataJsonLoader.getArticleData("articals", APP_CONTEXT))


                Log.d("MyLog", "Loading Articals from Internet")
                val interceptor = HttpLoggingInterceptor()
                interceptor.level = HttpLoggingInterceptor.Level.BODY

                val client = OkHttpClient.Builder()
                    .addInterceptor(interceptor)
                    .build()

                val retrofit = Retrofit.Builder()
                    .baseUrl("https://amaranth64.github.io").client(client)
                    .addConverterFactory(GsonConverterFactory.create()).build()
                val mainApi = retrofit.create(RetrofitAPI::class.java)

                var response: Response<ArticleList>? = null
                try {
                    response = mainApi.getArticleList()
                } catch (e: IOException) {
                    Log.d("MyLog", "Connection error")
                }
                if (response != null && response.isSuccessful) {
                    response.body()?.articles?.let {
                        reserveData.addAll(it)
                        Log.d("MyLog", "add ArticleData from Retrofit")
                    }
                }

                reserveData.reverse()
                articleData.postValue(reserveData)

                // если загруженных историй больше чем было -> обновляем newArticle (см MainActivity)
                if (articleCount < reserveData.count()){
                    newArticle.postValue(reserveData.count() - articleCount)
                }

                Log.d("MyLog", "Model init CoroutineScope end")
            }
        }

        private fun setArticleData(articleList: ArrayList<ArticleDataClass>){
        reserveData.clear()
        reserveData.addAll(articleList)
        //reserveData.sortBy { it.titleRU }
        articleData.postValue(reserveData)

        Log.d("MyLog", "setArticleData")

        val setOfCategory: MutableSet<String> = mutableSetOf<String>()
        setOfCategory.add(CommonConst.allCategory)
        articleList.forEach{
            setOfCategory.add(it.category)
        }
        articleCategory.clear()
        articleCategory.addAll(setOfCategory)
    }

    fun setFilter(filter: FilterDataClass){
        val tmp: ArrayList<ArticleDataClass> = arrayListOf()
        reserveData.forEach {
            if (it.title.lowercase().contains(filter.searchString.lowercase())){
                tmp.add(it)
            }
        }
        //tmp.sortBy { it.title }
        articleData.postValue(tmp)
    }

    fun setGameData(gameList: ArrayList<GameDataClass>){
        reserveGameData.clear()
        reserveGameData.addAll(gameList)
        gameData.postValue(reserveGameData)
        Log.d("MyLog", "reserveGameData")
    }



}