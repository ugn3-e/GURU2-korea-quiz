package com.example.guru2

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

class SurveyQ2Fragment : Fragment(R.layout.fragment_survey_q2) {

    private var selectedAnswer: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnHard1 = view.findViewById<Button>(R.id.btnHard1)
        val btnHard2 = view.findViewById<Button>(R.id.btnHard2)
        val btnHard3 = view.findViewById<Button>(R.id.btnHard3)
        val btnHard4 = view.findViewById<Button>(R.id.btnHard4)
        val buttons = listOf(btnHard1, btnHard2, btnHard3, btnHard4)

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

        btnHard1.setOnClickListener { selectButton(btnHard1) }
        btnHard2.setOnClickListener { selectButton(btnHard2) }
        btnHard3.setOnClickListener { selectButton(btnHard3) }
        btnHard4.setOnClickListener { selectButton(btnHard4) }



        btnNext.setOnClickListener {
            (requireActivity() as SurveyActivity).goNextPage()
        }
    }
}