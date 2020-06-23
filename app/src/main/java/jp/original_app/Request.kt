package jp.original_app

import java.io.Serializable

class Request (val userName: String,val roomName: String,val userUid: String) :
    Serializable {
}