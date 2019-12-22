package com.bulingo.Exercises;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.bulingo.Chat.RecyclerItemClickListener;
import com.bulingo.Database.APICLient;
import com.bulingo.Database.APIInterface;
import com.bulingo.Database.Writing;
import com.bulingo.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SendReceiveWriting extends AppCompatActivity {
    APIInterface apiInterface = APICLient.getClient(this).create(APIInterface.class);
    String username;
    String action;
    WritingRecyclerViewAdapter adapter;
    RecyclerView recyclerView;
    List<Writing> writings = new ArrayList<>();
    private SwipeRefreshLayout mSwipeRefreshLayout;
    boolean isSent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_receive_writing);
        username = getIntent().getStringExtra("username");
        action = getIntent().getStringExtra("action");
        isSent = action.equals("sent");
        Toolbar toolbar = (Toolbar) findViewById(R.id.appbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        recyclerView = findViewById(R.id.writingRecycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new WritingRecyclerViewAdapter(writings, getApplicationContext(), isSent);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, recyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                    }
                    @Override public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );

        mSwipeRefreshLayout = findViewById(R.id.swipe);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            if (isSent) getSentWritings();
            else getReceivedWritings();
            final Handler handler = new Handler();
            handler.postDelayed(() -> {
                if(mSwipeRefreshLayout.isRefreshing()) {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }, 500);
        });
        TextView title = findViewById(R.id.title);
        if(isSent) {
            getSentWritings();
            title.setText("Sent Writings");
        } else {
            getReceivedWritings();
            title.setText("Received Writings");
        }
    }

    private void getReceivedWritings() {
        Call<List<Writing>> responseCall = apiInterface.doGetReceivedWriting(username);

        responseCall.enqueue(new Callback<List<Writing>>() {
            @Override
            public void onResponse(Call<List<Writing>> call, Response<List<Writing>> response) {
                writings.clear();
                writings.addAll(response.body());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<Writing>> call, Throwable t) {
                Log.d("request", t.toString());
            }

        });
    }

    public void getSentWritings() {

        Call<List<Writing>> responseCall = apiInterface.doGetSentWriting(username);

        responseCall.enqueue(new Callback<List<Writing>>() {
            @Override
            public void onResponse(Call<List<Writing>> call, Response<List<Writing>> response) {
                writings.clear();
                writings.addAll(response.body());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<Writing>> call, Throwable t) {
                Log.d("request", t.toString());
            }

        });
    }
}
