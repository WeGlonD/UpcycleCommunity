package com.example.upcyclecommunity.community1;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;

import androidx.annotation.NonNull;

import com.example.upcyclecommunity.R;

public class WritePostDeleting extends Dialog {
    public WritePostDeleting(@NonNull Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_deleting);
    }
}
