package org.techtown.smarket_android.Loading;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import org.techtown.smarket_android.R;

public class CustomAnimationDialog extends ProgressDialog {
    private Context c;
    private ImageView imgLogo;
    public CustomAnimationDialog(Context context){
        super(context);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setCanceledOnTouchOutside(false);
        c = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_dialog_loading);
        imgLogo = findViewById(R.id.loadingIcon);
        Animation anim = AnimationUtils.loadAnimation(c, R.anim.loading);
        imgLogo.setAnimation(anim);
    }

    @Override
    public void show() {
        super.show();
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }
}
