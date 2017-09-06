package com.mad.connect.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
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
import com.mad.connect.Entities.User;
import com.mad.connect.Home;
import com.mad.connect.R;
import com.squareup.picasso.Picasso;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.ArrayList;

/**
 * Created by pushparajparab on 10/18/16.
 */
public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.UserHolder>{

    private ArrayList<Message> allMessages;
    private LayoutInflater inflater;
    private int recourseID;
    private Context mContext;

    DatabaseReference mRoot  = FirebaseDatabase.getInstance().getReference();
    User LOGGED_IN_USER;
    DatabaseReference  mConditionRef ;



    private ItemClickCallBack itemClickCallBack;

    public interface ItemClickCallBack
    {
        void OnItemClick(Message selectedMessage);
        void OnItemMessageDeleteClick(Message toDelete);
      //  void OnItemCommentsClick(int p);
        //if next method.
    }


    public MessageAdapter(ArrayList<Message> allMessages, Context context, int recourseId)
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
        final Message curMsg = allMessages.get(position);
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
                if(!curMsg.isRead()) {

                    get.fullName.setTypeface(null, Typeface.BOLD);
                    get.time.setTextColor(Color.parseColor("#2BC0E4"));

                }

                if(!curMsg.isImage())
                {
                    get.Attach.setVisibility(View.INVISIBLE);
                }else
                {
                    get.Attach.setVisibility(View.VISIBLE);
                }
                char firstChar = user.getFirstName().charAt(0);
                get.initials.setText(String.valueOf(user.getFirstName().charAt(0)).toUpperCase());


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
        private TextView initials;
        ImageView delete,Attach;

        private View container;



        public UserHolder(View itemView) {
            super(itemView);

            fullName = (TextView) itemView.findViewById(R.id.textViewSender);
            messageText = (TextView) itemView.findViewById(R.id.textViewMessageText);
            time = (TextView) itemView.findViewById(R.id.textViewTime);
            initials = (TextView)itemView.findViewById(R.id.TVInitials);

            delete = (ImageView) itemView.findViewById(R.id.imageViewDelete);
            Attach = (ImageView) itemView.findViewById(R.id.imageViewAttach);
            container = itemView.findViewById(R.id.eachMessageRoot);

            container.setOnClickListener(this);
            delete.setOnClickListener(this);


        }

        @Override
        public void onClick(View v) {
            switch (v.getId())
            {
                case R.id.eachMessageRoot:

                    itemClickCallBack.OnItemClick(allMessages.get(getAdapterPosition()));
                    break;

                case R.id.imageViewDelete:

                    itemClickCallBack.OnItemMessageDeleteClick(allMessages.get(getAdapterPosition()));
                    allMessages.remove(allMessages.get(getAdapterPosition()));
                    break;

            }
        }
    }

}
