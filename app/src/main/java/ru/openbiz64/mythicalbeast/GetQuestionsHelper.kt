package ru.openbiz64.mythicalbeast

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import ru.openbiz64.mythicalbeast.dataclass.DataClassQuestion
import ru.openbiz64.mythicalbeast.dataclass.DataClassQuestionWithoutType
import ru.openbiz64.mythicalbeast.dataclass.GameDataClass
import ru.openbiz64.mythicalbeast.model.MyJsonHelper


object GetQuestionsHelper {

    fun getQuestions(fileName: String, context: Context): ArrayList<DataClassQuestionWithoutType>{

        var questionsList = ArrayList<DataClassQuestionWithoutType>()

        if (fileName.isNotEmpty()) questionsList = MyJsonHelper.getQuestionList("$fileName.json", context)


        return questionsList
    }

    fun getQuestionsFromRequest(request: String): ArrayList<DataClassQuestionWithoutType>{

        var questionsList = ArrayList<DataClassQuestionWithoutType>()

        if (request.isNotEmpty()) questionsList = MyJsonHelper.getQuestionListFromJsonString(request)


        return questionsList
    }

}