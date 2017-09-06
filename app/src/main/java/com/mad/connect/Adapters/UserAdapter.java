package com.mad.connect.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mad.connect.CircleTransform;
import com.mad.connect.Entities.User;
import com.mad.connect.R;
import com.squareup.picasso.Picasso;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by pushparajparab on 10/18/16.
 */
public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserHolder>{

    private ArrayList<User> allUsers;
    private LayoutInflater inflater;
    private int recourseID;
    private Context mContext;
    private  String userid;


    private ItemClickCallBack itemClickCallBack;

    public interface ItemClickCallBack
    {
        void OnItemClick(User selectedUser);
        void OnItemMessageIconClick(User selectedUser);
        //if next method.
    }


    public UserAdapter(ArrayList<User> weathersData, Context context, int recourseId, String userID)
    {
        this.inflater = LayoutInflater.from(context);
        this.allUsers = weathersData;
        this.recourseID = recourseId;
        this.mContext = context;
        this.userid = userID;
        this.itemClickCallBack = (ItemClickCallBack) context;

    }

    @Override
    public UserHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(this.recourseID,parent,false);
        return new UserHolder(view);
    }

    @Override
    public void onBindViewHolder(UserHolder holder, int position) {
        User curUser = allUsers.get(position);



        PrettyTime p2 = new PrettyTime();




    holder.fullName.setText(curUser.getFullName());
        Picasso.with(mContext).load(curUser.CheckAndGetDpUrl()).placeholder(curUser.isGender() ? R.drawable.u_female :R.drawable.u_male)
                .transform(new CircleTransform()).into(holder.userIcon);






    }

    @Override
    public int getItemCount() {
        return allUsers.size();
    }





    class UserHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private TextView fullName;
        private ImageView userIcon,msgIcon;
        private View container;



        public UserHolder(View itemView) {
            super(itemView);

            fullName = (TextView) itemView.findViewById(R.id.Name_TextView);
            userIcon = (ImageView)itemView.findViewById(R.id.imageViewUserIcon);
            msgIcon = (ImageView) itemView.findViewById(R.id.imageViewMsgIcon) ;

            container = itemView.findViewById(R.id.eachUserRoot);

            container.setOnClickListener(this);
            msgIcon.setOnClickListener(this);


        }

        @Override
        public void onClick(View v) {
            switch (v.getId())
            {
                case R.id.eachUserRoot:

                    itemClickCallBack.OnItemClick(allUsers.get(getAdapterPosition()));
                    break;
                case R.id.imageViewMsgIcon:

                    itemClickCallBack.OnItemMessageIconClick(allUsers.get(getAdapterPosition()));
                    break;
//                case R.id.commentIcon:
//                    itemClickCallBack.OnItemCommentsClick(getAdapterPosition());
//                    break;

            }
        }
    }

}
