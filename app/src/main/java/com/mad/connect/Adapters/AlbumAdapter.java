package com.mad.connect.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mad.connect.CircleTransform;
import com.mad.connect.Entities.Photos;

import com.mad.connect.R;
import com.squareup.picasso.Picasso;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.ArrayList;

/**
 * Created by pushparajparab on 10/18/16.
 */
public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.PhotoHolder>{

    private ArrayList<Photos> allPhotos;
    private LayoutInflater inflater;
    private int recourseID;
    private Context mContext;
    private  String userid;


    private ItemClickCallBack itemClickCallBack;

    public interface ItemClickCallBack
    {
        void OnItemClick(Photos selectedPhoto);

    }


    public AlbumAdapter(ArrayList<Photos> AlbumData, Context context, int recourseId, String userID)
    {
        this.inflater = LayoutInflater.from(context);
        this.allPhotos = AlbumData;
        this.recourseID = recourseId;
        this.mContext = context;
        this.userid = userID;
        this.itemClickCallBack = (ItemClickCallBack) context;

    }

    @Override
    public PhotoHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(this.recourseID,parent,false);
        return new PhotoHolder(view);
    }

    @Override
    public void onBindViewHolder(PhotoHolder holder, int position) {
        Photos curPhoto = allPhotos.get(position);



        PrettyTime p2 = new PrettyTime();




        holder.time.setText(p2.format( curPhoto.getDate()));
        Picasso.with(mContext).load(curPhoto.getPhotoUrl()).placeholder(R.mipmap.ic_launcher)
               .into(holder.photo);






    }

    @Override
    public int getItemCount() {
        return allPhotos.size();
    }





    class PhotoHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private TextView time;
        private ImageView photo;
        private View container;



        public PhotoHolder(View itemView) {
            super(itemView);

            time = (TextView) itemView.findViewById(R.id.textViewWhen);
            photo = (ImageView)itemView.findViewById(R.id.imageViewMyPhoto);
            container = itemView.findViewById(R.id.eachPhotoRoot);

            container.setOnClickListener(this);


        }

        @Override
        public void onClick(View v) {
            switch (v.getId())
            {
                case R.id.eachUserRoot:

                    itemClickCallBack.OnItemClick(allPhotos.get(getAdapterPosition()));
                    break;

//                case R.id.commentIcon:
//                    itemClickCallBack.OnItemCommentsClick(getAdapterPosition());
//                    break;

            }
        }
    }

}
