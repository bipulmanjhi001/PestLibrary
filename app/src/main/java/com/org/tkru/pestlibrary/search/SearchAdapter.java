package com.org.tkru.pestlibrary.search;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.org.tkru.pestlibrary.R;

import java.util.ArrayList;

public class SearchAdapter extends BaseAdapter  {

    private Context mContext;
    private ArrayList<String> mCategory;
    private ArrayList<String> mImage;
    private LayoutInflater mLayoutInflater;
    private boolean mIsFilterList;

    public SearchAdapter(Context context, ArrayList<String> categoryname, ArrayList<String> category, boolean isFilterList) {
        this.mContext = context;
        this.mCategory =categoryname;
        this.mImage=category;
        this.mIsFilterList = isFilterList;
    }

    public void updateList(ArrayList<String> filterList,ArrayList<String> filterimg, boolean isFilterList) {
        this.mCategory = filterList;
        this.mImage=filterimg;
        this.mIsFilterList = isFilterList;
        notifyDataSetChanged ();
    }
    @Override
    public int getCount() {
        return mCategory.size();
    }

    @Override
    public String getItem(int position) {
        return mCategory.get(position);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        ViewHolder holder = null;
        if(v==null){
            holder = new ViewHolder();
            mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            v = mLayoutInflater.inflate(R.layout.list_item_search, parent, false);
            holder.txtCategory = (TextView)v.findViewById(R.id.txt_country);
            holder.img_url=(TextView)v.findViewById(R.id.img_url);
            holder.img=(ImageView)v.findViewById(R.id.img_icon);
            v.setTag(holder);
        } else{
            holder = (ViewHolder) v.getTag();
        }
        holder.txtCategory.setText(mCategory.get(position));
        holder.img_url.setText(mImage.get(position));
        try {
            Glide.with(mContext)
                    .load(mImage.get(position))
                    .into(holder.img);
        }catch (IllegalArgumentException e){
            e.printStackTrace();
        }
        return v;
    }
}
class ViewHolder{
     TextView txtCategory;
     TextView img_url;
     ImageView img;
}





