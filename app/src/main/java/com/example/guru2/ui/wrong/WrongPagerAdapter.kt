package com.example.guru2.ui.wrong

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

// 오답 노트 ViewPager 어댑터
// 탭(Tab) 단위로 Fragment를 관리하며 각 탭은 서로 다른 오답 유형을 표시
class WrongPagerAdapter(activity: FragmentActivity)
    : FragmentStateAdapter(activity) {

    // 디미 유진_탭 이름
    private val tabs = listOf(
        "신조어 퀴즈"
        // 나중에 "맞춤법 퀴즈" 추가 가능
    )

    // 디미 유진_탭 사이즈 반환
    override fun getItemCount(): Int = tabs.size

    // 디미 유진_fragment 생성
    override fun createFragment(position: Int): Fragment {
        return WrongListFragment()
    }

    // 디미 유진_탭 제목 반환
    fun getTitle(position: Int): String = tabs[position]
}
