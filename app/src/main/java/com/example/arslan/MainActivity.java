package com.example.arslan;

import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SearchView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private NewsAdapter adapter;
    private List<News> allFetchedNews = new ArrayList<>();
    private TabLayout tabLayout;
    private SearchView searchView;
    private ImageButton btnTheme;
    private SwipeRefreshLayout swipeRefresh;
    private boolean isDarkMode = false;
    private String currentCategory = "general";
    
    private static final String API_KEY = "64c8680190534c0389369324505373a0"; 
    private static final String BASE_URL = "https://newsapi.org/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerView = findViewById(R.id.recyclerView);
        tabLayout = findViewById(R.id.tabLayout);
        searchView = findViewById(R.id.searchView);
        btnTheme = findViewById(R.id.btnTheme);
        swipeRefresh = findViewById(R.id.swipeRefresh);

        setupRecyclerView();
        setupTabs();
        setupSearch();
        setupThemeToggle();
        
        swipeRefresh.setOnRefreshListener(() -> {
            fetchNews(currentCategory);
            new Handler().postDelayed(() -> swipeRefresh.setRefreshing(false), 1500);
        });

        fetchNews(currentCategory);
    }

    private void setupThemeToggle() {
        btnTheme.setOnClickListener(v -> {
            if (isDarkMode) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                isDarkMode = false;
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                isDarkMode = true;
            }
        });
    }

    private void setupRecyclerView() {
        adapter = new NewsAdapter(new ArrayList<>());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void setupTabs() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getText() != null) {
                    String category = tab.getText().toString().toLowerCase();
                    if (category.equals("all")) category = "general";
                    if (category.equals("political")) category = "politics";
                    currentCategory = category;
                    fetchNews(category);
                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void setupSearch() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterNews(query);
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                filterNews(newText);
                return true;
            }
        });
    }

    private void filterNews(String query) {
        List<News> filteredList = new ArrayList<>();
        for (News news : allFetchedNews) {
            if (news.getTitle().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(news);
            }
        }
        adapter.setNewsList(filteredList);
    }

    private void fetchNews(String category) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        NewsApiService apiService = retrofit.create(NewsApiService.class);
        
        String apiCategory = category;
        if (category.equals("politics")) apiCategory = "general"; 
        if (category.equals("education")) apiCategory = "science";

        apiService.getTopHeadlines("us", apiCategory, API_KEY).enqueue(new Callback<NewsResponse>() {
            @Override
            public void onResponse(@NonNull Call<NewsResponse> call, @NonNull Response<NewsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Article> articles = response.body().getArticles();
                    allFetchedNews = new ArrayList<>();
                    if (articles != null) {
                        for (Article article : articles) {
                            allFetchedNews.add(new News(
                                    article.getTitle(),
                                    article.getDescription() != null ? article.getDescription() : "Tap to read more.",
                                    article.getDescription() != null ? article.getDescription() + "\n\nFull article content would go here from the API source." : "No content available.",
                                    category.toUpperCase(),
                                    article.getUrlToImage(),
                                    article.getUrl()
                            ));
                        }
                        adapter.setNewsList(allFetchedNews);
                    }
                } else {
                    loadFallbackData(category);
                }
            }

            @Override
            public void onFailure(@NonNull Call<NewsResponse> call, @NonNull Throwable t) {
                loadFallbackData(category);
            }
        });
    }

    private void loadFallbackData(String category) {
        allFetchedNews = new ArrayList<>();
        String aryUrl = "https://arynews.tv/";

        // Fixed image URLs from reliable sources
        if (category.contains("politic") || category.equals("general")) {
            allFetchedNews.add(new News("Pakistan Vision 2026: Economic Corridors Expansion", "The government has announced a massive expansion of economic zones by 2026.", "Full content about vision 2026 economic expansion.", "POLITICAL", "https://images.pexels.com/photos/3183197/pexels-photo-3183197.jpeg?auto=compress&cs=tinysrgb&w=800", aryUrl));
            allFetchedNews.add(new News("Elections 2026: New Digital Voting System", "ECP prepares for the first-ever digital voting system in the upcoming 2026 local elections.", "Full content about digital voting system in Pakistan.", "POLITICAL", "https://images.pexels.com/photos/1550337/pexels-photo-1550337.jpeg?auto=compress&cs=tinysrgb&w=800", aryUrl));
            allFetchedNews.add(new News("Green Pakistan 2026: 10 Billion Tree Success", "Pakistan achieves milestone in environmental protection with record tree plantation.", "Full content about tree plantation project success.", "POLITICAL", "https://images.pexels.com/photos/1072824/pexels-photo-1072824.jpeg?auto=compress&cs=tinysrgb&w=800", aryUrl));
            allFetchedNews.add(new News("Islamabad Smart City Phase 2", "CDA initiates second phase of the smart city project for 2026.", "Full content about smart city Islamabad.", "POLITICAL", "https://images.pexels.com/photos/313782/pexels-photo-313782.jpeg?auto=compress&cs=tinysrgb&w=800", aryUrl));
            allFetchedNews.add(new News("Gwadar Port Capacity Doubled", "New terminals operational at Gwadar International Port in 2026.", "Full content about Gwadar port expansion.", "POLITICAL", "https://images.pexels.com/photos/2090645/pexels-photo-2090645.jpeg?auto=compress&cs=tinysrgb&w=800", aryUrl));
        }
        
        if (category.contains("educat") || category.equals("general") || category.equals("science")) {
            allFetchedNews.add(new News("First AI University Islamabad", "HEC confirms opening of specialized AI and Robotics university in 2026.", "Full content about AI university.", "EDUCATION", "https://images.pexels.com/photos/373543/pexels-photo-373543.jpeg?auto=compress&cs=tinysrgb&w=800", aryUrl));
            allFetchedNews.add(new News("Digital Literacy 2026 Goal", "Over 5 million students in rural areas provided with tablets.", "Full content about digital literacy program.", "EDUCATION", "https://images.pexels.com/photos/1337380/pexels-photo-1337380.jpeg?auto=compress&cs=tinysrgb&w=800", aryUrl));
            allFetchedNews.add(new News("Space Mission 2026: Moon Rover", "SUPARCO announces plans for a lunar rover in 2026.", "Full content about Pakistan moon mission.", "SCIENCE", "https://images.pexels.com/photos/2150/sky-space-milky-way-stars.jpg?auto=compress&cs=tinysrgb&w=800", aryUrl));
            allFetchedNews.add(new News("5G Network Nationwide Rollout", "Pakistan completes its 5G network coverage in all major cities by 2026.", "Full content about 5G in Pakistan.", "SCIENCE", "https://images.pexels.com/photos/2582937/pexels-photo-2582937.jpeg?auto=compress&cs=tinysrgb&w=800", aryUrl));
        }

        if (category.contains("sport") || category.equals("general")) {
            allFetchedNews.add(new News("World Cup 2026: Pakistan Training", "The national football team begins training for 2026 qualifiers.", "Full content about football training.", "SPORTS", "https://images.pexels.com/photos/46798/the-ball-stadion-football-the-pitch-46798.jpeg?auto=compress&cs=tinysrgb&w=800", aryUrl));
            allFetchedNews.add(new News("Champions Trophy 2026 Prep", "PCB renovates stadiums for the upcoming global cricket event.", "Full content about Champions Trophy prep.", "SPORTS", "https://images.pexels.com/photos/3651674/pexels-photo-3651674.jpeg?auto=compress&cs=tinysrgb&w=800", aryUrl));
            allFetchedNews.add(new News("PSL 2026: Expansion to 8 Teams", "Gilgit and Faisalabad franchises join the PSL in 2026.", "Full content about PSL expansion.", "SPORTS", "https://images.pexels.com/photos/163271/cricket-sports-match-sport-163271.jpeg?auto=compress&cs=tinysrgb&w=800", aryUrl));
        }

        adapter.setNewsList(allFetchedNews);
    }
}
