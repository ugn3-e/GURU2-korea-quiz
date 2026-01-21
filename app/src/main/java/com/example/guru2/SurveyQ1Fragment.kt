package com.example.guru2

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

class SurveyQ1Fragment : Fragment(R.layout.fragment_survey_q1) {

    private var selectedAnswer: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnStudy1 = view.findViewById<Button>(R.id.btnStudy1)
        val btnStudy2 = view.findViewById<Button>(R.id.btnStudy2)
        val btnStudy3 = view.findViewById<Button>(R.id.btnStudy3)
        val btnStudy4 = view.findViewById<Button>(R.id.btnStudy4)
        val buttons = listOf(btnStudy1, btnStudy2, btnStudy3, btnStudy4)

        // 다음 설문으로 넘어가기
        val btnNext = view.findViewById<Button>(R.id.btnNext)

        val defaultColor =
            ContextCompat.getColorStateList(requireContext(), R.color.choice_default)
        val selectedColor =
            ContextCompat.getColorStateList(requireContext(), R.color.choice_selected)

        // 선택지 초기 상태 (색 없음)
        buttons.forEach {
            it.backgroundTintList = defaultColor
        }

        // Next 버튼 초기 비활성
        btnNext.isEnabled = false
        btnNext.backgroundTintList = defaultColor

        fun selectButton(selected: Button) {
            // 모든 버튼 초기화
            buttons.forEach {
                it.backgroundTintList = defaultColor
            }

            // 선택된 버튼만 색 변경
            selected.backgroundTintList = selectedColor
            selectedAnswer = selected.text.toString()

            // Next 활성화
            btnNext.isEnabled = true
            btnNext.backgroundTintList = selectedColor
        }

        btnStudy1.setOnClickListener { selectButton(btnStudy1) }
        btnStudy2.setOnClickListener { selectButton(btnStudy2) }
        btnStudy3.setOnClickListener { selectButton(btnStudy3) }
        btnStudy4.setOnClickListener { selectButton(btnStudy4) }

        btnNext.setOnClickListener {
            (requireActivity() as SurveyActivity).goNextPage()
        }
    }
}