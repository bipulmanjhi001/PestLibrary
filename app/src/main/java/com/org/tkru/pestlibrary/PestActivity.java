package com.org.tkru.pestlibrary;


import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;

import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.text.Html;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.org.tkru.pestlibrary.model.ExpandableHeightGridView;
import com.org.tkru.pestlibrary.model.Pest_Sub_GridView_ImageAdapter;
import com.org.tkru.pestlibrary.model.Pest_Sub_Grid_Model;
import com.org.tkru.pestlibrary.model.ConnectionDetector;
import com.org.tkru.pestlibrary.model.JSONParse;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.NameValuePair;

public class PestActivity extends AppCompatActivity {
    String id,title,banner_image,container;
    TextView pest_header,pest_title,pest_contains_text;
    ImageView pest_bannar_image,back_from_details;
    android.support.v7.widget.CardView showcontains;
    String status,msg,ids,contains,titles,image_url,banner_image_url,footer_image,main_category_id;
    private String PEST_SUB_URL= "http://onlinetemplatedesign.com/pest_control/webservices/sub_category_listing_by_main_catid_service.php?main_cat_id=";
    CoordinatorLayout coordinatorlayout;
    Boolean isInternetPresent = false;
    ConnectionDetector cd;
    ExpandableHeightGridView mGridView;
    private Pest_Sub_GridView_ImageAdapter pest_sub_gridView_imageAdapter;
    private ArrayList<Pest_Sub_Grid_Model> subGridData;
    LinearLayout banner_layout,grid_pest_one;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_pest);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        id = getIntent().getStringExtra("id");
        banner_image=getIntent().getStringExtra("image");
        title = getIntent().getStringExtra("title");
        container = getIntent().getStringExtra("contain");

        coordinatorlayout = (CoordinatorLayout) findViewById(R.id.detailsconnectioncheck);
        pest_header=(TextView)findViewById(R.id.pest_header);
        pest_bannar_image=(ImageView)findViewById(R.id.pest_bannar_image);
        pest_title=(TextView)findViewById(R.id.pest_title);
        pest_contains_text=(TextView)findViewById(R.id.pest_contains_text);
        back_from_details=(ImageView)findViewById(R.id.back_from_details);
        banner_layout=(LinearLayout)findViewById(R.id.banner_layout);
        grid_pest_one=(LinearLayout)findViewById(R.id.grid_pest_one);
        showcontains=(android.support.v7.widget.CardView)findViewById(R.id.showcontains);
        back_from_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PestActivity.this, HomeActivity.class);
                startActivity(intent);
                PestActivity.this.finish();
            }
        });
        try{
            pest_header.setText(title);
            if(banner_image.isEmpty()){
                banner_layout.setVisibility(View.GONE);
                pest_bannar_image.setVisibility(View.GONE);
            }else {
                Glide.with(PestActivity.this)
                        .load(banner_image)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(pest_bannar_image);
            }pest_title.setText(title);
            if(container.isEmpty()){
                showcontains.setVisibility(View.GONE);
            }else {
                pest_contains_text.setText(container);
            }
        }catch (NullPointerException e){
            e.printStackTrace();
        }
        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();
        mGridView=(ExpandableHeightGridView)findViewById(R.id.pest_imagescategory);
        mGridView.setExpanded(true);
        if (isInternetPresent) {
            subGridData = new ArrayList<Pest_Sub_Grid_Model>();
            pest_sub_gridView_imageAdapter= new Pest_Sub_GridView_ImageAdapter(this, R.layout.pest_image_sub_library, subGridData);
            mGridView.setAdapter(pest_sub_gridView_imageAdapter);
            progressBar = (ProgressBar) findViewById(R.id.progressBar);
            //making the progressbar visible
            progressBar.setVisibility(View.VISIBLE);
            new PestSubGallery_image().execute();

            mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                    Pest_Sub_Grid_Model item = (Pest_Sub_Grid_Model) parent.getItemAtPosition(position);
                    Intent intent = new Intent(PestActivity.this, PestShowActivity.class);
                    intent.putExtra("id",item.getPest_main_id());
                    intent.putExtra("sub_id", item.getPest_sub_id());
                    intent.putExtra("image", item.getPest_sub_banner_image());
                    intent.putExtra("title", item.getPest_sub_title());
                    intent.putExtra("pretitle",title);
                    intent.putExtra("precontains",container);
                    intent.putExtra("prebanner",banner_image);
                    intent.putExtra("check","");
                    intent.putExtra("contains", item.getPest_sub_contains());
                    intent.putExtra("footer",item.getPest_sub_footer_image());
                    startActivity(intent);
                    PestActivity.this.finish();
                }
            });
        }else{
            // Ask user to connect to Internet
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
    //---------------------------------------------------------Active Sub Gallery ----------------------------
    private class PestSubGallery_image extends AsyncTask<String, String, Boolean> {
        JSONParse jsonParser = new JSONParse();

        @Override
        protected Boolean doInBackground(String... para) {
            try {
                List<NameValuePair> params = new ArrayList<NameValuePair>();

                String json = jsonParser.makeHttpRequest(PEST_SUB_URL+id, "GET", params);
                try {
                    JSONObject c = new JSONObject(json);
                    status=c.getString("status");
                    msg=c.getString("message");
                    JSONArray array=c.getJSONArray("subcategory");
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = array.getJSONObject(i);

                        ids = object.getString("id");
                        main_category_id=object.getString("main_category_id");
                        titles = object.getString("sub_category_name");
                        image_url=object.getString("image");
                        footer_image=object.getString("footer_img");
                        banner_image_url=object.getString("banner_img");
                        contains = Html.fromHtml(object.getString("content")).toString();

                        Pest_Sub_Grid_Model pest_sub_grid_model = new Pest_Sub_Grid_Model();
                        pest_sub_grid_model.setPest_sub_id(ids);
                        pest_sub_grid_model.setPest_sub_title(titles);
                        pest_sub_grid_model.setPest_sub_image(image_url);
                        pest_sub_grid_model.setPest_sub_footer_image(footer_image);
                        pest_sub_grid_model.setPest_main_id(main_category_id);
                        pest_sub_grid_model.setPest_sub_image(image_url);
                        pest_sub_grid_model.setPest_sub_banner_image(banner_image_url);
                        pest_sub_grid_model.setPest_sub_contains(contains);

                        subGridData.add(pest_sub_grid_model);
                    }
                }catch(JSONException e1){
                    e1.printStackTrace();

                }
                return null;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Boolean aBoolean) {
            pest_sub_gridView_imageAdapter.notifyDataSetChanged();
            mGridView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            if(msg.equals("data not found")){
                grid_pest_one.setVisibility(View.GONE);
                mGridView.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
            }
        }
    }
    public void onBackPressed() {
                Intent intent1  = new Intent(PestActivity.this,HomeActivity.class);
                startActivity(intent1);
                PestActivity.this.finish();
    }
}
