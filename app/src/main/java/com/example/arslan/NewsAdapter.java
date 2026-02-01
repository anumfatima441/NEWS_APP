package com.example.arslan;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {

    private List<News> newsList;

    public NewsAdapter(List<News> newsList) {
        this.newsList = newsList;
    }

    public void setNewsList(List<News> newsList) {
        this.newsList = newsList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_news, parent, false);
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        News news = newsList.get(position);
        holder.titleTextView.setText(news.getTitle());
        holder.descriptionTextView.setText(news.getDescription());
        holder.categoryTextView.setText(news.getCategory());
        
        holder.playIcon.setVisibility(View.GONE);

        // Improved Glide loading with error handling and caching
        if (news.getImageUrl() != null && !news.getImageUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                .load(news.getImageUrl())
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.stat_notify_error) // Fallback if image fails
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .into(holder.imageView);
        } else if (news.getImageResource() != 0) {
            holder.imageView.setImageResource(news.getImageResource());
        } else {
            holder.imageView.setImageResource(android.R.drawable.ic_menu_gallery);
        }

        holder.btnShare.setOnClickListener(v -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            String shareMessage = news.getTitle() + "\n\nRead more at: " + news.getNewsUrl();
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            v.getContext().startActivity(Intent.createChooser(shareIntent, "Share News via"));
        });

        holder.btnCopy.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) v.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("News Link", news.getNewsUrl());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(v.getContext(), "Link Copied!", Toast.LENGTH_SHORT).show();
        });

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), NewsDetailActivity.class);
            intent.putExtra("title", news.getTitle());
            intent.putExtra("desc", news.getDescription());
            intent.putExtra("content", news.getContent());
            intent.putExtra("category", news.getCategory());
            intent.putExtra("image", news.getImageUrl());
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    static class NewsViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView, playIcon;
        TextView titleTextView, descriptionTextView, categoryTextView;
        ImageButton btnShare, btnCopy;

        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.newsImage);
            playIcon = itemView.findViewById(R.id.playIcon);
            titleTextView = itemView.findViewById(R.id.newsTitle);
            descriptionTextView = itemView.findViewById(R.id.newsDescription);
            categoryTextView = itemView.findViewById(R.id.newsCategory);
            btnShare = itemView.findViewById(R.id.btnShare);
            btnCopy = itemView.findViewById(R.id.btnCopy);
        }
    }
}
