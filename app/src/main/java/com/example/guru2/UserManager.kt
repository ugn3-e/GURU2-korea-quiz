package com.example.guru2

import android.content.Context
import java.util.UUID

object UserManager {

    // SharedPreferences 파일 이름
    private const val PREF = "user_pref"

    // 사용자 ID 저장 키
    private const val KEY_USER_ID = "user_id"

    // 사용자 ID 조회
    //SharedPreferences에 저장된 사용자 ID를 반환
    fun getUserId(context: Context): String {
        val pref = context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
        var userId = pref.getString(KEY_USER_ID, null)

        // 처음 실행 시 사용자 ID 생성
        if (userId == null) {
            userId = "local_" + UUID.randomUUID().toString()
            pref.edit().putString(KEY_USER_ID, userId).apply()
        }
        return userId
    }
}
