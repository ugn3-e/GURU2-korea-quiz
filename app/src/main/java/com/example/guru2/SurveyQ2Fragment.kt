package com.example.guru2

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment

class SurveyQ2Fragment : Fragment(R.layout.fragment_survey_q2) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 다음 설문으로 넘어가기
        val btnNext = view.findViewById<Button>(R.id.btnNext)

        btnNext.setOnClickListener {
            (requireActivity() as SurveyActivity).goNextPage()
        }
    }
}