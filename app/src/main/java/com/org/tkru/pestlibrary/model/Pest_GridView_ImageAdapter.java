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
import com.squareup.picasso.Picasso;


import java.util.ArrayList;

/**
 * Created by Bipul on 26-04-2017.
 */
public class Pest_GridView_ImageAdapter extends ArrayAdapter<Pest_Grid_Model> {
    private Context mContext;
    private int layoutResourceId;
    private ArrayList<Pest_Grid_Model> mGridData = new ArrayList<Pest_Grid_Model>();

    public Pest_GridView_ImageAdapter(Context mContext, int layoutResourceId, ArrayList<Pest_Grid_Model> mGridData) {
        super(mContext, layoutResourceId, mGridData);
        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.mGridData = mGridData;
    }
    /**
     * Updates grid data and refresh grid items.
     * @param mGridData
     */
    public void setGridData(ArrayList<Pest_Grid_Model> mGridData) {
        this.mGridData = mGridData;
        notifyDataSetChanged();
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder;

        if (row == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();
            holder.titleTextView = (TextView) row.findViewById(R.id.pest_grid_item_title);
            holder.url=(TextView)row.findViewById(R.id.pest_grid_item_url);
            holder.id=(TextView)row.findViewById(R.id.pest_grid_item_id);
            holder.imageView = (ImageView) row.findViewById(R.id.pest_grid_item_image);
            holder.banner_url=(TextView)row.findViewById(R.id.pest_grid_banner_url);
            holder.pest_contains=(TextView)row.findViewById(R.id.pest_grid_contains);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }
        Pest_Grid_Model item = mGridData.get(position);
        holder.titleTextView.setText(item.getPest_title());
        holder.url.setText(item.getPest_image());
        holder.id.setText(item.getPest_id());
        holder.banner_url.setText(item.getPest_banner_image());
        holder.pest_contains.setText(item.getPest_contains());

        Picasso.with(mContext)
                .load(item.getPest_image())
                .into(holder.imageView);
        /*Glide.with(mContext)
                .load(item.getPest_image())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .thumbnail(0.5f)
                .crossFade()
                .skipMemoryCache(true)
                .into(holder.imageView);*/
        return row;
    }
   private static class ViewHolder {
        TextView titleTextView;
        TextView url,id,banner_url,pest_contains;
        ImageView imageView;
    }
}
