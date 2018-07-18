package com.example.helloapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class SecondaryActivity extends AppCompatActivity {

    public static final int RESULT_CODE_201 = 201;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secondary);

        Intent from = getIntent();
        Bundle bundle = from.getExtras();
        String name = bundle.getString("name");
        int age = bundle.getInt("age");

        Log.e("SecondaryActivity", String.format("name=%s, age=%s", name, age));

        Button button = findViewById(R.id.btn_test);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                intent.putExtra("foo", "bar");
                setResult(RESULT_CODE_201, intent);
                finish();
            }
        });
    }
}
