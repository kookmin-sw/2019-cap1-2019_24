package com.example.WITTYPHOTOS;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.WITTYPHOTOS.controller.ImgPickerDetailController;
import com.example.WITTYPHOTOS.controller.OnQueryListener;

import java.util.ArrayList;


public class ImagePickerDetailActivity extends AppCompatActivity implements OnQueryListener<ArrayList<PickerImage>> {

    private ImagePickerAdapter adapter;

    private RecyclerView recyclerView;

    private ImgPickerDetailController mController;

    private long bucketId;

    private SearchView searchView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imagepickerdetail);
        bucketId = getIntent().getLongExtra("id", 0);
        invalidateOptionsMenu();

        recyclerView = findViewById(R.id.recyclerview);
        adapter = new ImagePickerAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        mController = new ImgPickerDetailController(this, this);
        mController.loadImages(bucketId);

    }

    @Override
    public void onComplete(ArrayList<PickerImage> data) {
        adapter.init(data);

    }

    public class ImagePickerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        public ArrayList<PickerImage> data;
        public ArrayList<PickerImage> localData;
        public ArrayList<PickerImage> queryData;

        public ImagePickerAdapter() {
            data = new ArrayList<>();
        }

        public void init(@NonNull ArrayList<PickerImage> item) {
            data = new ArrayList<>();
            localData = new ArrayList<>();
            queryData = new ArrayList<>();
            data.addAll(item);
            localData.addAll(item);
            notifyDataSetChanged();
        }

        public void query(@NonNull ArrayList<PickerImage> item) {
            data.clear();
            if (item == null || item.size() == 0) {
                data.addAll(localData);
            } else {
                data.addAll(item);
            }
            notifyDataSetChanged();
        }

        public PickerImage getItem(int position) {
            return data.get(position);
        }


        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.item_imagedetail, parent, false);
            return new ImageDetailViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ((ImageDetailViewHolder) holder).onBindViewHolder(data.get(position));

        }

        @Override
        public int getItemCount() {
            return data.size();
        }
    }


    public class ImageDetailViewHolder extends RecyclerView.ViewHolder {

        public ImageView imgThumb;


        public ImageDetailViewHolder(View itemView) {
            super(itemView);
            imgThumb = itemView.findViewById(R.id.imagethumb);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int position = getLayoutPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        PickerImage pickerImage = adapter.getItem(position);
                        Intent intent = new Intent(ImagePickerDetailActivity.this, TagAddActivity.class);
                        intent.putExtra("item", pickerImage);
                        startActivity(intent);
                    }

                }


            });

        }

        public void onBindViewHolder(PickerImage image) {
            Glide.with(ImagePickerDetailActivity.this).load(image.imgPath).into(imgThumb);

        }

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem menuItem = menu.findItem(R.id.action_search);
        menuItem.setVisible(bucketId == 0);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);

        MenuItem searchMenu = menu.findItem(R.id.action_search);

        searchView = (SearchView) searchMenu.getActionView();
        searchView.setSubmitButtonEnabled(true);

        //SearchView의 검색 이벤트
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            //검색버튼을 눌렀을 경우
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            //텍스트가 바뀔때마다 호출
            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() > 0) {
                    String[] tags = newText.split(" ");
                    if (tags.length <= 5) {
                        ArrayList<PickerImage> pickerImages = App.mDB.selectMultiTag(tags);
                        adapter.query(pickerImages);
                    } else {
                        Toast.makeText(ImagePickerDetailActivity.this, "최대 5개까지 입니다", Toast.LENGTH_LONG).show();
                    }
                } else {
                    adapter.query(null);
                }
                return true;
            }
        });
        return true;
    }
}
