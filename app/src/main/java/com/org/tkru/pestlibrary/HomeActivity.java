package com.org.tkru.pestlibrary;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.org.tkru.pestlibrary.model.ConnectionDetector;
import com.org.tkru.pestlibrary.model.ExpandableHeightGridView;
import com.org.tkru.pestlibrary.model.JSONParse;
import com.org.tkru.pestlibrary.model.Pest_GridView_ImageAdapter;
import com.org.tkru.pestlibrary.model.Pest_Grid_Model;
import com.org.tkru.pestlibrary.search.SearchAdapter;
import com.org.tkru.pestlibrary.search.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;


import cz.msebera.android.httpclient.NameValuePair;


public class HomeActivity extends AppCompatActivity {
    String PEST_URL= "http://onlinetemplatedesign.com/pest_control/webservices/main_category_listing_service.php";
    String PEST_LIST_URL="http://onlinetemplatedesign.com/pest_control/webservices/search_service.php";

    String status,msg,id,contains,title,image_url,banner_image_url;
    String getsearchtext;
    ExpandableHeightGridView mGridView;
    private Pest_GridView_ImageAdapter pest_gridView_imageAdapter;
    private ArrayList<Pest_Grid_Model> mGridData;
    ArrayList<String> categoryname= new ArrayList<>();
    ArrayList<String> categoryimg= new ArrayList<>();

    CoordinatorLayout coordinatorlayout;
    Boolean isInternetPresent = false;
    Boolean checkResume=false;
    ConnectionDetector cd;
    int check=1;
    String category;

    ArrayList<String> filterList;
    ArrayList<String> filterimg;

