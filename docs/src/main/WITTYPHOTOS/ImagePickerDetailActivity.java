package com.example.WITTYPHOTOS;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.WITTYPHOTOS.controller.ImgPickerDetailController;
import com.example.WITTYPHOTOS.controller.ImgPickerDirController;
import com.example.WITTYPHOTOS.controller.OnQueryListener;

import java.util.ArrayList;


public class ImagePickerDetailActivity extends AppCompatActivity implements OnQueryListener<ArrayList<PickerImage>> {

    private ImagePickerAdapter adapter;

    private RecyclerView recyclerView;

    private ImgPickerDetailController mController;

    private long bucketId ;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imagepickerdetail);
        bucketId = getIntent().getLongExtra("id", 0);

        recyclerView = findViewById(R.id.recyclerview);
        adapter = new ImagePickerAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this,3));
        mController = new ImgPickerDetailController(this, this);
        mController.loadImages(bucketId);

    }



    @Override
    public void onComplete(ArrayList<PickerImage> data) {
        adapter.init(data);

    }

    public class ImagePickerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        public ArrayList<PickerImage> data;

        public ImagePickerAdapter() {
            data = new ArrayList<>();
        }

        public void init (@NonNull ArrayList<PickerImage> item){
            data = new ArrayList<>();
            data.addAll(item);
            notifyDataSetChanged();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.item_imagedetail, parent,false);
            return new ImageDetailViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ( (ImageDetailViewHolder) holder ).onBindViewHolder(data.get(position));

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

        }
        public void onBindViewHolder(PickerImage image) {
            Glide.with(ImagePickerDetailActivity.this).load(image.imgPath).into(imgThumb);


        }

    }
}
