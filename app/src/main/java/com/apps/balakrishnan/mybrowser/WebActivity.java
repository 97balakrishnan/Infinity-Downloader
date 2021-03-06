package com.apps.balakrishnan.mybrowser;

/*import android.Manifest;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import fisk.chipcloud.ChipCloud;
import fisk.chipcloud.ChipCloudConfig;
import fisk.chipcloud.ChipListener;
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;

import static com.example.balakrishnan.mybrowser.MainActivity.adapter;

public class WebActivity extends AppCompatActivity{



    Typeface regular,bold;
    FontChanger regularFontChanger,boldFontChanger;
    ImageView downloadIV,sendIV;
    public static EditText urlET;
    public static WebView webView;
    public static List<Suggestion> sList = new ArrayList<>();
    public static SuggestionAdapter sAdapter;
    public static String dpath;
    public static Context cont;
    public ArrayList<String> FileList;
    public ArrayList<String> DownloadList;
    public static SwipeRefreshLayout swipeRefreshLayout;
    private int backFlag = 0,duplFlag=1,replFlag=0;
    private boolean isFirstLoad = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_web);
        getSupportActionBar().hide();
        init();

        //Changing the font throughout the activity
        regularFontChanger.replaceFonts((ViewGroup)this.findViewById(android.R.id.content));

        urlET.setText(getIntent().getStringExtra("url"));
        loadURL(getIntent().getStringExtra("url"));

        urlET.setSelectAllOnFocus(true);

        urlET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                boolean handled = false;
                if (i == EditorInfo.IME_ACTION_SEND) {

                    String url=urlET.getText().toString().trim();
                    loadURL(url);
                    sList.clear();
                    sAdapter.notifyDataSetChanged();
                    urlET.clearFocus();
                    handled = true;
                }
                return handled;
            }
        });

        downloadIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup(v);
            }
        });

        sendIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String url=urlET.getText().toString().trim();
                loadURL(url);
                sList.clear();
                urlET.clearFocus();
                sAdapter.notifyDataSetChanged();
            }
        });

        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {

                        webView.reload();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }
        );

        SearchSuggestionInitiate();

        urlET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                System.out.println(urlET.getText().toString());
                String q=urlET.getText().toString();
                if(!q.startsWith("http://")&&!q.startsWith("https://"))
                    s.updateSuggestion(q);

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if(urlET.length()==0)
                {
                    sList.clear();
                    sAdapter.notifyDataSetChanged();
                }

            }
        });
        urlET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                String url=urlET.getText().toString().trim();
                loadURL(url);

                sList.clear();
                sAdapter.notifyDataSetChanged();
                urlET.clearFocus();
                return true;
            }
        });
        if(sList!=null && sAdapter!=null) {
            sList.clear();
            sAdapter.notifyDataSetChanged();
        }
    }

    SearchSuggestion s;
    RecyclerView recyclerView;

    //LinearLayoutManager layoutManager;
    FlexboxLayoutManager layoutManager;
    public void SearchSuggestionInitiate()
    {


        layoutManager = new FlexboxLayoutManager(getApplicationContext());
        layoutManager.setFlexDirection(FlexDirection.ROW_REVERSE);
        layoutManager.setJustifyContent(JustifyContent.FLEX_END);

        recyclerView = findViewById(R.id.recycler_view);
        sAdapter = new SuggestionAdapter(sList,getApplicationContext(),this);
        recyclerView.setLayoutManager(layoutManager);


        AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter(sAdapter);
        alphaAdapter.setFirstOnly(false);
        recyclerView.setAdapter(alphaAdapter);
        s=new SearchSuggestion();
    }
    public void init(){


        initElements();

        isStoragePermissionGranted();
        FileListFunction();

        downloadIV = findViewById(R.id.downloadIV);
        sendIV = findViewById(R.id.sendIV);
        regular = Typeface.createFromAsset(getAssets(), "fonts/product_san_regular.ttf");
        bold = Typeface.createFromAsset(getAssets(),"fonts/product_sans_bold.ttf");
        regularFontChanger = new FontChanger(regular);
        boldFontChanger = new FontChanger(bold);
        urlET = findViewById(R.id.urlET);

        webView = findViewById(R.id.main_web_view);

        WebSettings ws = webView.getSettings();
        ws.setJavaScriptEnabled(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        webView.getSettings().setMinimumFontSize(10);
        webView.setWebViewClient(new NewWebViewClient());

        swipeRefreshLayout = findViewById(R.id.swipeContainer);

    }


    //Getting dominant color from wallpaper
    public static int getDominantColor(Bitmap bitmap) {
        Bitmap newBitmap = Bitmap.createScaledBitmap(bitmap, 1, 1, true);
        final int color = newBitmap.getPixel(0, 0);
        newBitmap.recycle();
        return color;
    }

    @Override
    public void onBackPressed() {

        if(webView.canGoBack()){
            webView.goBack();

            urlET.setText(webView.getOriginalUrl());
        }
        else{

            supportFinishAfterTransition();
        }
        //To support reverse transitions when user clicks the device back button



    }

    //To show keyboard
    public void showSoftKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        view.requestFocus();
        inputMethodManager.showSoftInput(view, 0);
    }

    //To  hide keyboard
    public void hideSoftKeyboard() {
        if(getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }

    }

    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.download_menu, popup.getMenu());
        popup.show();
        popup.getMenu().getItem(1).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(isStoragePermissionGranted()) {
                    alertBoxWindow();
                }
                else{
                    Toast.makeText(WebActivity.this,"Storage permission not granted",Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });

    }

    public void initElements()
    {

        DownloadList = new ArrayList<>();

        dpath="/Download";
        cont = this.getApplicationContext();



    }

    public void loadURL(String url) {

        if (url.length() == 0)                                          //url field left empty
            Toast.makeText(getApplicationContext(), "Please Enter URL", Toast.LENGTH_SHORT).show();
        else if (url.contains("http://") || url.contains("https://"))  //is a valid url
            webView.loadUrl(url);

        else if(url.contains("."))                                  //is an url but does'nt start with http or https
            webView.loadUrl("http://"+url);

        else if (url.contains("."))                                  //is an url but doesnt start with http or https
            webView.loadUrl("http://" + url);

        else                                                        // not an url therefore searched on google
            webView.loadUrl("http://google.com/search?q=" + url);
        hideSoftKeyboard();

    }


    public static String exts1 = ".pdf .ppt .pptx .PDF .doc .docx";
    public String exts = "";
    private EditText vDpath;
    private EditText edt;
    Switch cb;
    Switch cb2;

    public String[] extensions = {".pdf",".ppt",".doc",".xls"};
    public boolean[] extensionsSelected = {true,false,false,false};

    public void alertBoxWindow()
    {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_layout, null);
        dialogBuilder.setView(dialogView);

        regularFontChanger.replaceFonts((ViewGroup)dialogView);

        FlexboxLayout flexbox = (FlexboxLayout) dialogView.findViewById(R.id.flexboxLayout);


        ChipCloudConfig config = new ChipCloudConfig()
                .selectMode(ChipCloud.SelectMode.multi)
                .checkedChipColor(Color.parseColor("#3F51B5"))
                .checkedTextColor(Color.parseColor("#ffffff"))
                .uncheckedChipColor(Color.parseColor("#efefef"))
                .uncheckedTextColor(Color.parseColor("#000000"))
                .useInsetPadding(true)
                .typeface(bold);
        //Create a new ChipCloud with a Context and ViewGroup:
        ChipCloud chipCloud = new ChipCloud(this, flexbox,config);


        chipCloud.addChip(".pdf", ContextCompat.getDrawable(this, R.drawable.pdf),true);
        chipCloud.addChip(".ppt", ContextCompat.getDrawable(this, R.drawable.ppt),true);
        chipCloud.addChip(".doc", ContextCompat.getDrawable(this, R.drawable.doc),true);
        chipCloud.addChip(".xls", ContextCompat.getDrawable(this, R.drawable.xls),true);
        for(int i=0;i<extensionsSelected.length;i++){
            if(extensionsSelected[i]){
                chipCloud.setChecked(i);
            }
        }

        chipCloud.setListener(new ChipListener() {
            @Override
            public void chipCheckedChange(int i, boolean b, boolean b1) {
                extensionsSelected[i]=b;
            }
        });

        vDpath= dialogView.findViewById(R.id.edit2);

        //cb =dialogView.findViewById(R.id.checkBox);
        cb2=dialogView.findViewById(R.id.checkBox2);

        //cb.setChecked(true);
        cb2.setChecked(false);


        dialogBuilder.setTitle("Download All");

        vDpath.setText(dpath);
        dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                //duplFlag=(cb.isChecked())?1:0;
                replFlag=(cb2.isChecked())?1:0;
                dpath=vDpath.getText().toString();
                CreateDir(dpath);

                exts="";
                for(int i=0;i<extensionsSelected.length;i++){
                    if(extensionsSelected[i]){
                        if(!exts.equals(""))
                        exts =exts+" "+extensions[i];
                        else
                        exts=extensions[i];
                    }
                }
                if(exts.trim().length()==0){
                    Toast.makeText(WebActivity.this,"Atlest one extension should be selected",Toast.LENGTH_SHORT).show();
                }
                else {
                    System.out.println("Extensions "+exts);
                    BackgroundParseTask b = new BackgroundParseTask(WebActivity.this);
                    b.execute(urlET.getText().toString().trim(), exts);
                    System.out.println("status:" + urlET);
                }
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //pass
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    public void CreateDir(String s)
    {
        File dir = new File(s);
        if(dir.exists())
        {
            System.out.println("Directory "+s+" exists");
        }
        else {
            try {
                if (dir.mkdir()) {
                    System.out.println("Directory created");
                } else {
                    System.out.println("Directory is not created");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        dpath=dir.getPath();
    }

    public void FileListFunction()
    {

        FileList=new ArrayList<>();
        FileList.clear();

        File f = Environment.getExternalStoragePublicDirectory(dpath);
        if(f.listFiles()==null)
            return;
        System.out.println(dpath);
        File[] g = f.listFiles();

        for(File x:g)
        {
            String fname=x.getAbsoluteFile().getName();
            if(!FileList.contains(fname))
                FileList.add(fname);

        }
        try {
            Class.forName("android.os.AsyncTask");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                System.out.println("Permission is granted");
                return true;
            } else {

                System.out.println("Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            System.out.println("PERMISSION GRANTED!");
            return true;
        }
    }


}*/
