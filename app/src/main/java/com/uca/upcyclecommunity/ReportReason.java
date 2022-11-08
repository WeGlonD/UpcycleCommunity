package com.uca.upcyclecommunity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.uca.upcyclecommunity.community1.Personal_Post;
import com.uca.upcyclecommunity.community1.WritePostUploading;
import com.uca.upcyclecommunity.database.Database;

public class ReportReason extends AppCompatActivity {
    private Button upload_btn;
    private RadioGroup radiogroup;
    private String detail;
    private String reportreason;
    private EditText et_reason;
    private String nowUser;
    public ReportingDialog reportingDialog;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_reason);
        upload_btn = findViewById(R.id.activity_report_reason_submit_button);
        radiogroup = findViewById(R.id.activity_report_reason_rg);
        et_reason = findViewById(R.id.activity_report_reason_editText);
        Intent it = getIntent();
        String Type = it.getStringExtra("type");
        String category = it.getStringExtra("category");
        nowUser = it.getStringExtra("NowUser");
        if(Type.equals("POST")){
            detail = it.getStringExtra("reportpost");
        }
        else if(Type.equals("USER")){
            detail = it.getStringExtra("reportuser");
        }

        reportingDialog = new ReportingDialog(this);
        reportingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        reportingDialog.setCancelable(false);

        radiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch(i){
                    case R.id.activity_report_reason_1_rb:
                        reportreason = getString(R.string.activity_report_reason_1);
                        break;
                    case R.id.activity_report_reason_2_rb:
                        reportreason = getString(R.string.activity_report_reason_2);
                        break;
                    case R.id.activity_report_reason_3_rb:
                        reportreason = getString(R.string.activity_report_reason_3);
                        break;
                    case R.id.activity_report_reason_4_rb:
                        reportreason = getString(R.string.activity_report_reason_4);
                        break;
                    case R.id.activity_report_reason_5_rb:
                        et_reason.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });
        upload_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reportingDialog.show();
                if(et_reason.getVisibility()==View.VISIBLE){
                    reportreason = et_reason.getText().toString();
                }
                String Userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                if(Type.equals("USER")){
                    Database.getUserRoot().child(nowUser).child("reportuser").child("cnt").get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Long reportusercnt = task.getResult().getValue(Long.class);
                            if (reportusercnt == null)
                                reportusercnt = 0l;
                            reportusercnt++;
                            Database.getUserRoot().child(nowUser).child("reportuser").child("cnt").setValue(reportusercnt);
                            Database.getUserRoot().child(nowUser).child("reportuser").child(reportusercnt + "").setValue(detail);

                            Database.getDBRoot().child("Report").child("User").child("cnt").get().addOnCompleteListener(task1->{
                                if(task1.isSuccessful()){
                                    Long reportusercnt1 = task1.getResult().getValue(Long.class);
                                    if (reportusercnt1 == null)
                                        reportusercnt1 = 0l;
                                    reportusercnt1++;
                                    Database.getDBRoot().child("Report").child("User").child("cnt").setValue(reportusercnt1);
                                    Database.getDBRoot().child("Report").child("User").child(reportusercnt1 + "").child(Userid).setValue(detail);
                                    Database.getDBRoot().child("Report").child("User").child(reportusercnt1 + "").child("reason").setValue(reportreason);

                                    reportingDialog.dismiss();
                                    if(Personal_Post.CurInst!=null)
                                        Personal_Post.CurInst.finish();
                                    finish();
                                }
                            });
                        }
                    });

                }
                else if(Type.equals("POST")){
                    Database.getUserRoot().child(nowUser).child("reportpost"+category).child("cnt").get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Long reportpostcnt = task.getResult().getValue(Long.class);
                            if (reportpostcnt == null)
                                reportpostcnt = 0l;
                            reportpostcnt++;
                            Database.getUserRoot().child(nowUser).child("reportpost"+category).child("cnt").setValue(reportpostcnt);
                            Database.getUserRoot().child(nowUser).child("reportpost"+category).child(reportpostcnt + "").setValue(Long.parseLong(detail));

                            Database.getDBRoot().child("Report").child("Post"+category).child("cnt").get().addOnCompleteListener(task1 ->{
                                if(task1.isSuccessful()) {
                                    Long reportpostcnt1 = task1.getResult().getValue(Long.class);
                                    if (reportpostcnt1 == null)
                                        reportpostcnt1 = 0l;
                                    reportpostcnt1++;
                                    Database.getDBRoot().child("Report").child("Post"+category).child("cnt").setValue(reportpostcnt1);
                                    Database.getDBRoot().child("Report").child("Post"+category).child(reportpostcnt1 + "").child(Userid).setValue(Long.parseLong(detail));
                                    Database.getDBRoot().child("Report").child("Post"+category).child(reportpostcnt1 + "").child("reason").setValue(reportreason);

                                    reportingDialog.dismiss();
                                    if(Personal_Post.CurInst!=null)
                                        Personal_Post.CurInst.finish();
                                    finish();
                                }
                            });
                        }
                    });

                }

            }
        });
    }
}
