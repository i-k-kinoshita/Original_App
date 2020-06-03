package jp.original_app

import java.io.Serializable

class Report(val date: String, val temperature: String, val condition: String, val remark: String,val cnt: String) :
    Serializable {
}