package ru.openbiz64.mythicalbeast.model

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
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
    val newGameData: MutableLiveData<Int> by lazy { MutableLiveData()}

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
        loadGameListFromInternet()
    }

    fun loadArticalsFromInternet(){

        // считываем количество старых историй
        val sharedPref: SharedPreferences = APP_CONTEXT.getSharedPreferences(CommonConst.sharedBlock, Context.MODE_PRIVATE)
        val articleCount = sharedPref.getInt("articleCount", 0)

        val list: ArrayList<ArticleDataClass> = arrayListOf()
        val url = "https://amaranth64.github.io/myth/articals_web.json"
        val queue = Volley.newRequestQueue(APP_CONTEXT)
        val sRequest = StringRequest(
            Request.Method.GET,
            url,
            { response ->

                if (response.isNotEmpty()) {

                    val jsonArray = JSONObject(response).getJSONArray("articles")


                    for (i in 0 until jsonArray.length()) {

                        val obj = jsonArray.getJSONObject(i)

                        val id = obj.getString("id")
                        val title = obj.getString("title")
                        val category = obj.getString("category")

                        val urlA = obj.getString("url")
                        val picURL = obj.getString("picURL")
                        val description = obj.getString("description")

                        val data = ArticleDataClass(
                            title,
                            category,
                            urlA,
                            picURL,
                            description
                        )

                        list.add(data)
                    }

                    list.reverse()
                    articleData.postValue(list)

                    // если загруженных историй больше чем было -> обновляем newArticle (см MainActivity)
                    if (articleCount < list.count()){
                        newArticle.postValue(list.count() - articleCount)
                    }

                    Log.d("MyLog", "Model init CoroutineScope end")
                }
            },
            {
                Log.d("MyLog", "VolleyError: $it")
            }
        )
        sRequest.setShouldCache(false)
        queue.add(sRequest)

    }

    fun loadGameListFromInternet(){

        // считываем количество старых историй
        val sharedPref: SharedPreferences = APP_CONTEXT.getSharedPreferences(CommonConst.sharedBlock, Context.MODE_PRIVATE)
        val gameCount = sharedPref.getInt("gameCount", 0)

        val list: ArrayList<GameDataClass> = arrayListOf()
        val url = "https://amaranth64.github.io/myth/quiz/games.json"
        val queue = Volley.newRequestQueue(APP_CONTEXT)
        val sRequest = StringRequest(
            Request.Method.GET,
            url,
            { response ->

                if (response.isNotEmpty()) {

                    val jsonArray = JSONArray(response)

                    for (i in 0 until jsonArray.length()) {
                        val obj = jsonArray.getJSONObject(i)

                        val title = obj.getString("title")
                        val slug = obj.getString("slug")
                        val picURL = obj.getString("picURL")
                        val description = obj.getString("description")

                        val data = GameDataClass(
                            title,
                            slug,
                            picURL,
                            description
                        )

                        list.add(data)
                    }
                    setGameData(list)

                    // если загруженных историй больше чем было -> обновляем newArticle (см MainActivity)
                    if (gameCount < list.count()){
                        newGameData.postValue(list.count() - gameCount)
                    }

                }
            },
            {
                Log.d("MyLog", "VolleyError: $it")
            }
        )
        sRequest.setShouldCache(false)
        queue.add(sRequest)
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

    private fun setGameData(gameList: ArrayList<GameDataClass>){
        reserveGameData.clear()
        reserveGameData.addAll(gameList)
        gameData.postValue(reserveGameData)
        Log.d("MyLog", "reserveGameData")
    }



}