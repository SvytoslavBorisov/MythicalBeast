package ru.openbiz64.mythicalbeast

import android.content.Context
import ru.openbiz64.mythicalbeast.dataclass.DataClassQuestion
import ru.openbiz64.mythicalbeast.model.MyJsonHelper


object GetQuestionsHelper {

    fun getQuestions(fileName: String, context: Context): ArrayList<DataClassQuestion>{

        var questionsList = ArrayList<DataClassQuestion>()

        if (fileName.isNotEmpty()) questionsList = MyJsonHelper.getQuestionList("$fileName.json", context)


        return questionsList
    }
}