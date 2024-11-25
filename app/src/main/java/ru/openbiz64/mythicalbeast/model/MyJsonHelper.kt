package ru.openbiz64.mythicalbeast.model

import ru.openbiz64.mythicalbeast.dataclass.DataClassQuestionWithoutType
import ru.openbiz64.mythicalbeast.dataclass.DataClassWrongAnswer
import android.content.Context
import android.util.Log
import android.widget.Toast
import org.json.JSONArray
import java.io.IOException


object MyJsonHelper {

    // формирование списка вопросов из строки (json файл как текст)
    fun getQuestionListFromJsonString(jsonString: String): ArrayList<DataClassQuestionWithoutType> {

        val jsonArray = JSONArray(jsonString)
        val questionList = ArrayList<DataClassQuestionWithoutType>()

        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)

            val textQuestion = obj.getString("textQue").replace("_", "\n")
            val slugQuestion = obj.getString("slugQue")
            val slugDialog = obj.getString("slugDlg")
            val textCorrectAnswer = obj.getString("corAns")
            val commentCorrectAnswer = obj.getString("comment").replace("_", "\n")

            val listWrongAnswer = obj.getJSONArray("wrAns")
                .let { jsonArray ->
                    (0 until jsonArray.length()).map { index ->
                        DataClassWrongAnswer(jsonArray.getString(index))
                    }
                }.shuffled()

            questionList.add(
                DataClassQuestionWithoutType(
                    textQuestion,
                    slugQuestion,
                    slugDialog,
                    textCorrectAnswer,
                    commentCorrectAnswer,
                    ArrayList(listWrongAnswer)
                )
            )
        }

        questionList.shuffle()

        return questionList
    }

    fun getQuestionList(fileName: String, context: Context): ArrayList<DataClassQuestionWithoutType> {

        val jsonArray = JSONArray(getJsonText(fileName, context))
        val questionList = ArrayList<DataClassQuestionWithoutType>()

        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)

            val textQuestion = obj.getString("textQue")
            val slugQuestion = obj.getString("slugQue")
            val slugDialog = obj.getString("slugDlg")
            val textCorrectAnswer = obj.getString("corAns").replace(" ", "\n")
            val commentCorrectAnswer = obj.getString("comment").replace("_", "\n")

            val listWrongAnswer = obj.getJSONArray("wrAns")
                .let { jsonArray ->
                    (0 until jsonArray.length()).map { index ->
                        DataClassWrongAnswer(jsonArray.getString(index).replace(" ", "\n"))
                    }
                }.shuffled()

            questionList.add(
                DataClassQuestionWithoutType(
                    textQuestion,
                    slugQuestion,
                    slugDialog,
                    textCorrectAnswer,
                    commentCorrectAnswer,
                    ArrayList(listWrongAnswer)
                )
            )
        }

        questionList.shuffle()

        return questionList
    }

    private fun getJsonText(file: String, context: Context): String {
        return try {
            context.assets.open(file).bufferedReader().use { it.readText() }
        } catch (e: IOException) {
            Toast.makeText(context, "Ошибка чтения файла: ${e.message}", Toast.LENGTH_LONG).show()
            "[]" // Возвращаем пустую структуру JSON при ошибке
        }
    }
}