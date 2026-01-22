package com.example.guru2

import android.os.Bundle
import android.util.Log
import android.widget.ProgressBar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.firestore.FirebaseFirestore


class SurveyActivity : AppCompatActivity() {
    lateinit var viewPager2: ViewPager2
    lateinit var progressBar: ProgressBar

    // 파이어베이스
    val surveyAnswers = mutableMapOf<String, String>()
    val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        setContentView(R.layout.activity_survey)
        //ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            //val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            //v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            //insets
        //}

        viewPager2 = findViewById<ViewPager2>(R.id.viewPager)
        progressBar = findViewById<ProgressBar>(R.id.progressBar)

        // ViewPager과 Fragment 연결
        viewPager2.adapter = SurveyPagerAdapter(this)

        // 설문 넘어갈 때마다 진행바 업데이트
        viewPager2.registerOnPageChangeCallback(
            object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    progressBar.progress = position + 1
                }
            }
        )
    }

    class SurveyPagerAdapter(activity: AppCompatActivity) :
    // ViewPager에 표시할 설문(Fragment) 관리
        FragmentStateAdapter(activity) {

        override fun getItemCount() = 4 // 설문 문항

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> SurveyQ1Fragment()
                1 -> SurveyQ2Fragment()
                2 -> SurveyQ3Fragment()
                else -> SurveyQ4Fragment()
            }
        }
    }

    fun goNextPage() {
        // 다음 설문으로 이동
        if (viewPager2.currentItem < viewPager2.adapter!!.itemCount - 1) {
            viewPager2.currentItem += 1
        }
    }

    // 파이어베이스
    fun saveAnswer(key: String, value: String) {
        surveyAnswers[key] = value
    }

    fun submitSurvey() {
        val data = hashMapOf(
            "q1" to surveyAnswers["q1"],
            "q2" to surveyAnswers["q2"],
            "q3" to surveyAnswers["q3"],
            "q4" to surveyAnswers["q4"],
            "timestamp" to System.currentTimeMillis()
        )

        db.collection("surveyResults")
            .add(data)
            .addOnSuccessListener {
                Log.d("Firebase", "설문 저장 성공")
            }
            .addOnFailureListener {
                Log.e("Firebase", "설문 저장 실패", it)
            }
    }
}