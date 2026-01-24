package com.example.guru2.survey

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import com.example.guru2.R
import com.example.guru2.mypage.MyPageActivity
import com.google.android.material.button.MaterialButton

// 설문 Q1 Fragment
class SurveyQ1Fragment : Fragment(R.layout.fragment_survey_q1) {
    lateinit var toolbar: Toolbar
    private var selectedAnswer: String? = null // 현재 선택된 답안

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 선택지 버튼 + 리스트화
        val btnStudy1 = view.findViewById<Button>(R.id.btnStudy1)
        val btnStudy2 = view.findViewById<Button>(R.id.btnStudy2)
        val btnStudy3 = view.findViewById<Button>(R.id.btnStudy3)
        val btnStudy4 = view.findViewById<Button>(R.id.btnStudy4)
        val buttons = listOf(btnStudy1, btnStudy2, btnStudy3, btnStudy4)

        // 다음 설문으로 넘어가기
        val btnNext = view.findViewById<Button>(R.id.btnNext)

        // 툴바 + 타이틀
        val toolbar = view.findViewById<Toolbar>(R.id.mainToolbar)
        val activity = requireActivity() as? AppCompatActivity
        activity?.setSupportActionBar(toolbar)
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
                        val intent = Intent(requireContext(), MyPageActivity::class.java)
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

        // Next 비활성화
        btnNext.isEnabled = false
        btnNext.backgroundTintList = defaultColor

        fun selectButton(selected: Button) {
            // 모든 버튼 초기화
            buttons.forEach { selected ->
                selected.backgroundTintList = defaultColor
                selected.setTextColor(textDefaultColor)

                if (selected is MaterialButton) {
                    selected.strokeColor = strokeDefaultColor
                }
            }

            // 선택된 버튼만 색 변경(강조)
            selected.backgroundTintList = selectedColorNext
            selected.setTextColor(textSelectedColor2)
            if (selected is MaterialButton) {
                selected.strokeColor = strokeSelectedColor
            }

            // 선택된 답안 저장
            selectedAnswer = selected.text.toString()

            // Next 활성화
            btnNext.isEnabled = true
            btnNext.backgroundTintList = selectedColor
            btnNext.setTextColor(textSelectedColor)
        }

        // 선택지 클릭
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