package com.d.fivelove.adapter;

import android.annotation.SuppressLint;
import android.location.Address;
import android.location.Geocoder;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.databinding.BindingAdapter;

import com.bumptech.glide.Glide;
import com.d.fivelove.R;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by Nguyen Kim Khanh on 8/18/2020.
 */
public class MyBindingAdapter {
    @BindingAdapter("android:src")
    public static void getImage(RoundedImageView view, String resource) {
        Glide.with(view).load(resource).into(view);
    }

    @BindingAdapter("avatar")
    public static void setAvatar(ImageView view, String resource) {
        if (resource != null && resource.length() != 0) {
            Glide.with(view.getContext()).load(resource).into(view);
        } else {
            Glide.with(view.getContext()).load(R.drawable.background_login_image).into(view);
        }
    }

    @BindingAdapter("textIDUser")
    public static void setId(TextView view, String resource) {
        if (resource != null) {
            view.setText(view.getResources().getText(R.string.txt_id).toString().concat(" " + resource));
        } else {
            view.setText(view.getResources().getText(R.string.txt_id).toString().concat(" null"));
        }
    }

    @BindingAdapter("textSexUser")
    public static void setSex(TextView view, String resource) {
        if (resource != null) {
            if (resource.equals("nữ"))
                view.setText(view.getResources().getText(R.string.txt_sex).toString().concat(" nữ"));
            else {
                view.setText(view.getResources().getText(R.string.txt_sex).toString().concat(" nam"));
            }
        } else {
            view.setText(view.getResources().getText(R.string.txt_sex).toString().concat(" không xác định!"));
        }
    }

    @SuppressLint("SetTextI18n")
    @BindingAdapter("userName")
    public static void setName(TextView view, String resource) {
        if (resource != null && resource.length() != 0) {
            view.setText(resource);
        } else {
            view.setText("Anonymous");
        }
    }

    @SuppressLint("SetTextI18n")
    @BindingAdapter({"latitude", "longitude"})
    public static void setLocation(TextView view, String latitude, String longitude) {
        if (latitude != null && longitude != null) {
            Geocoder geocoder = new Geocoder(view.getContext(), Locale.ENGLISH);
            try {
                List<Address> location = geocoder.getFromLocation(Float.parseFloat(latitude), Float.parseFloat(longitude), 1);
                view.setText(location.get(0).getAddressLine(0));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            view.setText("No address data.");
        }

    }
}
