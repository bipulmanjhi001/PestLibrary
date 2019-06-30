package com.org.tkru.pestlibrary.model;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.org.tkru.pestlibrary.R;


import java.util.ArrayList;

/**
 * Created by tkru on 7/5/2017.
 */

public class Pest_Sub_GridView_ImageAdapter extends ArrayAdapter<Pest_Sub_Grid_Model> {
    private Context mContext;
    private int layoutResourceId;
    private ArrayList<Pest_Sub_Grid_Model> mGridData = new ArrayList<Pest_Sub_Grid_Model>();

    public Pest_Sub_GridView_ImageAdapter(Context mContext, int layoutResourceId, ArrayList<Pest_Sub_Grid_Model> mGridData) {
        super(mContext, layoutResourceId, mGridData);
        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.mGridData = mGridData;
    }
    /**
     * Updates grid data and refresh grid items.
     * @param mGridData
     */
    public void setGridData(ArrayList<Pest_Sub_Grid_Model> mGridData) {
        this.mGridData = mGridData;
        notifyDataSetChanged();
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        Pest_Sub_GridView_ImageAdapter.ViewHolders holder;

        if (row == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new Pest_Sub_GridView_ImageAdapter.ViewHolders();

            holder.titleTextView = (TextView) row.findViewById(R.id.pest_sub_grid_item_title);
            holder.url=(TextView)row.findViewById(R.id.pest_sub_grid_item_url);
            holder.id=(TextView)row.findViewById(R.id.pest_sub_grid_item_id);
            holder.imageView = (ImageView) row.findViewById(R.id.pest_sub_grid_item_image);
            holder.banner_url=(TextView)row.findViewById(R.id.pest_sub_grid_banner_url);
            holder.pest_contains=(TextView)row.findViewById(R.id.pest_sub_grid_contains);
            holder.footer_url=(TextView)row.findViewById(R.id.pest_sub_grid_footer_url);
            holder.main_id=(TextView)row.findViewById(R.id.pest_sub_main_item_id);
            row.setTag(holder);
        } else {
            holder = (Pest_Sub_GridView_ImageAdapter.ViewHolders) row.getTag();
        }
        Pest_Sub_Grid_Model item = mGridData.get(position);
        holder.titleTextView.setText(item.getPest_sub_title());
        holder.url.setText(item.getPest_sub_image());
        holder.id.setText(item.getPest_sub_id());
        holder.banner_url.setText(item.getPest_sub_banner_image());
        holder.pest_contains.setText(item.getPest_sub_contains());
        holder.footer_url.setText(item.getPest_sub_footer_image());
        holder.main_id.setText(item.getPest_main_id());
        Glide.with(mContext)
                .load(item.getPest_sub_image())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .thumbnail(0.5f)
                .crossFade()
                .skipMemoryCache(true)
                .into(holder.imageView);
        return row;
    }
    private static class ViewHolders {
        TextView titleTextView,footer_url,main_id;
        TextView url,id,banner_url,pest_contains;
        ImageView imageView;
    }

}
