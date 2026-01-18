package com.example.guru2

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment

class SurveyQ4Fragment : Fragment(R.layout.fragment_survey_q4) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnSubmit = view.findViewById<Button>(R.id.btnSubmit)

        // 마지막 설문 → 홈화면으로 돌아가기
        btnSubmit.setOnClickListener {
            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)

            // 설문 액티비티 종료 (뒤로 가기 방지)
            requireActivity().finish()
        }
    }
}