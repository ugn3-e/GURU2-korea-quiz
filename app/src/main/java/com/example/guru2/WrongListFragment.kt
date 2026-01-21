package com.example.guru2

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

// 오답 목록 Fragment
// ViewPager / TabLayout 안에서 사용됨
// 신조어 / 맞춤법 오답 목록을 동일한 Fragment로 처리
// ARG_TYPE 값에 따라 조회 DB 및 화면 내용 분기
class WrongListFragment : Fragment(R.layout.fragment_wrong_list) {

    companion object {

        // 디미 유진_Fragment에 전달할 타입 키
        private const val ARG_TYPE = "type"

        // 디미 유진_신조어 오답 목록 Fragment 생성
        fun newSlangInstance() = WrongListFragment().apply {
            arguments = Bundle().apply { putString(ARG_TYPE, "slang") }
        }

        // 디미 유진_맞춤법 오답 목록 Fragment 생성
        fun newSpellingInstance() = WrongListFragment().apply {
            arguments = Bundle().apply { putString(ARG_TYPE, "spelling") }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        // 디미 유진_오답 목록 RecyclerView 설정
        // 세로 스크롤, 날짜별 그룹 표시
        val recycler = view.findViewById<RecyclerView>(R.id.recyclerView)
        recycler.layoutManager = LinearLayoutManager(requireContext())

        // 디미 유진_Fragment 생성 시 전달받은 타입 ("slang" / "spelling")
        val type = arguments?.getString(ARG_TYPE) ?: "slang"

        // 디미 유진_오답 전용 DB 매니저
        val wrongDB = WrongDBManager(requireContext())

        // 디미 유진_타입에 따라 조회할 오답 데이터 분기
        // 디미 유진_신조어 오답 조회
        val groups = if (type == "slang") {
            wrongDB.getSlangWrongGrouped(requireContext(), SlangDBManager(requireContext()))

        // 디미 유진_맞춤법 오답 조회
        } else {
            wrongDB.getSpellingWrongGrouped(requireContext(), SpellDBManager(requireContext()))
        }

        // 디미 유진_날짜별 RecyclerView 어댑터 연결
        recycler.adapter = WrongDateAdapter(groups) { quizId ->

            // 디미 유진_오답 단어 클릭 시 상세 화면으로 이동
            startActivity(
                Intent(requireContext(), WrongDetailActivity::class.java)
                    .putExtra("quiz_id", quizId)  // 디미 유진_
                    .putExtra("quiz_type", type)  // 디미 유진_
            )
        }
    }
}
