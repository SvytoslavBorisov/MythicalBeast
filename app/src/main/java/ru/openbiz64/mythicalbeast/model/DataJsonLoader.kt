package ru.openbiz64.mythicalbeast.model

import android.content.Context
import android.widget.Toast
import org.json.JSONArray
import ru.openbiz64.mythicalbeast.dataclass.BeastDataClass
import ru.openbiz64.mythicalbeast.dataclass.ArticleDataClass
import ru.openbiz64.mythicalbeast.dataclass.GameDataClass
import java.io.IOException
import java.io.InputStream

object DataJsonLoader {

    fun getBeastData(fileName:String, context: Context): ArrayList<BeastDataClass>{

        val list: ArrayList<BeastDataClass> = arrayListOf()

        if (fileName.isEmpty()) return list

        val jsonArray = JSONArray(getJsonText("$fileName.json", context))

        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)

            val id = obj.getInt("Id")
            val picUrl = obj.getString("picUrl")
            val title = obj.getString("Title")
            val htmlUrl = obj.getString("htmlUrl")
            val mythology = obj.getString("mythology")
            val description = obj.getString("description")

            val data = BeastDataClass(
                id,
                title,
                picUrl,
                htmlUrl,
                mythology,
                description,
                false
            )

            list.add(data)
        }

        return list
    }

    fun getArticleData(fileName:String, context: Context): ArrayList<ArticleDataClass>{

        val list: ArrayList<ArticleDataClass> = arrayListOf()

        if (fileName.isEmpty()) return list

        val jsonArray = JSONArray(getJsonText("$fileName.json", context))

        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)


            val title = obj.getString("title")
            val category = obj.getString("category")
            val url = obj.getString("url")
            val picURL = obj.getString("picURL")
            val description = obj.getString("description")

            val data = ArticleDataClass(
                title,
                category,
                url,
                picURL,
                description
            )

            list.add(data)
        }

        return list
    }

    fun getGameData(fileName:String, context: Context): ArrayList<GameDataClass>{

        val list: ArrayList<GameDataClass> = arrayListOf()

        if (fileName.isEmpty()) return list

        val jsonArray = JSONArray(getJsonText("$fileName.json", context))

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

        return list
    }


    private fun getJsonText(file:String, context: Context):String{
        var jsonFileData = "[]"
        try {
            val inputStream: InputStream = context.assets.open(file)
            val size:Int = inputStream.available()
            val bytesArray = ByteArray(size)
            inputStream.read(bytesArray)
            jsonFileData = String(bytesArray)
        } catch (e: IOException){
            Toast.makeText(context,"Ошибка базы данных", Toast.LENGTH_LONG).show()
        }
        return jsonFileData
    }

}