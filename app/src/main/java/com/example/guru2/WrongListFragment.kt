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
        val store = com.example.guru2.fire.FirestoreWrongNote()

        // 신조어 or 맞춤법
        val firestoreType = if (type == "slang") "slang" else "spell"

        store.loadWrongs(
            type = firestoreType,
            onResult = { records ->

                // 중복 제거하면서 가장 마지막 wrongAt만 남기기 ☑️
                val dedup = linkedMapOf<String, com.example.guru2.fire.WrongRecord>()
                for (r in records) {
                    val key = "${r.quizId}_${r.wrongDate}"
                    val prev = dedup[key]
                    if (prev == null || r.wrongAt > prev.wrongAt) {
                        dedup[key] = r
                    }
                }

                // 최신순 정렬
                val filtered = dedup.values.sortedByDescending { it.wrongAt }

                // 날짜별 그룹 만들기
                val map = linkedMapOf<String, MutableList<WrongItem>>()

                // 신조어
                if (type == "slang") {
                    val slangDB = SlangDBManager(requireContext())
                    filtered.forEach { r ->
                        val quiz = slangDB.getQuizById(r.quizId) ?: return@forEach
                        map.getOrPut(r.wrongDate) { mutableListOf() }
                            .add(WrongItem(r.quizId, quiz.slangWord))
                    }
                } else {
                    // 맞춤법
                    val spellDB = SpellDBManager(requireContext())
                    filtered.forEach { r ->
                        val quiz = spellDB.getQuizById(r.quizId) ?: return@forEach
                        map.getOrPut(r.wrongDate) { mutableListOf() }
                            .add(WrongItem(r.quizId, quiz.correct))
                    }
                }

                val groups = map.map { WrongDateGroup(it.key, it.value) }

                recycler.adapter = WrongDateAdapter(groups) { quizId ->
                    startActivity(
                        Intent(requireContext(), WrongDetailActivity::class.java)
                            .putExtra("quiz_id", quizId)
                            .putExtra("quiz_type", type)
                    )
                }
            },
            onFail = { e ->
                android.util.Log.e("WRONG_FIRE", "오답 불러오기 실패: ${e.message}")
            }
        )
    }
}
