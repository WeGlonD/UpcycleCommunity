package com.uca.upcyclecommunity;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;

import androidx.annotation.NonNull;

import com.uca.upcyclecommunity.R;

public class ReportingDialog extends Dialog {
    public ReportingDialog(@NonNull Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_reporting);
    }
}