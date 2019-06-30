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
 * Created by tkru on 7/11/2017.
 */

public class SearchResultDisplay extends ArrayAdapter<Search_Result_Model> {

    private Context mContext;
    private int layoutResourceId;
    private ArrayList<Search_Result_Model> mGridData = new ArrayList<Search_Result_Model>();

    public SearchResultDisplay(Context mContext, int layoutResourceId, ArrayList<Search_Result_Model> mGridData) {
        super(mContext, layoutResourceId, mGridData);
        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.mGridData = mGridData;
    }

    /**
     * Updates grid data and refresh grid items.
     * @param mGridData
     */
    public void setGridData(ArrayList<Search_Result_Model> mGridData) {
        this.mGridData = mGridData;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        SearchResultDisplay.ViewHolded holder;

        if (row == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new SearchResultDisplay.ViewHolded();

            holder.titleTextView = (TextView) row.findViewById(R.id.pest_search_grid_item_title);
            holder.url=(TextView)row.findViewById(R.id.pest_search_grid_item_url);
            holder.id=(TextView)row.findViewById(R.id.pest_search_grid_item_id);
            holder.imageView = (ImageView) row.findViewById(R.id.pest_search_grid_item_image);
            holder.banner_url=(TextView)row.findViewById(R.id.pest_search_grid_banner_url);
            holder.pest_contains=(TextView)row.findViewById(R.id.pest_search_grid_contains);
            holder.footer_url=(TextView)row.findViewById(R.id.pest_search_grid_footer_url);
            holder.main_id=(TextView)row.findViewById(R.id.pest_search_main_item_id);
            row.setTag(holder);
        } else {
            holder = (SearchResultDisplay.ViewHolded) row.getTag();
        }

        Search_Result_Model item = mGridData.get(position);
        holder.titleTextView.setText(item.getPest_search_name());
        holder.url.setText(item.getPest_search_image());
        holder.id.setText(item.getPest_search_id());
        holder.banner_url.setText(item.getPest_search_banner_image());
        holder.pest_contains.setText(item.getPest_search_contains());
        holder.footer_url.setText(item.getPest_search_footer_image());
        holder.main_id.setText(item.getPest_search_main_id());
        Glide.with(mContext)
                .load(item.getPest_search_image())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .thumbnail(0.5f)
                .crossFade()
                .skipMemoryCache(true)
                .into(holder.imageView);
        return row;
    }
    private static class ViewHolded {
        TextView titleTextView,footer_url,main_id;
        TextView url,id,banner_url,pest_contains;
        ImageView imageView;
    }
}
