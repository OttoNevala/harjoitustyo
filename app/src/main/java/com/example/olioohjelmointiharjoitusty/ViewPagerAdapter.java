// Read me
// This class is created for switching between fragments

package com.example.olioohjelmointiharjoitusty;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.olioohjelmointiharjoitusty.ShowData.InformationFragment;
import com.example.olioohjelmointiharjoitusty.quiz.QuizFragment;
import com.example.olioohjelmointiharjoitusty.comparison.CompareFragment; // Ota käyttöön myöhemmin

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
        } else if (position == 2) {
            return new QuizFragment();
        } else {
            return new CompareFragment(); // position == 3
        }
    }

    @Override
    public int getItemCount() {
        return 4; // Palauta 4 kun CompareFragment otetaan käyttöön
    }
}
