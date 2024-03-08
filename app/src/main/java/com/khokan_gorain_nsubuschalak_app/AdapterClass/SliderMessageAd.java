package com.khokan_gorain_nsubuschalak_app.AdapterClass;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.khokan_gorain_nsubuschalak_app.Fragments.HomeFragment;
import com.khokan_gorain_nsubuschalak_app.R;

import java.util.ArrayList;

public class SliderMessageAd extends PagerAdapter {
    public Context context;
    public LayoutInflater layoutInflater;
    ArrayList<String> driverNoticeMessage;


    public SliderMessageAd(Context context,ArrayList driverNoticeMessage){
        this.context = context;
        this.driverNoticeMessage = driverNoticeMessage;
    }

    @Override
    public int getCount() {
        return driverNoticeMessage.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        TextView noticeMessage;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.notice_message_lyt,null);
        noticeMessage = view.findViewById(R.id.noticeMessage);
        noticeMessage.setText(driverNoticeMessage.get(position));
        ViewPager viewPager = (ViewPager) container;
        viewPager.addView(view,0);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        ViewPager viewPager = (ViewPager) container;
        View view = (View) object;
        viewPager.removeView(view);
    }
}

