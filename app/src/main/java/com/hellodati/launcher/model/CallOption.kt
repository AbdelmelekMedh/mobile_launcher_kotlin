package com.hellodati.launcher.model

import java.io.Serializable

class CallOption(
    var mCallOptionCode: CallOptionCode?,
    var mTitle: String?,
    var mSummery: String?,
    var mDescription: String?,
    var mIcon: Int,
    var mColor: Int,
) : Serializable {

    enum class CallOptionCode {
        ROOM, NATIONAL, INTERNATIONAL, EMERGENCY, ROOM_SERVICE
    }



}