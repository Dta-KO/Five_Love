package com.d.fivelove.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.d.fivelove.ui.chat.ChatFragment;
import com.d.fivelove.ui.main.MainFragment;
import com.d.fivelove.ui.profile.ProfileFragment;

/**
 * Created by Nguyen Kim Khanh on 8/18/2020.
 */
public class PagerAdapter extends FragmentStateAdapter {

    public PagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return ProfileFragment.newInstance();
            case 1:
                return MainFragment.newInstance();
            default:
                return ChatFragment.newInstance();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
