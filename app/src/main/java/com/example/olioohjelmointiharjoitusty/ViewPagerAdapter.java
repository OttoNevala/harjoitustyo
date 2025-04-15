//Read me
//This class is created for switching between fragments

package com.example.olioohjelmointiharjoitusty;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.olioohjelmointiharjoitusty.ShowData.InformationFragment;
import com.example.olioohjelmointiharjoitusty.quiz.QuizFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return new SearchFragment();
        } else if (position == 1) {
            return new InformationFragment();
        } else {
            return new QuizFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
