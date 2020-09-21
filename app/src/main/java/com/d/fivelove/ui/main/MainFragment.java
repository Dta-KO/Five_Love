package com.d.fivelove.ui.main;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.d.fivelove.adapter.ImageAdapter;
import com.d.fivelove.databinding.MainFragmentBinding;
import com.d.fivelove.model.Image;

import java.util.ArrayList;
import java.util.List;

public class MainFragment extends Fragment {

    private MainViewModel mViewModel;
    private MainFragmentBinding binding;
    private ViewPager2 viewPager2;
    private Handler imageHandler = new Handler(Looper.getMainLooper());
    private Runnable imageRunnable = new Runnable() {
        @Override
        public void run() {
            viewPager2.setCurrentItem(viewPager2.getCurrentItem() + 1, true);
        }
    };

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = MainFragmentBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        // TODO: Use the ViewModel

        //demo data
        List<Image> images = new ArrayList<>();
        images.add(new Image("https://raw.githubusercontent.com/Dta-KO/Dta-KO.github.io/master/IMG_20190406_113909.jpg"));
        images.add(new Image("https://raw.githubusercontent.com/Dta-KO/Dta-KO.github.io/master/avt.jpeg"));
        images.add(new Image("https://raw.githubusercontent.com/Dta-KO/Dta-KO.github.io/master/IMG_20190406_113909.jpg"));
        images.add(new Image("https://raw.githubusercontent.com/Dta-KO/Dta-KO.github.io/master/avt.jpeg"));
        images.add(new Image("https://raw.githubusercontent.com/Dta-KO/Dta-KO.github.io/master/avt.jpeg"));
        images.add(new Image("https://raw.githubusercontent.com/Dta-KO/Dta-KO.github.io/master/avt.jpeg"));

        setViewPager2();
        mViewModel.imageLiveData.postValue(images);
        mViewModel.imageLiveData.observe(getViewLifecycleOwner(), images1 -> {
            viewPager2.setAdapter(new ImageAdapter(images1, viewPager2));
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        imageHandler.removeCallbacks(imageRunnable);
    }

    @Override
    public void onResume() {
        super.onResume();
        imageHandler.postDelayed(imageRunnable, 2000);
    }

    private void setViewPager2() {
        viewPager2 = binding.viewPager;
        viewPager2.setClipToPadding(false);
        viewPager2.setClipChildren(false);
        viewPager2.setOffscreenPageLimit(3);
        viewPager2.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(40));
        compositePageTransformer.addTransformer((page, position) -> {
            float r = 1 - Math.abs(position);
            page.setScaleY(0.65f + r * 0.35f);
        });

        viewPager2.setPageTransformer(compositePageTransformer);
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                imageHandler.removeCallbacks(imageRunnable);
                imageHandler.postDelayed(imageRunnable, 2000);
            }
        });
    }

}