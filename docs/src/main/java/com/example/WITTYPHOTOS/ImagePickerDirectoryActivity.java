package com.example.WITTYPHOTOS;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class ImagePickerDirectoryActivity extends AppCompatActivity {

    private ImagePickerAdapter adapter;

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imagepicker_dir);

        recyclerView = findViewById(R.id.recyclerview);
        adapter = new ImagePickerAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this,3));
        adapter.init(DummyManager.getDirList());

    }
    public class ImagePickerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        public ArrayList<PickerDir> data;

        public ImagePickerAdapter() {
            data = new ArrayList<>();
        }

        public void init (@NonNull ArrayList<PickerDir> item){
            data = new ArrayList<>();
            data.addAll(item);
            notifyDataSetChanged();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.item_imagepickerdir,parent,false);
            return new ImageDirViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ( (ImageDirViewHolder) holder ).onBindViewHolder(data.get(position));

        }

        @Override
        public int getItemCount() {
            return data.size();
        }
    }

    public class ImageDirViewHolder extends RecyclerView.ViewHolder {

        public ImageView imgThumb;
        public TextView txtDirName;


        public ImageDirViewHolder(View itemView) {
            super(itemView);
            imgThumb = itemView.findViewById(R.id.imgThumb);
            txtDirName = itemView.findViewById(R.id.txtDirName);
        }
        public void onBindViewHolder(PickerDir dir) {
            imgThumb.setImageResource(dir.imgResId);
            txtDirName.setText(dir.dirName);

        }

    }
}

