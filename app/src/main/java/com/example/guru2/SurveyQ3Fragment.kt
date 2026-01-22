package com.example.guru2

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

class SurveyQ3Fragment : Fragment(R.layout.fragment_survey_q3) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val editText = view.findViewById<EditText>(R.id.editText)
        val btnNext = view.findViewById<Button>(R.id.btnNext)

        val defaultColor =
            ContextCompat.getColorStateList(requireContext(), R.color.choice_default)
        val selectedColor =
            ContextCompat.getColorStateList(requireContext(), R.color.choice_selected)

        // 처음에는 Next 비활성화
        btnNext.isEnabled = false
        btnNext.backgroundTintList = defaultColor

        // EditText 입력 감지
        editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val hasText = !s.isNullOrBlank()

                btnNext.isEnabled = hasText
                btnNext.backgroundTintList =
                    if (hasText) selectedColor else defaultColor
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        btnNext.setOnClickListener {
            val answer = editText.text.toString()

            val activity = requireActivity() as SurveyActivity
            activity.saveAnswer("q3", answer)
            activity.goNextPage()
            //(requireActivity() as SurveyActivity).goNextPage()
        }
    }
}