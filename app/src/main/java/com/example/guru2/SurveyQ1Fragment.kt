package com.example.guru2

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle

class SurveyQ1Fragment : Fragment(R.layout.fragment_survey_q1) {
    lateinit var toolbar: Toolbar

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

        // 툴바
        val toolbar = view.findViewById<androidx.appcompat.widget.Toolbar>(R.id.mainToolbar)
        val activity = requireActivity() as? androidx.appcompat.app.AppCompatActivity
        activity?.setSupportActionBar(toolbar)

        // 만약 타이틀을 바꾸고 싶다면
        activity?.supportActionBar?.title = "Quiz"

        // 메뉴 연결
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

        // 선택지 초기 상태
        buttons.forEach {
            it.backgroundTintList = defaultColor
        }

        // Next 버튼 비활성
        btnNext.isEnabled = false
        btnNext.backgroundTintList = defaultColor

        fun selectButton(selected: Button) {
            // 모든 버튼 초기화
            buttons.forEach { selected ->
                selected.backgroundTintList = defaultColor
                selected.setTextColor(textDefaultColor)

                if (selected is com.google.android.material.button.MaterialButton) {
                    selected.strokeColor = strokeDefaultColor
                }
            }

            // 선택된 버튼만 색 변경
            selected.backgroundTintList = selectedColorNext
            selected.setTextColor(textSelectedColor2)
            if (selected is com.google.android.material.button.MaterialButton) {
                selected.strokeColor = strokeSelectedColor
            }

            selectedAnswer = selected.text.toString()

            // Next 활성화
            btnNext.isEnabled = true
            btnNext.backgroundTintList = selectedColor
            btnNext.setTextColor(textSelectedColor)
        }

        btnStudy1.setOnClickListener { selectButton(btnStudy1) }
        btnStudy2.setOnClickListener { selectButton(btnStudy2) }
        btnStudy3.setOnClickListener { selectButton(btnStudy3) }
        btnStudy4.setOnClickListener { selectButton(btnStudy4) }

        // 다음으로 넘어가기
        btnNext.setOnClickListener {
            selectedAnswer?.let {
                val activity = requireActivity() as SurveyActivity
                activity.saveAnswer("q1", it)
                activity.goNextPage()
            }
        }
    }

}