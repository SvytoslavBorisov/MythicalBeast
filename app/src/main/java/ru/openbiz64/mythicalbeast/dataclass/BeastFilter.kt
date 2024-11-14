package ru.openbiz64.mythicalbeast.dataclass

import java.io.Serializable

data class BeastFilter(
    var title: String,
    var mythology: String
): Serializable
