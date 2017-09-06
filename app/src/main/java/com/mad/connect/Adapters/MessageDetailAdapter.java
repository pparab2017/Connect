package com.mad.connect.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mad.connect.CircleTransform;
import com.mad.connect.Entities.Message;
import com.mad.connect.Entities.MessageContent;
import com.mad.connect.Entities.User;
import com.mad.connect.R;
import com.squareup.picasso.Picasso;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.ArrayList;

/**
 * Created by pushparajparab on 10/18/16.
 */
public class MessageDetailAdapter extends RecyclerView.Adapter<MessageDetailAdapter.UserHolder>{

    private ArrayList<MessageContent> allMessages;
    private LayoutInflater inflater;
    private int recourseID;
    private Context mContext;

    DatabaseReference mRoot  = FirebaseDatabase.getInstance().getReference();
    User LOGGED_IN_USER;
    DatabaseReference  mConditionRef ;



    private ItemClickCallBack itemClickCallBack;

    public interface ItemClickCallBack
    {

        //if next method.
    }


    public MessageDetailAdapter(ArrayList<MessageContent> allMessages, Context context, int recourseId)
    {
        this.inflater = LayoutInflater.from(context);
        this.allMessages = allMessages;
        this.recourseID = recourseId;
        this.mContext = context;

        this.itemClickCallBack = (ItemClickCallBack) context;

    }

    @Override
    public UserHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(this.recourseID,parent,false);
        return new UserHolder(view);
    }

    @Override
    public void onBindViewHolder(UserHolder holder, int position) {
        final MessageContent curMsg = allMessages.get(position);
        final UserHolder get = holder;
        final PrettyTime p2 = new PrettyTime();
      //  holder.fullName.setText(curMsg.getFullName());

        mConditionRef = mRoot.child("Users").child(curMsg.getSender());
        mConditionRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                get.fullName.setText(user.getFullName());
                get.messageText.setText(curMsg.getMessage());
                get.time.setText(p2.format( curMsg.getSendTime()));



                Picasso.with(mContext).load(user.CheckAndGetDpUrl()).placeholder(user.isGender() ? R.drawable.u_female :R.drawable.u_male)
                        .transform(new CircleTransform()).into(get.userIcon);

                if(curMsg.getImgURL().equals(""))
                {
                    get.messageIcon.setVisibility(View.GONE);
                }
                else
                {
                    get.messageIcon.setVisibility(View.VISIBLE);
                    Picasso.with(mContext).load(curMsg.getImgURL())
                           .into(get.messageIcon);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });







    }

    @Override
    public int getItemCount() {
        return allMessages.size();
    }





    class UserHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private TextView fullName;
        private TextView messageText;
        private TextView time;

        ImageView userIcon,messageIcon;

        private View container;



        public UserHolder(View itemView) {
            super(itemView);

            fullName = (TextView) itemView.findViewById(R.id.textViewSenderInDetail);
            messageText = (TextView) itemView.findViewById(R.id.textViewMessageTextInDetails);
            time = (TextView) itemView.findViewById(R.id.textViewSentTimeInDetails);

            userIcon = (ImageView) itemView.findViewById(R.id.imageViewUserIconInDetail);

            messageIcon = (ImageView) itemView.findViewById(R.id.imageViewMessageInDetails);


           // container.setOnClickListener(this);


        }

        @Override
        public void onClick(View v) {
            switch (v.getId())
            {
            }
        }
    }

}
