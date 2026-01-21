package com.example.guru2

import android.content.Context
import java.util.UUID

object UserManager {

    // 디미 유진_SharedPreferences 파일 이름
    private const val PREF = "user_pref"

    // 디미 유진_사용자 ID 저장 키
    private const val KEY_USER_ID = "user_id"

    // 디미 유진_사용자 ID 조회
    //SharedPreferences에 저장된 사용자 ID를 반환하기
    fun getUserId(context: Context): String {
        val pref = context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
        var userId = pref.getString(KEY_USER_ID, null)

        // 디미 유진_최초 실행 시(user_id 없음) 사용자 ID 생성
        if (userId == null) {
            userId = "local_" + UUID.randomUUID().toString()
            pref.edit().putString(KEY_USER_ID, userId).apply()
        }
        return userId
    }
}
