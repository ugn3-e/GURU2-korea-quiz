package com.example.guru2.ui.wrong

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.guru2.DBManager
import com.example.guru2.R
import com.example.guru2.SlangDBManager
import com.example.guru2.WrongDBManager

class WrongListFragment : Fragment(R.layout.fragment_wrong_list) {

    // 디미 유진_오답 목록 화면 fragment
    companion object {
        private const val ARG_TYPE = "quiz_type"

        fun newSlangInstance(): WrongListFragment {
            return WrongListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_TYPE, "slang")
                }
            }
        }

        fun newSpellingInstance(): WrongListFragment {
            return WrongListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_TYPE, "spelling")
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recycler = view.findViewById<RecyclerView>(R.id.recyclerView)
        recycler.layoutManager = LinearLayoutManager(requireContext())

        val quizType = arguments?.getString(ARG_TYPE) ?: "slang"

        val wrongDB = WrongDBManager(requireContext())
        val list = if (quizType == "slang") {
            val slangDB = SlangDBManager(requireContext())
            wrongDB.getSlangWrongList(requireContext(), slangDB)
        } else {
            val spellDB = DBManager(requireContext())
            wrongDB.getSpellingWrongList(requireContext(), spellDB)
        }

        recycler.adapter = WrongQuizAdapter(list) { quiz ->
            val intent = Intent(requireContext(), WrongDetailActivity::class.java)
            intent.putExtra("quiz_id", quiz.quizId)
            intent.putExtra("quiz_type", quizType)
            startActivity(intent)
        }
    }
}
