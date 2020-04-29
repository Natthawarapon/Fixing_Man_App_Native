package com.natth.fixingmanapp.model

import android.os.Parcel
import android.os.Parcelable

data  class Technician (
    var id_tech:Int = 0 ,
    var nameStore:String? = null ,
    var nameOwn :String?=null,
    var numberphone:String?=null ,
    var email:String?=null,
    var latitude:String?=null,
    var longitude:String?=null,
    var password:String?=null ,
    var address:String?=null

) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id_tech)
        parcel.writeString(nameStore)
        parcel.writeString(nameOwn)
        parcel.writeString(numberphone)
        parcel.writeString(email)
        parcel.writeString(latitude)
        parcel.writeString(longitude)
        parcel.writeString(password)
        parcel.writeString(address)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Technician> {
        override fun createFromParcel(parcel: Parcel): Technician {
            return Technician(parcel)
        }

        override fun newArray(size: Int): Array<Technician?> {
            return arrayOfNulls(size)
        }
    }
}
