package com.example.guru2.wrong

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

// 오답 노트 ViewPager용 어댑터
// 신조어 / 맞춤법 오답 Fragment를 페이지로 관리

class WrongPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    // ViewPager에 표시할 페이지 수 (0: 신조어/ 1: 맞춤법)
    override fun getItemCount(): Int = 2

    // position에 따라 표시할 Fragment 생성
    override fun createFragment(position: Int): Fragment {

        // 신조어 오답 목록 Fragment
        return if (position == 0)
            WrongListFragment.Companion.newSlangInstance()
        // 맞춤법 오답 목록 Fragment
        else
            WrongListFragment.Companion.newSpellingInstance()
    }
}