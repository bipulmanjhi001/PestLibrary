package com.org.tkru.pestlibrary;

import android.app.ProgressDialog;
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
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;

import android.widget.TextView;

import com.org.tkru.pestlibrary.model.ConnectionDetector;
import com.org.tkru.pestlibrary.model.CustomProgressDialog;
import com.org.tkru.pestlibrary.model.ExpandableHeightGridView;
import com.org.tkru.pestlibrary.model.JSONParse;
import com.org.tkru.pestlibrary.model.SearchResultDisplay;
import com.org.tkru.pestlibrary.model.Search_Result_Model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.NameValuePair;

public class SearchDetailsActivity extends AppCompatActivity {
    String statuss,msgs,ids,containss,titles,image_urls,banner_image_urls,namess,main_categorys,footer_img;
    String SEARCH_URL= "http://onlinetemplatedesign.com/pest_control/webservices/search_service.php?key=";
    String getsearchtext;

    CoordinatorLayout coordinatorlayout;
    Boolean isInternetPresent = false;
    ConnectionDetector cd;
    ExpandableHeightGridView mGridView;
    private SearchResultDisplay searchResultDisplay;
    private ArrayList<Search_Result_Model> searchGridData;
    ImageView back_from_search_details;
    TextView pest_search_title;
    TextView pest_search_header;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_search_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.search_toolbar);
        setSupportActionBar(toolbar);
        getsearchtext = getIntent().getStringExtra("getsearchvalue");
        pest_search_header = (TextView) findViewById(R.id.pest_search_header);
        pest_search_header.setText(getsearchtext);
        if (getsearchtext.contains("")){
            getsearchtext=getsearchtext.replace(" ", "_");
    }
        back_from_search_details=(ImageView)findViewById(R.id.back_from_search_details);
        back_from_search_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1  = new Intent(SearchDetailsActivity.this,HomeActivity.class);
                startActivity(intent1);
                SearchDetailsActivity.this.finish();
            }
        });
        pest_search_title=(TextView)findViewById(R.id.pest_search_title);

        coordinatorlayout = (CoordinatorLayout) findViewById(R.id.search_coordinatorlayout);
        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();
        mGridView=(ExpandableHeightGridView)findViewById(R.id.pest_image_search_category);
        mGridView.setExpanded(true);
        if (isInternetPresent) {
            searchGridData = new ArrayList<Search_Result_Model>();
            searchResultDisplay = new SearchResultDisplay(this, R.layout.search_result_display, searchGridData);
            mGridView.setAdapter(searchResultDisplay);
            new SearchPest().execute();
            mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Search_Result_Model item = (Search_Result_Model) parent.getItemAtPosition(position);
                    if(item.getPest_search_main_id().equals("Main Category")){
                        Intent intent = new Intent(SearchDetailsActivity.this, PestActivity.class);
                        intent.putExtra("id",item.getPest_search_id());
                        intent.putExtra("image", item.getPest_search_banner_image());
                        intent.putExtra("title", item.getPest_search_name());
                        intent.putExtra("contain", item.getPest_search_contains());
                        startActivity(intent);
                        SearchDetailsActivity.this.finish();
                    }
                    else if(item.getPest_search_main_id().equals("Sub Category")){
                        Intent intent = new Intent(SearchDetailsActivity.this, PestShowActivity.class);
                        intent.putExtra("id",item.getPest_search_id());
                        intent.putExtra("image", item.getPest_search_banner_image());
                        intent.putExtra("title", item.getPest_search_name());
                        intent.putExtra("contains", item.getPest_search_contains());
                        intent.putExtra("footer",item.getPest_search_footer_image());
                        intent.putExtra("pretitle",getsearchtext);
                        intent.putExtra("precontains",containss);
                        intent.putExtra("prebanner",banner_image_urls);
                        intent.putExtra("sub_id", getsearchtext);
                        intent.putExtra("check", "search");
                        startActivity(intent);
                        SearchDetailsActivity.this.finish();
                    }
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
    //---------------------------------------------------------Search Result ----------------------------
    private class SearchPest extends AsyncTask<String, String, Boolean> {
        JSONParse jsonParser = new JSONParse();
        private ProgressDialog pDialog;
        @Override
        protected void onPreExecute() {
            pDialog = new CustomProgressDialog(SearchDetailsActivity.this, R.style.LoadingTheme);
            pDialog.getWindow().setGravity(Gravity.CENTER);
            pDialog.show();
            pDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
        }
        @Override
        protected Boolean doInBackground(String... para) {
            try {
                List<NameValuePair> params = new ArrayList<NameValuePair>();

                String json = jsonParser.makeHttpRequest(SEARCH_URL + getsearchtext, "POST", params);
                try {
                    JSONObject c = new JSONObject(json);
                    statuss=c.getString("status");
                    msgs=c.getString("message");
                    JSONArray array=c.getJSONArray("searchresult");
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = array.getJSONObject(i);

                        if(object.has("id") && !object.getString("id").isEmpty()){
                            ids = object.getString("id");
                        }
                        if(object.has("category") && !object.getString("category").isEmpty()) {
                            titles = object.getString("category");
                        }
                        if(object.has("name") && !object.getString("name").isEmpty()) {
                            namess = object.getString("name");
                        }
                        if(object.has("main_category_id") && !object.getString("main_category_id").isEmpty()) {
                            main_categorys = object.getString("main_category_id");
                        }
                        if(object.has("image") && !object.getString("image").isEmpty()) {
                            image_urls = object.getString("image");
                        }
                        if(object.has("banner_img") && !object.getString("banner_img").isEmpty()) {
                            banner_image_urls = object.getString("banner_img");
                        }
                        if(object.has("footer_img") && !object.getString("footer_img").isEmpty()) {
                            footer_img = object.getString("footer_img");
                        }
                        if(object.has("content") && !object.getString("content").isEmpty()) {
                            containss = Html.fromHtml(object.getString("content")).toString();
                        }
                        Search_Result_Model search_result_model = new Search_Result_Model ();
                        search_result_model.setPest_search_id(ids);
                        search_result_model.setPest_search_name(namess);
                        search_result_model.setPest_search_image(image_urls);
                        search_result_model.setPest_search_footer_image(footer_img);
                        search_result_model.setPest_search_main_id(titles);
                        search_result_model.setPest_search_banner_image(banner_image_urls);
                        search_result_model.setPest_search_contains(containss);

                        searchGridData.add(search_result_model);
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
        protected void onPostExecute(Boolean aBoolean)
        {
            searchResultDisplay.notifyDataSetChanged();
               pDialog.dismiss();
                pest_search_title.setText(getsearchtext);


        }
    }
    public void onBackPressed() {
        Intent intent1  = new Intent(SearchDetailsActivity.this,HomeActivity.class);
        startActivity(intent1);
        SearchDetailsActivity.this.finish();
    }
}
