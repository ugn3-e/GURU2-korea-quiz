package com.example.guru2.survey

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import com.example.guru2.R
import com.example.guru2.home.MainActivity
import com.example.guru2.mypage.MyPageActivity

class SurveyQ4Fragment : Fragment(R.layout.fragment_survey_q4) {
    lateinit var toolbar: Toolbar

    private var selectedAnswer: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val editText = view.findViewById<EditText>(R.id.editText)
        val btnSubmit = view.findViewById<Button>(R.id.btnSubmit)

        val defaultColor =
            ContextCompat.getColorStateList(requireContext(), R.color.button_default_bg) // 하얀색
        val textDefaultColor =
            ContextCompat.getColor(requireContext(), R.color.button_text_default)
        val selectedColor =
            ContextCompat.getColorStateList(requireContext(), R.color.confirm_active) // 파란색
        val textSelectedColor =
            ContextCompat.getColor(requireContext(), R.color.white)

        // 툴바
        val toolbar = view.findViewById<androidx.appcompat.widget.Toolbar>(R.id.mainToolbar)
        val activity = requireActivity() as? AppCompatActivity
        activity?.setSupportActionBar(toolbar)

        // 만약 타이틀을 바꾸고 싶다면
        activity?.supportActionBar?.title = "Quiz"

        // 메뉴 툴바 연결
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

        // 처음에는 Submit 비활성화
        btnSubmit.isEnabled = false
        btnSubmit.backgroundTintList = defaultColor
        btnSubmit.setTextColor(textDefaultColor)

        // EditText 입력
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