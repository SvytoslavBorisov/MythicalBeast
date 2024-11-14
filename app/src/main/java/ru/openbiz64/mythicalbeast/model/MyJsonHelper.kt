package ru.openbiz64.mythicalbeast.model

import android.content.Context
import android.widget.Toast
import org.json.JSONArray

import ru.openbiz64.mythicalbeast.dataclass.DataClassQuestion
import ru.openbiz64.mythicalbeast.dataclass.DataClassWrongAnswer
import java.io.IOException
import java.io.InputStream

object MyJsonHelper {



    fun getQuestionList(fileName: String, context: Context):ArrayList<DataClassQuestion>{
        var list: ArrayList<DataClassQuestion> = arrayListOf<DataClassQuestion>()

        val jsonArray = JSONArray(getJsonText(fileName, context))

        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)


            var listWrongAnswer: ArrayList<DataClassWrongAnswer> = arrayListOf()

            val type = obj.getInt("type")
            //val level = obj.getInt("level")
            val textQuestion = obj.getString("textQue")
            val slugQuestion = obj.getString("slugQue")
            val slugDialogPic = obj.getString("slugDlg")
            val textCorrectAnswer = obj.getString("txtCorAns").replace(" ", "\n")
            val slugCorrectAnswer = obj.getString("slugCorAns")
            val commentCorrectAnswer = obj.getString("comment").replace("_", "\n")

            val wrongTextAnswer1 = obj.getString("wrTxtAns1").replace(" ", "\n")
            val wrongSlugAnswer1 = obj.getString("wrSlgAns1")
            listWrongAnswer.add(DataClassWrongAnswer(wrongTextAnswer1,wrongSlugAnswer1))
            val wrongTextAnswer2 = obj.getString("wrTxtAns2").replace(" ", "\n")
            val wrongSlugAnswer2 = obj.getString("wrSlgAns2")
            listWrongAnswer.add(DataClassWrongAnswer(wrongTextAnswer2,wrongSlugAnswer2))
            val wrongTextAnswer3 = obj.getString("wrTxtAns3").replace(" ", "\n")
            val wrongSlugAnswer3 = obj.getString("wrSlgAns3")
            listWrongAnswer.add(DataClassWrongAnswer(wrongTextAnswer3,wrongSlugAnswer3))
            val wrongTextAnswer4 = obj.getString("wrTxtAns4").replace(" ", "\n")
            val wrongSlugAnswer4 = obj.getString("wrSlgAns4")
            listWrongAnswer.add(DataClassWrongAnswer(wrongTextAnswer4,wrongSlugAnswer4))
            val wrongTextAnswer5 = obj.getString("wrTxtAns5").replace(" ", "\n")
            val wrongSlugAnswer5 = obj.getString("wrSlgAns5")
            listWrongAnswer.add(DataClassWrongAnswer(wrongTextAnswer5,wrongSlugAnswer5))
            //val wrongTextAnswer6 = obj.getString("wrTxtAns6").replace(" ", "\n")
            //val wrongSlugAnswer6 = obj.getString("wrSlgAns6")
            //listWrongAnswer.add(DataClassWrongAnswer(wrongTextAnswer6,wrongSlugAnswer6))

            listWrongAnswer.shuffle()

            val answer = DataClassQuestion(1,type,textQuestion, slugQuestion, slugDialogPic,
                textCorrectAnswer,slugCorrectAnswer,commentCorrectAnswer,
                listWrongAnswer
            )

            list.add(answer)

        }

        list.shuffle()

        return list
    }

    private fun getJsonText(file:String, context: Context):String{
        var jsonFileData:String = "[]"
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