package com.example.guru2

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle

class SurveyQ2Fragment : Fragment(R.layout.fragment_survey_q2) {
    lateinit var toolbar: Toolbar
    private var selectedAnswer: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnHard1 = view.findViewById<Button>(R.id.btnHard1)
        val btnHard2 = view.findViewById<Button>(R.id.btnHard2)
        val btnHard3 = view.findViewById<Button>(R.id.btnHard3)
        val btnHard4 = view.findViewById<Button>(R.id.btnHard4)
        val buttons = listOf(btnHard1, btnHard2, btnHard3, btnHard4)
        val editText = view.findViewById<EditText>(R.id.editText)

        // 다음 설문으로 넘어가기
        val btnNext = view.findViewById<Button>(R.id.btnNext)

        // 배경색 변수
        val defaultColor =
            ContextCompat.getColorStateList(requireContext(), R.color.button_default_bg) // 하얀색
        val selectedColor =
            ContextCompat.getColorStateList(requireContext(), R.color.confirm_active) // 파란색
        val selectedColorNext =
            ContextCompat.getColorStateList(requireContext(), R.color.choice_default) // 연한 파랭이

        // 글자색 변수
        val textDefaultColor =
            ContextCompat.getColor(requireContext(), R.color.button_text_default)
        val textSelectedColor =
            ContextCompat.getColor(requireContext(), R.color.white)
        val textSelectedColor2 =
            ContextCompat.getColor(requireContext(), R.color.confirm_active)

        // 테두리색 변수
        val strokeDefaultColor =
            ContextCompat.getColorStateList(requireContext(), R.color.button_default_stroke)
        val strokeSelectedColor =
            ContextCompat.getColorStateList(requireContext(), R.color.confirm_active)

        // 툴바
        val toolbar = view.findViewById<androidx.appcompat.widget.Toolbar>(R.id.mainToolbar)
        val activity = requireActivity() as? androidx.appcompat.app.AppCompatActivity
        activity?.setSupportActionBar(toolbar)

        // 만약 타이틀을 바꾸고 싶다면
        activity?.supportActionBar?.title = "Quiz"

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_main, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_mypage -> {
                        val intent = Intent(requireContext(), com.example.guru2.mypage.MyPageActivity::class.java)
                        startActivity(intent)
                        true
                    }
                    R.id.action_survey -> {
                        val intent = Intent(requireContext(), SurveyActivity::class.java)
                        startActivity(intent)
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        // 선택지 초기 상태 (색 없음)
        buttons.forEach {
            it.backgroundTintList = defaultColor
        }

        // Next 버튼 초기 비활성
        btnNext.isEnabled = false
        btnNext.backgroundTintList = defaultColor

        fun selectButton(selected: Button) {
            // 모든 버튼 초기화
            buttons.forEach { selected ->
                selected.backgroundTintList = defaultColor // 하얀색
                selected.setTextColor(textDefaultColor) // 검은색

                if (selected is com.google.android.material.button.MaterialButton) {
                    selected.strokeColor = strokeDefaultColor // 회색(기본) 테두리로 복구
                }
            }

            // 선택된 버튼만 색 변경
            selected.backgroundTintList = selectedColorNext // 연한 파랭이
            selected.setTextColor(textSelectedColor2) // 파랭이
            if (selected is com.google.android.material.button.MaterialButton) {
                selected.strokeColor = strokeSelectedColor
            }

            selectedAnswer = selected.text.toString()

            // Next 활성화
            btnNext.isEnabled = true
            btnNext.backgroundTintList = selectedColor
            btnNext.setTextColor(textSelectedColor)
        }

        btnHard1.setOnClickListener { selectButton(btnHard1) }
        btnHard2.setOnClickListener { selectButton(btnHard2) }
        btnHard3.setOnClickListener { selectButton(btnHard3) }
        btnHard4.setOnClickListener { selectButton(btnHard4) }



        btnNext.setOnClickListener {
            selectedAnswer?.let { choice ->
                val extra = editText.text.toString().trim()

                val finalAnswer = if (extra.isNotEmpty()) {
                    "$choice (추가 의견: $extra)"
                } else {
                    choice
                }

                val activity = requireActivity() as SurveyActivity
                activity.saveAnswer("q2", finalAnswer)
                activity.goNextPage()
            }
            //(requireActivity() as SurveyActivity).goNextPage()
        }
    }
}