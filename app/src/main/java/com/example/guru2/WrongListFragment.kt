package com.example.guru2

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class WrongListFragment : Fragment(R.layout.fragment_wrong_list) {

    companion object {
        private const val ARG_TYPE = "type"

        fun newSlangInstance() = WrongListFragment().apply {
            arguments = Bundle().apply { putString(ARG_TYPE, "slang") }
        }

        fun newSpellingInstance() = WrongListFragment().apply {
            arguments = Bundle().apply { putString(ARG_TYPE, "spelling") }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val recycler = view.findViewById<RecyclerView>(R.id.recyclerView)
        recycler.layoutManager = LinearLayoutManager(requireContext())

        val type = arguments?.getString(ARG_TYPE) ?: "slang"
        val wrongDB = WrongDBManager(requireContext())

        val groups = if (type == "slang") {
            wrongDB.getSlangWrongGrouped(requireContext(), SlangDBManager(requireContext()))
        } else {
            wrongDB.getSpellingWrongGrouped(requireContext(), DBManager(requireContext()))
        }

        recycler.adapter = WrongDateAdapter(groups) { quizId ->
            startActivity(
                Intent(requireContext(), WrongDetailActivity::class.java)
                    .putExtra("quiz_id", quizId)
                    .putExtra("quiz_type", type)
            )
        }
    }
}
