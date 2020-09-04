package com.d.fivelove.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.d.fivelove.R;
import com.d.fivelove.databinding.ImageItemBinding;
import com.d.fivelove.model.Image;

import java.util.List;

/**
 * Created by Nguyen Kim Khanh on 8/18/2020.
 */
public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {
    private List<Image> images;
    private ViewPager2 viewPager2;

    public ImageAdapter(List<Image> images, ViewPager2 viewPager2) {
        this.images = images;
        this.viewPager2 = viewPager2;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ImageItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.image_item, parent, false);
        return new ImageViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        holder.binding.setImage(images.get(position));
        holder.binding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder {
        private ImageItemBinding binding;


        public ImageViewHolder(@NonNull ImageItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
