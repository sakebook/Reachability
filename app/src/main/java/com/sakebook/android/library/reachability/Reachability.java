package com.sakebook.android.library.reachability;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by sakemotoshinya on 2014/09/10.
 */
public class Reachability {

    Context mContext;

    public Reachability() {
    }

    public Reachability(Context context) {
        this.mContext = context;
    }

    public void show() {
        Toast.makeText(mContext, "toast", Toast.LENGTH_LONG).show();
    }
}
