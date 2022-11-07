package com.uca.upcyclecommunity;

import android.content.Intent;
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
import com.uca.upcyclecommunity.database.Database;

public class ReportReason extends AppCompatActivity {
    private Button upload_btn;
    private RadioGroup radiogroup;
    private String detail;
    private String reportreason;
    private EditText et_reason;
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
        if(Type.equals("POST")){
            detail = it.getStringExtra("reportpost");
        }
        else if(Type.equals("USER")){
            detail = it.getStringExtra("reportuser");
        }
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
                if(et_reason.getVisibility()==View.VISIBLE){
                    reportreason = et_reason.getText().toString();
                }
                String Userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                if(Type.equals("USER")){
                    Database.getDBRoot().child("Report").child("User").child("cnt").get().addOnCompleteListener(task->{
                       if(task.isSuccessful()){
                           Long reportusercnt = task.getResult().getValue(Long.class);
                           if (reportusercnt == null)
                               reportusercnt = 0l;
                           reportusercnt++;
                           Database.getDBRoot().child("Report").child("User").child("cnt").setValue(reportusercnt);
                           Database.getDBRoot().child("Report").child("User").child(reportusercnt + "").child(Userid).setValue(detail);
                           Database.getDBRoot().child("Report").child("User").child(reportusercnt + "").child("reason").setValue(reportreason);
                       }
                    });
                }
                else if(Type.equals("POST")){
                    Database.getDBRoot().child("Report").child("Post"+category).child("cnt").get().addOnCompleteListener(task ->{
                        if(task.isSuccessful()) {
                            Long reportpostcnt = task.getResult().getValue(Long.class);
                            if (reportpostcnt == null)
                                reportpostcnt = 0l;
                            reportpostcnt++;
                            Database.getDBRoot().child("Report").child("Post"+category).child("cnt").setValue(reportpostcnt);
                            Database.getDBRoot().child("Report").child("Post"+category).child(reportpostcnt + "").child(Userid).setValue(detail);
                            Database.getDBRoot().child("Report").child("Post"+category).child(reportpostcnt + "").child("reason").setValue(reportreason);
                        }
                    });
                }
                Personal_Post.CurInst.finish();
                finish();
            }
        });
    }
}
