package org.caiqizhao.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.caiqizhao.mycloud.R;

public class MycloudVersion extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mycloud_version);
        ImageButton button = findViewById(R.id.return_main_activity);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MycloudVersion.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
