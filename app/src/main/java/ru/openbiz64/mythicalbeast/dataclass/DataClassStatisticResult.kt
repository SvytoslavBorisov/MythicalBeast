package ru.openbiz64.mythicalbeast.dataclass

import android.os.Parcel
import android.os.Parcelable

data class DataClassStatisticResult(
    val textQuestion: String?,
    val slugQuestion: String?,
    val textCorrectAnswer: String?,
    val textPlayerAnswer: String?,
    val isCorrectAnswer: Boolean
):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readByte() != 0.toByte()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(textQuestion)
        parcel.writeString(slugQuestion)
        parcel.writeString(textCorrectAnswer)
        parcel.writeString(textPlayerAnswer)
        parcel.writeByte(if (isCorrectAnswer) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DataClassStatisticResult> {
        override fun createFromParcel(parcel: Parcel): DataClassStatisticResult {
            return DataClassStatisticResult(parcel)
        }

        override fun newArray(size: Int): Array<DataClassStatisticResult?> {
            return arrayOfNulls(size)
        }
    }
}
