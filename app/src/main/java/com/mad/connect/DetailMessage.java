package com.mad.connect;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.mad.connect.Adapters.MessageAdapter;
import com.mad.connect.Adapters.MessageDetailAdapter;
import com.mad.connect.Entities.Message;
import com.mad.connect.Entities.MessageContent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class DetailMessage extends Fragment {

    private OnFragmentInteractionListener mListener;

    private RecyclerView recyclerView;
    private MessageDetailAdapter messageAdapter;
    private ArrayList<MessageContent> allMessages = new ArrayList<MessageContent>();
    private LinearLayout reply;

    private ArrayList<MessageContent> allMessagesSent;
    private  Message mMessage;

    private FirebaseUser user;
    TextView msgFrom ;
    DatabaseReference mRoot  = FirebaseDatabase.getInstance().getReference();
    DatabaseReference  mConditionRef ;

    public void SetDetailsObj(ArrayList<MessageContent> Sent,Message message)
    {



        this.allMessagesSent = Sent;
        this.mMessage = message;
    }


    public DetailMessage() {
        // Required empty public constructor
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        recyclerView = (RecyclerView)getActivity().findViewById(R.id.MessageDetailsView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false));

        user = FirebaseAuth.getInstance().getCurrentUser();

        msgFrom = (TextView)getActivity().findViewById(R.id.textViewMsgfrom);


        Collections.sort(allMessagesSent, new Comparator<MessageContent>() {
            @Override
            public int compare(MessageContent lhs, MessageContent rhs) {


                if (lhs.getSendTime().compareTo( rhs.getSendTime()) > 0){
                    return -1;
                }
                if (lhs.getSendTime().compareTo( rhs.getSendTime()) < 0){
                    return 1;
                }
                return 0;

            }


        });

        messageAdapter = new MessageDetailAdapter(allMessagesSent,getActivity(),R.layout.item_detail_message);
        messageAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(messageAdapter);
        messageAdapter.notifyDataSetChanged();


        final String key = mRoot.child("Messages").push().getKey();
        mMessage.setRead(true);
        mRoot.child("Messages").child(user.getUid()).child("Received").child(mMessage.getKey()).setValue(mMessage);
        mListener.refreshCount();

        reply = (LinearLayout)getActivity().findViewById(R.id.ReplyView);
        reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onFragmentInteractionOfDetailMessage(mMessage);
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detail_message, container, false);
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteractionOfDetailMessage(Message toReply);
        void refreshCount();
    }
}
