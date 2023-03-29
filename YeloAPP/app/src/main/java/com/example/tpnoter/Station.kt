package com.example.tpnoter

import android.os.Parcel
import android.os.Parcelable

class Station(
    var nomStation: String?,
    var nbEmplacement: Int, var longitude: Double, var latitude: Double, var placeLibre: Int) : Parcelable{

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readInt(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readInt()
    ) {
    }

    override fun toString(): String {
        return "$nomStation\n $placeLibre place(s) libre(s) sur 8"
    }

    override fun describeContents(): Int {
        TODO("Not yet implemented")
    }

    override fun writeToParcel(parcel: Parcel?, p1: Int) {
        if (parcel != null) {
            parcel.writeString(nomStation)
            parcel.writeInt(nbEmplacement)
            parcel.writeDouble(longitude)
            parcel.writeDouble(latitude)
            parcel.writeInt(placeLibre)
        }
    }

    companion object CREATOR : Parcelable.Creator<Station> {
        override fun createFromParcel(parcel: Parcel): Station {
            return Station(parcel)
        }

        override fun newArray(size: Int): Array<Station?> {
            return arrayOfNulls(size)
        }
    }
}