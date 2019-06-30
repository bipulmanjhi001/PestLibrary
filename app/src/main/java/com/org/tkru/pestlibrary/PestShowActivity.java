package com.org.tkru.pestlibrary;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.org.tkru.pestlibrary.model.ConnectionDetector;
import com.org.tkru.pestlibrary.model.CustomProgressDialog;


public class PestShowActivity extends AppCompatActivity {
    String id,title,banner_image,contain,footer,sub_id;
    String pretitle,precontains,prebanner,check;
    TextView pest_header,pest_title,pest_contains_text;
    ImageView pest_bannar_image,back_from_details;
    android.support.v7.widget.CardView showcontains;
    CoordinatorLayout coordinatorlayout;
    RelativeLayout checkbannerimg;
    ImageView pest_sub_footer_image;
    RelativeLayout footer_layout;
    Boolean isInternetPresent = false;
    ConnectionDetector cd;
    NestedScrollView nestedScrollView;
    private ProgressDialog pDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_pest_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.sub_toolbar);
        setSupportActionBar(toolbar);

        id = getIntent().getStringExtra("id");
        banner_image=getIntent().getStringExtra("image");
        title = getIntent().getStringExtra("title");
        contain = getIntent().getStringExtra("contains");
        footer=getIntent().getStringExtra("footer");
        sub_id=getIntent().getStringExtra("sub_id");
        pretitle=getIntent().getStringExtra("pretitle");
        precontains=getIntent().getStringExtra("precontains");
        prebanner=getIntent().getStringExtra("prebanner");
        check=getIntent().getStringExtra("check");

        checkbannerimg=(RelativeLayout)findViewById(R.id.checkbannerimg);
        coordinatorlayout = (CoordinatorLayout) findViewById(R.id.pest_sub_details_coordinatorlayout);
        pest_header=(TextView)findViewById(R.id.pest_sub_header);
        pest_bannar_image=(ImageView)findViewById(R.id.pest_sub_bannar_image);
        pest_title=(TextView)findViewById(R.id.pest_sub_title);
        pest_contains_text=(TextView)findViewById(R.id.pest_sub_contains_text);
        back_from_details=(ImageView)findViewById(R.id.back_from_sub_details);

        pest_sub_footer_image=(ImageView)findViewById(R.id.pest_sub_footer_image);
        footer_layout=(RelativeLayout)findViewById(R.id.footer);
        nestedScrollView=(NestedScrollView)findViewById(R.id.pest_details_nest);

        showcontains=(android.support.v7.widget.CardView)findViewById(R.id.showsubcontains);
        back_from_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(check.isEmpty()) {
                    Intent intent = new Intent(PestShowActivity.this, PestActivity.class);
                    intent.putExtra("id", id);
                    intent.putExtra("image", prebanner);
                    intent.putExtra("title", pretitle);
                    intent.putExtra("contain", precontains);
                    startActivity(intent);
                    PestShowActivity.this.finish();

                }else if(!check.isEmpty()){
                    Intent intent = new Intent(PestShowActivity.this, SearchDetailsActivity.class);
                    intent.putExtra("getsearchvalue", pretitle);
                    startActivity(intent);
                    PestShowActivity.this.finish();
                }
            }
        });
        try {
            pest_header.setText(title);
            if(!banner_image.isEmpty()) {
                getProgressbar();
                Glide.with(PestShowActivity.this)
                        .load(banner_image)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .skipMemoryCache(true)
                        .into(pest_bannar_image);
                checkbannerimg.setVisibility(View.VISIBLE);
            }
                pest_title.setText(title);
            if (!contain.isEmpty()) {
                showcontains.setVisibility(View.VISIBLE);
                nestedScrollView.setVisibility(View.VISIBLE);
                pest_contains_text.setText(contain);
            }
            if (!footer.isEmpty()) {
                Glide.with(PestShowActivity.this)
                        .load(footer)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .skipMemoryCache(true)
                        .into(pest_sub_footer_image);
                footer_layout.setVisibility(View.VISIBLE);
                pest_sub_footer_image.setVisibility(View.VISIBLE);
                if(pest_sub_footer_image.getVisibility()== View.VISIBLE) {
                    pDialog.dismiss();
                }
            }
        }catch (NullPointerException e){
            e.printStackTrace();
        }
        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();
        if (!isInternetPresent) {
            Snackbar snackbar = Snackbar
                    .make(coordinatorlayout, "No internet connection ", Snackbar.LENGTH_LONG)
                    .setAction("DISMISS", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                        }
                    });
            TextView snackbarActionTextView = (TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_action);
            snackbarActionTextView.setTextSize(14);

            snackbarActionTextView.setTextColor(Color.RED);
            snackbarActionTextView.setTypeface(snackbarActionTextView.getTypeface(), Typeface.BOLD);

            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.WHITE);
            textView.setMaxLines(1);
            textView.setTextSize(14);
            textView.setSingleLine(true);
            textView.setTypeface(null, Typeface.BOLD);
            snackbar.show();
          }
        }
    public void onBackPressed() {
        if(check.isEmpty()) {
            Intent intent = new Intent(PestShowActivity.this, PestActivity.class);
            intent.putExtra("id", id);
            intent.putExtra("image", prebanner);
            intent.putExtra("title", pretitle);
            intent.putExtra("contain", precontains);
            startActivity(intent);
            PestShowActivity.this.finish();
        }else if(!check.isEmpty()){
            Intent intent = new Intent(PestShowActivity.this, SearchDetailsActivity.class);
            intent.putExtra("getsearchvalue", pretitle);
            startActivity(intent);
            PestShowActivity.this.finish();
        }
    }
    public void getProgressbar(){
        pDialog = new CustomProgressDialog(PestShowActivity.this, R.style.LoadingTheme);
        pDialog.getWindow().setGravity(Gravity.CENTER);
        pDialog.show();
        pDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
    }
}


