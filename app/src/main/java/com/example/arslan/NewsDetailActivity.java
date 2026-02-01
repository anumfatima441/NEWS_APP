package com.example.arslan;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;

public class NewsDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);

        Toolbar toolbar = findViewById(R.id.detailToolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }

        ImageView imageView = findViewById(R.id.detailImage);
        TextView titleView = findViewById(R.id.detailTitle);
        TextView categoryView = findViewById(R.id.detailCategory);
        TextView descView = findViewById(R.id.detailDescription);

        // Get data from Intent
        String title = getIntent().getStringExtra("title");
        String content = getIntent().getStringExtra("content"); // Using full content
        String category = getIntent().getStringExtra("category");
        String imageUrl = getIntent().getStringExtra("image");

        titleView.setText(title);
        // Show full content if available, otherwise show description
        descView.setText(content != null && !content.isEmpty() ? content : getIntent().getStringExtra("desc"));
        categoryView.setText(category);

        Glide.with(this)
                .load(imageUrl)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(imageView);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
