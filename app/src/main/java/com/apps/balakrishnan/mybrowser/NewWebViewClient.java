package com.apps.balakrishnan.mybrowser;

import android.graphics.Bitmap;
import android.view.View;
import android.webkit.URLUtil;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;

/**
 * Created by balakrishnan on 24/11/17.
 */

public class NewWebViewClient extends WebViewClient {

    private View mainView;
    private EditText et;
    private String[] aExt = {".pdf",".doc",".docx",".ppt",".pptx"};

    public void initVars()
    {
        et =mainView.findViewById(R.id.urlET);

    }
    @Override
    public boolean shouldOverrideUrlLoading(WebView wv, String url) {

        mainView = wv.getRootView();
        initVars();

        DownloadLinkChecker dlc = new DownloadLinkChecker(url,aExt);
        if(!dlc.isDownloadLink())
            et.setText(url);

        if (URLUtil.isValidUrl(url)) {
            System.out.println(url + " is valid");
            return false;
        }
        else {
            System.out.println(url + " is invalid");
        }

        return true;

    }


    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        //swipeRefreshLayout.setRefreshing(true);
        super.onPageStarted(view, url, favicon);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        //swipeRefreshLayout.setRefreshing(false);
        super.onPageFinished(view, url);
    }

    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        super.onReceivedError(view, request, error);
        System.out.println("error occured");
    }


}

