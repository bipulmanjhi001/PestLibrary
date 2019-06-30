package com.org.tkru.pestlibrary.model;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.org.tkru.pestlibrary.R;


/**
 * Created by Bipul on 06-01-2017.
 */
public class CustomProgressDialog extends ProgressDialog {
    ImageView la;
    public CustomProgressDialog(Context context) {
        super(context);
    }

    public CustomProgressDialog(Context context, int theme) {
        super(context, theme);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_progress_dialog);
        la = (ImageView) findViewById(R.id.animation);
        startAnimation();
    }
    private void startAnimation(){
        Animation rotation = AnimationUtils.loadAnimation(getContext(), R.anim.right_in);
        la.startAnimation(rotation);
    }

}