    private static final String TAG = HomeActivity.class.getSimpleName();
    Toolbar toolbar;
    NestedScrollView pest_nestedlayout;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_home);
        toolBarData();
        getIntent().setAction("Already created");
        coordinatorlayout = (CoordinatorLayout) findViewById(R.id.homecheckconnection);

        pest_nestedlayout=(NestedScrollView)findViewById(R.id.pest_nestedlayout);
        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();

        mGridView=(ExpandableHeightGridView)findViewById(R.id.pest_imageFragment);
        mGridView.setExpanded(true);
        if (isInternetPresent) {
            mGridData = new ArrayList<Pest_Grid_Model>();
            pest_gridView_imageAdapter = new Pest_GridView_ImageAdapter(this, R.layout.pest_image_library, mGridData);
            mGridView.setAdapter(pest_gridView_imageAdapter);
            progressBar = (ProgressBar) findViewById(R.id.progressBar);
            //making the progressbar visible
            progressBar.setVisibility(View.VISIBLE);
            new AsyncHttpTask().execute(PEST_URL);
            mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                    Pest_Grid_Model item = (Pest_Grid_Model) parent.getItemAtPosition(position);
                    Intent intent = new Intent(HomeActivity.this, PestActivity.class);
                    intent.putExtra("id", item.getPest_id());
                    intent.putExtra("image", item.getPest_banner_image());
                    intent.putExtra("title", item.getPest_title());
                    intent.putExtra("contain", item.getPest_contains());
                    startActivity(intent);
                    HomeActivity.this.finish();
                }
            });
        }else{
           setContentView(R.layout.activity_offline);
            LinearLayout offline_more=(LinearLayout)findViewById(R.id.offline_more);
            offline_more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(Settings.ACTION_SETTINGS));
                }
            });
        }
    }
    //==================================================================================================//
    //Downloading data asynchronously
    public class AsyncHttpTask extends AsyncTask<String, Void, Integer> {

        @Override
        protected Integer doInBackground(String... params) {
            Integer result = 0;
            try {
                // Create Apache HttpClient
                HttpClient httpclient = new DefaultHttpClient();
                HttpResponse httpResponse = httpclient.execute(new HttpGet(params[0]));
                int statusCode = httpResponse.getStatusLine().getStatusCode();

                // 200 represents HTTP OK
                if (statusCode == 200) {
                    String response = streamToString(httpResponse.getEntity().getContent());
                    parseResult(response);
                    result = 1; // Successful
                } else {
                    result = 0; //"Failed
                }
            } catch (Exception e) {
                Log.d(TAG, e.getLocalizedMessage());
            }
            return result;
        }
        @Override
        protected void onPostExecute(Integer result) {
            // Download complete. Let us update UI
            if (result == 1) {
                pest_gridView_imageAdapter.setGridData(mGridData);
            } else {
                Toast.makeText(HomeActivity.this, "Failed to fetch data!", Toast.LENGTH_SHORT).show();
            }
            progressBar.setVisibility(View.GONE);
            mGridView.setVisibility(View.VISIBLE);
        }
    }
    String streamToString(InputStream stream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
        String line;
        String result = "";
        while ((line = bufferedReader.readLine()) != null) {
            result += line;
        }
        // Close stream
        if (null != stream) {
            stream.close();
        }
        return result;
    }
    /**
     * Parsing the feed results and get the list
     * @param result
     */
    private void parseResult(String result) {
        try {
            JSONObject c = new JSONObject(result);
            JSONArray array = c.getJSONArray("maincategory");
            Pest_Grid_Model pest_grid_model;

            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                pest_grid_model = new Pest_Grid_Model();

                pest_grid_model.setPest_id(object.getString("id"));
                pest_grid_model.setPest_title(object.getString("category_name"));
                pest_grid_model.setPest_banner_image(object.getString("banner_img"));
                pest_grid_model.setPest_contains(Html.fromHtml(object.getString("content")).toString());

                if (array.length() > 0) {
                    if (object != null)
                        pest_grid_model.setPest_image(object.getString("image"));
                }
                mGridData.add(pest_grid_model);
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
    }


 /*   //---------------------------------------------------------Active Gallery ----------------------------
   private class PestGallery_image extends AsyncTask<String, String, Boolean> {
        JSONParse jsonParser = new JSONParse();
        @Override
        protected Boolean doInBackground(String... para) {
            try {
                List<NameValuePair> params = new ArrayList<NameValuePair>();

                String json = jsonParser.makeHttpRequest(PEST_URL, "GET", params);
                try {
                    JSONObject c = new JSONObject(json);
                    status=c.getString("status");
                    msg=c.getString("message");
                    JSONArray array=c.getJSONArray("maincategory");
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = array.getJSONObject(i);
                        id = object.getString("id");
                        title = object.getString("category_name");
                        image_url=object.getString("image");
                        banner_image_url=object.getString("banner_img");
                        contains = Html.fromHtml(object.getString("content")).toString();
                        Pest_Grid_Model pest_grid_model = new Pest_Grid_Model();
                        pest_grid_model.setPest_id(id);
                        pest_grid_model.setPest_title(title);
                        pest_grid_model.setPest_image(image_url);
                        pest_grid_model.setPest_banner_image(banner_image_url);
                        pest_grid_model.setPest_contains(contains);
                        mGridData.add(pest_grid_model);
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
            pest_gridView_imageAdapter.notifyDataSetChanged();
            progressBar.setVisibility(View.GONE);
            mGridView.setVisibility(View.VISIBLE);
        }
    }*/
    //---------------------------------------------------------Title List ----------------------------
    private class PestTitleList extends AsyncTask<String, String, Boolean> {
        JSONParse jsonParser = new JSONParse();
        @Override
        protected Boolean doInBackground(String... para) {
            try {
                List<NameValuePair> params = new ArrayList<NameValuePair>();

                String json = jsonParser.makeHttpRequest(PEST_LIST_URL, "GET", params);
                try {
                    JSONObject c = new JSONObject(json);
                    JSONArray array=c.getJSONArray("searchresult");
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = array.getJSONObject(i);
                         categoryname.add(object.getString("name"));
                         categoryimg.add(object.getString("image"));
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
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id) {

            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    private void toolBarData() {
        toolbar = (Toolbar) findViewById(R.id.search_toolbar);
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadToolBarSearch();
                new PestTitleList().execute();
            }
        });
        toolbar.setTitle("Search");
        toolbar.setTitleTextColor(getResources().getColor(R.color.black));
        setSupportActionBar(toolbar);
    }

    public void loadToolBarSearch() {

        View view = HomeActivity.this.getLayoutInflater().inflate(R.layout.view_toolbar_search, null);
        LinearLayout parentToolbarSearch = (LinearLayout) view.findViewById(R.id.parent_toolbar_search);
        ImageView imgToolBack = (ImageView) view.findViewById(R.id.img_tool_back);
        final EditText edtToolSearch = (EditText) view.findViewById(R.id.edt_tool_search);
        ImageView imgToolMic = (ImageView) view.findViewById(R.id.img_tool_mic);
        final ListView listSearch = (ListView) view.findViewById(R.id.list_search);
        final TextView txtEmpty = (TextView) view.findViewById(R.id.txt_empty);

        Utils.setListViewHeightBasedOnChildren(listSearch);

        edtToolSearch.setHint("Search pest");

        final Dialog toolbarSearchDialog = new Dialog(HomeActivity.this, R.style.MaterialSearch);
        toolbarSearchDialog.setContentView(view);
        toolbarSearchDialog.setCancelable(false);
        toolbarSearchDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        toolbarSearchDialog.getWindow().setGravity(Gravity.BOTTOM);
        toolbarSearchDialog.show();

        toolbarSearchDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        final SearchAdapter searchAdapter = new SearchAdapter(HomeActivity.this, categoryname,categoryimg, false);

        listSearch.setVisibility(View.GONE);
        listSearch.setAdapter(searchAdapter);
        listSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                category = String.valueOf(adapterView.getItemAtPosition(position));
                edtToolSearch.setText(category);
                getsearchtext=edtToolSearch.getText().toString();
                listSearch.setVisibility(View.GONE);
                toolbarSearchDialog.dismiss();
                new android.os.Handler().postDelayed(new Runnable() {
            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

                    @Override
                    public void run() {
                        // This method will be executed once the timer is over
                        // Start your app main activity
                        Intent i = new Intent(HomeActivity.this, SearchDetailsActivity.class);
                        i.putExtra("getsearchvalue",getsearchtext);
                        startActivity(i);
                        // close this activity
                        HomeActivity.this.finish();
                    }
                }, 500);
            }
        });
        edtToolSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                listSearch.setVisibility(View.GONE);
                searchAdapter.updateList(categoryimg,categoryname, true);
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterList = new ArrayList<String>();
                filterimg=new ArrayList<String>();
                boolean isNodata = false;
                if (s.length() > 0) {
                    for (int i = 0; i < categoryname.size(); i++) {
                        if (categoryname.get(i).toLowerCase().startsWith(s.toString().trim().toLowerCase())) {
                            filterList.add(categoryname.get(i));
                            filterimg.add(categoryimg.get(i));

                            listSearch.setVisibility(View.VISIBLE);
                            searchAdapter.updateList(filterList,filterimg, true);
                            listSearch.clearTextFilter();
                            isNodata = true;
                        }
                    }
                    if (!isNodata) {
                        listSearch.setVisibility(View.GONE);
                        txtEmpty.setVisibility(View.VISIBLE);
                        txtEmpty.setText("No data found");
                    }
                } else {
                    listSearch.setVisibility(View.GONE);
                    txtEmpty.setVisibility(View.GONE);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        if(!checkResume){
            try {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
             imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
             toolbarSearchDialog.setCancelable(true);
            }catch (NullPointerException e){
                e.printStackTrace();
            }
        }
        imgToolBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    listSearch.clearTextFilter();
                        filterimg.clear();
                        filterList.clear();
                       categoryimg.clear();
                       categoryname.clear();

                }catch (NullPointerException e){
                    e.printStackTrace();
                }
                toolbarSearchDialog.dismiss();
            }
        });
        imgToolMic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edtToolSearch.setText("");

            }
        });
        toolbarSearchDialog.setOnKeyListener(new Dialog.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface arg0, int keyCode,
                                 KeyEvent event) {
                // TODO Auto-generated method stub
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    try {
                        listSearch.clearTextFilter();
                        filterimg.clear();
                        filterList.clear();
                        categoryimg.clear();
                        categoryname.clear();

                    }catch (NullPointerException e){
                        e.printStackTrace();
                    }
                    toolbarSearchDialog.dismiss();
                }
                return true;
            }
        });
    }
    @Override
    public void onBackPressed() {
        if (check==1) {
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
            check++;
        }else if(check==2) {
            HomeActivity.this.finish();
        }
    }
    @Override
    protected void onResume() {

        String action = getIntent().getAction();
        // Prevent endless loop by adding a unique action, don't restart if action is present
        if(action == null || !action.equals("Already created")) {
            checkResume=false;
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
            finish();
        }
        // Remove the unique action so the next time onResume is called it will restart
        else
            getIntent().setAction(null);
            super.onResume();
    }

}
