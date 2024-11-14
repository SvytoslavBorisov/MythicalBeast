package ru.openbiz64.mythicalbeast.dataclass

import ru.openbiz64.mythicalbeast.dataclass.DataClassWrongAnswer

data class DataClassQuestion(
    val level:Int,
    val type:Int,
    val textQuestion: String,
    val slugQuestion: String,
    val slugDialogPic: String,
    val textCorrectAnswer: String,
    val slugCorrectAnswer: String,
    val commentCorrectAnswers: String,
    val wrongAnswers: ArrayList<DataClassWrongAnswer>
)
