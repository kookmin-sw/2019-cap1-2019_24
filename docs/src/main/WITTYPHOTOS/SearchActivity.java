package com.example.WITTYPHOTOS;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;

public class SearchActivity extends AppCompatActivity {

    private NoticeRecyclerAdapter mAdapter;
    private NoticeContent mNoticeContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imagepickerdetail);
    }


    private void initView() {
        getIntentDate();
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.notice_search_recylcer);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new NoticeRecylcerAdapter (this, mNoticeContent.getNoticeList());
        recyclerView.setAdapter(mAdapter);
    }

    private void getIntentDate() {
        Intent intent = getIntent();
        mNoticeContent = (NoticeContent)intent.getExtras().getSerializable(NoticeContent.NOTICE_CONTENT);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        SearchManager searchManager = (SearchManager) this.getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) findViewById(R.id.notice_search_view);
        searchView.onActionViewExpanded();

        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(this.getComponentName()));

        }
    }
}
