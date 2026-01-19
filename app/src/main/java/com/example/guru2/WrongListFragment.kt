package com.example.guru2

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

// 디미 유진_오답 목록 화면 fragment
class WrongListFragment : Fragment(R.layout.fragment_wrong_list) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 디미 유진_recyclerView 설정 (오답을 세로로 리스트로 출력)
        val recycler = view.findViewById<RecyclerView>(R.id.recyclerView)
        recycler.layoutManager = LinearLayoutManager(requireContext())

        // 디미 유진_DB 매니저 생성 (문제 DB, 오답 DB)
        val slangDB = SlangDBManager(requireContext())
        val wrongDB = WrongDBManager(requireContext())

        // 디미 유진_오답 목록 조회
        val list = wrongDB.getWrongQuizList(requireContext(), slangDB)

        // 디미 유진_recyclerView 어댑터 설정, WrongQuizAdapter으로 오답 목록 전달
        recycler.adapter = WrongQuizAdapter(list) { quiz ->
            val intent = Intent(requireContext(), WrongDetailActivity::class.java)
            intent.putExtra("quiz_id", quiz.quizId)
            startActivity(intent)
        }
    }
}
