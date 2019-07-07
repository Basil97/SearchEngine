package com.example.searchengine;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class Main2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        TextView name =findViewById(R.id.name);
        TextView desc=findViewById(R.id.description);
        TextView data=findViewById(R.id.datasheet);
        TextView reference=findViewById(R.id.refrence);
        Intent inComing=getIntent();
        name.setText(inComing.getStringExtra("name"));
        desc.setText(inComing.getStringExtra("desc"));
        reference.setText(inComing.getStringExtra("link"));
        data.setText(inComing.getStringExtra("data"));
        String imageURL = inComing.getStringExtra("image");


        Picasso.with(this).load(imageURL).into( (ImageView)findViewById(R.id.imageView2));

    }
}
