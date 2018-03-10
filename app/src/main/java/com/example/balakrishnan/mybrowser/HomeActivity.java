package com.example.balakrishnan.mybrowser;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.balysv.materialripple.MaterialRippleLayout;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.skyfishjy.library.RippleBackground;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import fisk.chipcloud.ChipCloud;
import fisk.chipcloud.ChipCloudConfig;
import fisk.chipcloud.ChipListener;

import static com.example.balakrishnan.mybrowser.BackgroundParseTask.cnt;
import static com.example.balakrishnan.mybrowser.BackgroundParseTask.cnt1;

public class HomeActivity extends AppCompatActivity {

    ImageView backgroundIV;
    BroadcastReceiver onComplete;
    // ImageView sendIV;
    Typeface regular,bold;
    FontChanger regularFontChanger,boldFontChanger;
    public static EditText urlET;
    TextView welcomeTV;
    TextView clockTV;
    MaterialRippleLayout settingsMRL;
    public static ImageView downloadMRL;
    public static Context cont;
    public int screenWidth,screenHeight;
    public static boolean isDownload=true;
    private CircleProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getSupportActionBar().hide();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE); // the results will be higher than using the activity context object or the getWindowManager() shortcut
        wm.getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;

        cont=this.getApplicationContext();
        //SearchSuggestion s= new SearchSuggestion();
        init();
        fn();
        loadBackgroundImage();
        boldFontChanger.replaceFonts((ViewGroup)this.findViewById(android.R.id.content));
        /*sendIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(urlET.getText().toString().length()!=0)
                    startWebActivity();
                else
                    Toast.makeText(getApplicationContext(),"Please Enter URL",Toast.LENGTH_SHORT).show();
            }
        });*/

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                urlET.setText(intent.getStringExtra(Intent.EXTRA_TEXT)); // Handle text being sent

            }
        }

        urlET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.toString().trim().length()!=0){

                }
                else{
                    progressBar.setColor(getResources().getColor(R.color.red));
                    progressBar.setProgressWithAnimation(0);
                    Picasso.with(cont).load(R.drawable.download).into(HomeActivity.downloadMRL);
                    isDownload=true;

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        urlET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {

                if(urlET.getText().toString().length()!=0) {
                   if(isValidURL(urlET.getText().toString()))
                    alertBoxWindow();
                   else
                       Toast.makeText(getApplicationContext(),"Invalid URL",Toast.LENGTH_LONG).show();
                    //startWebActivity();
                }
                else
                Toast.makeText(getApplicationContext(),"Please Enter URL",Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        /*downloadMRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopup(view);
                System.out.println("1");
            }
        });*/
        //SearchSuggestionInitiate();
        /*urlET.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if(urlET.length()==0)
                {
                    welcomeTV.setVisibility(View.VISIBLE);
                    clockTV.setVisibility(View.VISIBLE);
                    sList1.clear();
                    sAdapter1.notifyDataSetChanged();
                }

                return true;
            }
        });*//*
        urlET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                System.out.println(urlET.getText().toString());
                String q=urlET.getText().toString();
                if(!q.startsWith("http://")&&!q.startsWith("https://"))
                    s1.updateSuggestion(q);
                welcomeTV.setVisibility(View.INVISIBLE);
                clockTV.setVisibility(View.INVISIBLE);


            }

            @Override
            public void afterTextChanged(Editable editable) {

                if(urlET.length()==0)
                {
                    welcomeTV.setVisibility(View.VISIBLE);
                    clockTV.setVisibility(View.VISIBLE);
                    sList1.clear();
                    sAdapter1.notifyDataSetChanged();
                }

            }
        });
        */
        settingsMRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this,AboutActivity.class));
            }
        });

        downloadMRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isDownload) {

                    if (isValidURL(urlET.getText().toString())) {

                        alertBoxWindow();
                    } else
                        Toast.makeText(getApplicationContext(), "Invalid URL", Toast.LENGTH_LONG).show();

                }
                else{
                    ZipTask z = new ZipTask(dpath,dpath+".zip");
                    z.execute();

                }
            }
        });

    }
    //SearchSuggestion s1=new SearchSuggestion();
    //RecyclerView recyclerView;

    //LinearLayoutManager layoutManager;
    /*FlexboxLayoutManager layoutManager;
    public static SuggestionAdapter sAdapter1;
    public static List<Suggestion> sList1 = new ArrayList<>();
    public void SearchSuggestionInitiate()
    {


        layoutManager = new FlexboxLayoutManager(getApplicationContext());
        layoutManager.setFlexDirection(FlexDirection.ROW_REVERSE);
        layoutManager.setJustifyContent(JustifyContent.FLEX_END);

        recyclerView = findViewById(R.id.recycler_view_home);
        sAdapter1 = new SuggestionAdapter(sList1,getApplicationContext(),this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(sAdapter1);
        s1=new SearchSuggestion();
    }*/
    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this,v);
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
                    Toast.makeText(HomeActivity.this,"Storage permission not granted",Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });

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
    public boolean isValidURL(String s)
    {
        if(s.startsWith("http://")||s.startsWith("https://"))
        {
            return true;
        }
        return false;
    }
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
                //replFlag=(cb2.isChecked())?1:0;
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
                    Toast.makeText(HomeActivity.this,"At least one extension should be selected",Toast.LENGTH_SHORT).show();
                }
                else {
                    System.out.println("Extensions "+exts);
                    BackgroundParseTask b = new BackgroundParseTask(HomeActivity.this);
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
    public boolean[] extensionsSelected = {true,false,false,false};
    public static String exts1 = ".pdf .ppt .pptx .PDF .doc .docx";
    public String exts = "";
    private EditText vDpath;
    private EditText edt;
    Switch cb;
    Switch cb2;
    public static String dpath;
    public String[] extensions = {".pdf",".ppt",".doc",".xls"};
    /*public void startDownload(){

        showPopup();

        /*Intent intent = new Intent(HomeActivity.this,WebActivity.class);
        intent.putExtra("url",urlET.getText().toString().trim());
        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(HomeActivity.this, urlET,urlET.getTransitionName());
        startActivity(intent,optionsCompat.toBundle());

    }*/
    public void init(){
        progressBar =  findViewById(R.id.progress_bar);
        backgroundIV = findViewById(R.id.backgroundIV);
        backgroundIV.setDrawingCacheEnabled(true);
        backgroundIV.animate().alpha(0).start();
        //sendIV = findViewById(R.id.sendIV);
        regular = Typeface.createFromAsset(getAssets(), "fonts/product_san_regular.ttf");
        bold = Typeface.createFromAsset(getAssets(),"fonts/product_sans_bold.ttf");
        regularFontChanger = new FontChanger(regular);
        boldFontChanger = new FontChanger(bold);
        //Changing the font throughout the activity
        welcomeTV =findViewById(R.id.welcomeTV);

        urlET = findViewById(R.id.urlET);
        clockTV = findViewById(R.id.textClock);
        settingsMRL = findViewById(R.id.settingsMRL);
        downloadMRL = findViewById(R.id.downloadMRL);

        try{

        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

            ClipData c= clipboard.getPrimaryClip();
            String s=c.getItemAt(0).getText().toString();
            if(s.startsWith("http://")||s.startsWith("https://"))
            urlET.setText(s);

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    Handler handler;
    public void loadBackgroundImage(){


        handler = new Handler();
        HomeActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Picasso.with(getApplicationContext())
                        .load("https://source.unsplash.com/collection/1850974/"+screenHeight+"x"+screenWidth)
                        .skipMemoryCache()
                        .into(backgroundIV, new Callback() {
                            @Override
                            public void onSuccess() {
                                if(backgroundIV.getDrawingCache()!=null){
                                    //Changing the color of send icon
                                   // sendIV.setColorFilter(getDominantColor(backgroundIV.getDrawingCache()));
                                }

                                Animation zoomin= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.zoomin);
                                zoomin.setAnimationListener(new Animation.AnimationListener() {
                                    @Override
                                    public void onAnimationStart(Animation animation) {
                                        backgroundIV.animate().alpha(1).setDuration(2000).start();
                                    }

                                    @Override
                                    public void onAnimationEnd(Animation animation) {
                                        backgroundIV.animate().alpha(0).setDuration(2000).start();
                                    }

                                    @Override
                                    public void onAnimationRepeat(Animation animation) {

                                    }
                                });
                                backgroundIV.setAnimation(zoomin);
                                backgroundIV.startAnimation(zoomin);

                            }


                            @Override
                            public void onError() {
                                Toast.makeText(getApplicationContext(),"No internet!",Toast.LENGTH_LONG).show();
                            }
                        });

                handler.postDelayed(this,20000);
            }
        });

    }

    @Override
    protected void onStop() {

        super.onStop();
        System.out.println("onStop removed handler and cache");
        try {
            trimCache(this);
            clearApplicationData();
            unregisterReceiver(onComplete);
            handler.removeCallbacksAndMessages(null);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    //Getting dominant color from wallpaper
    public static int getDominantColor(Bitmap bitmap) {
        Bitmap newBitmap = Bitmap.createScaledBitmap(bitmap, 1, 1, true);
        final int color = newBitmap.getPixel(0, 0);
        newBitmap.recycle();
        return color;
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
    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.out.println("app destroyed");

        try {
            trimCache(this);
            clearApplicationData();
            handler.removeCallbacksAndMessages(null);
            unregisterReceiver(onComplete);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void trimCache(Context context) {
        try {
            File dir = context.getCacheDir();
            if (dir != null && dir.isDirectory()) {
                deleteDir(dir);
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
    }
    public void clearApplicationData()
    {
        File cache = getCacheDir();
        File appDir = new File(cache.getParent());
        if (appDir.exists()) {
            String[] children = appDir.list();
            for (String s : children) {
                if (!s.equals("lib")) {
                    deleteDir(new File(appDir, s));
                }
            }
        }
    }
    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }

        // The directory is now empty so delete it
        return dir.delete();
    }

    int i=0;
    public void fn()
    {
        onComplete=new BroadcastReceiver() {
            public void onReceive(Context ctxt, Intent intent) {
              i++;

              progressBar.setProgressWithAnimation((i*100)/BackgroundParseTask.cnt);
                if(BackgroundParseTask.cnt==i)
                {
                    System.out.println("Completed "+cnt+" "+cnt1);
                    Toast.makeText(cont,"Download Complete",Toast.LENGTH_SHORT).show();
                    progressBar.setColor(Color.GREEN);
                    HomeActivity.isDownload=false;
                    Picasso.with(cont).load(R.drawable.share_icon).into(HomeActivity.downloadMRL);
                    //zipFolder(dpath,dpath+".zip");
                }
            }

        };


        registerReceiver(onComplete,
                new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));



    }

    class ZipTask extends AsyncTask<Void, Void, Void> {
        ZipTask(){}
        String inputFolderPath,outputZipPath;

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            //TODO Share Intent

        }

        ZipTask(String inputFolderPath, String outputZipPath)
        {
            this.inputFolderPath=inputFolderPath;
            this.outputZipPath = outputZipPath;

        }
        @Override
        protected Void doInBackground(Void... args) {

            zipFolder(inputFolderPath,outputZipPath);
            return null;
        }
        private void zipFolder(String inputFolderPath, String outZipPath) {
            try {

                //CreateDir(Environment.getExternalStoragePublicDirectory(outZipPath).toString());
                FileOutputStream fos = new FileOutputStream(Environment.getExternalStoragePublicDirectory(outZipPath));
                ZipOutputStream zos = new ZipOutputStream(fos);
                File srcFile = new File(Environment.getExternalStoragePublicDirectory(inputFolderPath).toString());
                File[] files = srcFile.listFiles();
                Log.d("", "Zip directory: " + srcFile.getName());
                for (int i = 0; i < files.length; i++) {
                    Log.d("", "Adding file: " + files[i].getName());
                    byte[] buffer = new byte[1024];
                    FileInputStream fis = new FileInputStream(files[i]);
                    zos.putNextEntry(new ZipEntry(files[i].getName()));
                    int length;
                    while ((length = fis.read(buffer)) > 0) {
                        zos.write(buffer, 0, length);
                    }
                    zos.closeEntry();
                    fis.close();
                }
                zos.close();
            } catch (IOException ioe) {
                Log.e("", ioe.getMessage());
            }
        }
    }
}
