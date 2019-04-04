package com.example.WITTYPHOTOS;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class MyPagerAdapter extends FragmentPagerAdapter {

    private ArrayList<Fragment> mData;
    public MyPagerAdapter(FragmentManager fm) {
        super(fm);

        mData = new ArrayList<>();
        mData.add(new FirstFragment()); //태그 데이터 기반 통계 파이 그래프가 표시될 예정
        mData.add(new SecondFragment()); //네트워크 분석 기반 네트워크 그패프가 표시될 예정
        //더 많은 그래프를 시각화 할 수 있으면 fragment 추가해서 더 많은 뷰 슬라이더 가능
    }

    @Override
    public Fragment getItem(int position) {
        return mData.get(position);
    }

    @Override
    public int getCount() {
        return mData.size();
    }
}
