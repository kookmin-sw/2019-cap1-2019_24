package com.example.WITTYPHOTOS;

import android.content.Intent;
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

import com.bumptech.glide.Glide;
import com.example.WITTYPHOTOS.controller.ImgPickerDirController;
import com.example.WITTYPHOTOS.controller.OnQueryListener;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class ImagePickerDirectoryActivity extends AppCompatActivity
        implements OnQueryListener<ArrayList<PickerDir>> {

    private ImagePickerAdapter adapter;

    private RecyclerView recyclerView;

    private ImgPickerDirController mController;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imagepicker_dir);

        recyclerView = findViewById(R.id.recyclerview);
        adapter = new ImagePickerAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mController = new ImgPickerDirController(this, this);
        mController.loadDirList();


    }

    @Override
    public void onComplete(ArrayList<PickerDir> data) {
        adapter.init(data);

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

        public PickerDir getItem(int position) {

            return data.get(position);
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
        public TextView txtCounter;


        public ImageDirViewHolder(View itemView) {
            super(itemView);
            imgThumb = itemView.findViewById(R.id.imgThumb);
            txtDirName = itemView.findViewById(R.id.txtDirName);
            txtCounter = itemView.findViewById(R.id.txtCounter);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                   int position = getLayoutPosition();
                   if (position != RecyclerView.NO_POSITION) {
                       PickerDir pickerDir = adapter.getItem(position);
                       Intent intent = new Intent(ImagePickerDirectoryActivity.this, ImagePickerDetailActivity.class);
                       intent.putExtra("id", pickerDir.dirId);
                       startActivity(intent);

                   }
                }
            });
        }

        public void onBindViewHolder(PickerDir dir) {
            Glide.with(ImagePickerDirectoryActivity.this).load(dir.imgPath).into(imgThumb);
            //Glide.with(ImagePickerDirectoryActivity.this).load("https://pds.joins.com/news/component/newsis/201804/14/NISI20171229_0013676796_web.jpg").into(imgThumb);
            txtDirName.setText(dir.dirName);
            txtCounter.setText(String.valueOf(dir.count));

        }

    }

}

