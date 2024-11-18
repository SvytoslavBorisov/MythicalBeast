package ru.openbiz64.mythicalbeast.dataclass

import ru.openbiz64.mythicalbeast.dataclass.DataClassWrongAnswer

data class DataClassQuestionWithoutType(
    val textQuestion: String,
    val slugQuestion: String,
    val slugDialog: String,
    val textCorrectAnswer: String,
    val commentCorrectAnswers: String,
    val wrongAnswers: ArrayList<DataClassWrongAnswer>
)
