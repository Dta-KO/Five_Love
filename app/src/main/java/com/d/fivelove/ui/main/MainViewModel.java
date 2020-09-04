package com.d.fivelove.ui.main;

import android.widget.ImageView;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.d.fivelove.model.Image;

import java.util.List;

public class MainViewModel extends ViewModel {
    // TODO: Implement the ViewModel
    public MutableLiveData<List<Image>> imageLiveData;

    public MainViewModel() {
        imageLiveData = new MutableLiveData<>();
    }
}