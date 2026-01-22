package com.example.guru2

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

class SurveyQ4Fragment : Fragment(R.layout.fragment_survey_q4) {

    private var selectedAnswer: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val editText = view.findViewById<EditText>(R.id.editText)
        val btnSubmit = view.findViewById<Button>(R.id.btnSubmit)

        val defaultColor =
            ContextCompat.getColorStateList(requireContext(), R.color.white) // 하얀색
        val textDefaultColor =
            ContextCompat.getColor(requireContext(), R.color.black)
        val selectedColor =
            ContextCompat.getColorStateList(requireContext(), R.color.confirm_active) // 파란색
        val textSelectedColor =
            ContextCompat.getColor(requireContext(), R.color.white)

        // 처음에는 Submit 비활성화
        btnSubmit.isEnabled = false
        btnSubmit.backgroundTintList = defaultColor
        btnSubmit.setTextColor(textDefaultColor) // 검은색

        // EditText 입력 감지
        editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val hasText = !s.isNullOrBlank()

                btnSubmit.isEnabled = hasText
                btnSubmit.backgroundTintList =
                    if (hasText) selectedColor else defaultColor
                btnSubmit.setTextColor(
                    if(hasText) textSelectedColor else textDefaultColor
                )
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })


        // 마지막 설문 → 홈화면으로 돌아가기
        btnSubmit.setOnClickListener {
            // 제출 완료 토스트 문구
            val answer = editText.text.toString()

            val activity = requireActivity() as SurveyActivity
            activity.saveAnswer("q4", answer)
            activity.submitSurvey()

            Toast.makeText(
                requireContext(),
                "소중한 의견 감사합니다!",
                Toast.LENGTH_SHORT
            ).show()

            startActivity(Intent(requireContext(), MainActivity::class.java))
            // 설문 액티비티 종료 (뒤로 가기 방지)
            requireActivity().finish()
        }
    }
}