package com.example.guru2

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
import android.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle

class SurveyQ3Fragment : Fragment(R.layout.fragment_survey_q3) {

    lateinit var toolbar: Toolbar

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val editText = view.findViewById<EditText>(R.id.editText)
        val btnNext = view.findViewById<Button>(R.id.btnNext)

        val defaultColor =
            ContextCompat.getColorStateList(requireContext(), R.color.white) // 하얀색
        val textDefaultColor =
            ContextCompat.getColor(requireContext(), R.color.black)
        val selectedColor =
            ContextCompat.getColorStateList(requireContext(), R.color.confirm_active) // 파란색
        val textSelectedColor =
            ContextCompat.getColor(requireContext(), R.color.white)

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

        // 처음에는 Next 비활성화
        btnNext.isEnabled = false
        btnNext.backgroundTintList = defaultColor
        btnNext.setTextColor(textDefaultColor)

        // EditText 입력
        editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val hasText = !s.isNullOrBlank()

                btnNext.isEnabled = hasText
                btnNext.backgroundTintList =
                    if (hasText) selectedColor else defaultColor
                btnNext.setTextColor(
                    if(hasText) textSelectedColor else textDefaultColor
                )

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        btnNext.setOnClickListener {
            val answer = editText.text.toString()

            val activity = requireActivity() as SurveyActivity
            activity.saveAnswer("q3", answer)
            activity.goNextPage()
        }
    }
}