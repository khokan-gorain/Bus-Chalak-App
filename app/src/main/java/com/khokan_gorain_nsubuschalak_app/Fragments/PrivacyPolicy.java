package com.khokan_gorain_nsubuschalak_app.Fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.khokan_gorain_nsubuschalak_app.R;
import com.khokan_gorain_nsubuschalak_app.databinding.FragmentPrivacyPolicyBinding;
import com.khokan_gorain_nsubuschalak_app.databinding.FragmentStatusBinding;

public class PrivacyPolicy extends Fragment {

    public FragmentPrivacyPolicyBinding binding;
    WebView mWebView;

     public PrivacyPolicy(){

     }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binding = FragmentPrivacyPolicyBinding.inflate(inflater,container,false);

        mWebView = binding.webView;
        mWebView.setWebViewClient(new myWebViewclient()); // to handle URL redirects in the app
        mWebView.getSettings().setJavaScriptEnabled(true); // to enable JavaScript on web pages
        mWebView.getSettings().setGeolocationEnabled(true); // to enable GPS location on web pages
        mWebView.loadUrl("https://firebasestorage.googleapis.com/v0/b/nsu-bus-chalak.appspot.com/o/bus_chalak_privacy_policy.html?alt=media&token=fed59a8e-2580-4f92-b1dc-7c5499ea1772");

        return  binding.getRoot();
    }

    public class myWebViewclient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            view.loadUrl(url);

            return true;
        }


        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);


            final String urls = url;
            if (urls.contains("mailto") || urls.contains("whatsapp") || urls.contains("tel") || urls.contains("sms") || urls.contains("facebook") || urls.contains("truecaller") || urls.contains("twiter")) {
                mWebView.stopLoading();
                Intent i = new Intent();
                i.setAction(Intent.ACTION_VIEW);
                i.setData(Uri.parse(urls));
                startActivity(i);


            }


        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);

        }
    }

//    @Override
//    public void onBackPressed() {
//        if (mWebView.canGoBack()) {
//            mWebView.goBack();
//        } else {
//            super.onBackPressed();
//        }
//    }

}