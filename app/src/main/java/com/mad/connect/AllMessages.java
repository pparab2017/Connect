package com.mad.connect;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.mad.connect.Adapters.MessageAdapter;
import com.mad.connect.Adapters.UserAdapter;
import com.mad.connect.Entities.Message;
import com.mad.connect.Entities.User;

import java.util.ArrayList;


public class AllMessages extends Fragment {


    private OnFragmentInteractionListener mListener;

    private RecyclerView recyclerView;
    private MessageAdapter messageAdapter;
    private ArrayList<Message> allMessages = new ArrayList<Message>();

    private FirebaseUser user;
    DatabaseReference mRoot  = FirebaseDatabase.getInstance().getReference();
    DatabaseReference  mConditionRef ;
    LinearLayout noMsgDiv ;
    public AllMessages() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        allMessages = new ArrayList<Message>();
        recyclerView = (RecyclerView)getActivity().findViewById(R.id.MessageView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false));
        noMsgDiv = (LinearLayout)getActivity().findViewById(R.id.noMsgDiv);

        user = FirebaseAuth.getInstance().getCurrentUser();

        messageAdapter = new MessageAdapter(allMessages,getActivity(),R.layout.item_message);
        messageAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(messageAdapter);


        mConditionRef = mRoot.child("Messages").child(user.getUid()).child("Received");

        Query recentPostsQuery = mConditionRef
              .orderByChild("read");

        recentPostsQuery.addChildEventListener(new ChildEventListener() {


            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Message toadd = dataSnapshot.getValue(Message.class);


                allMessages.add(toadd);

             //   Log.d("XXXXXX",allMessages.toString());

                messageAdapter.notifyDataSetChanged();
if(allMessages.size()!=0)
{
    noMsgDiv.setVisibility(View.GONE);
}
                else {
 noMsgDiv.setVisibility(View.VISIBLE);

}


            }



            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                // allUsers.re(toadd);


            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                /*Expense toremove = dataSnapshot.getValue(Expense.class);
                MyList.remove(toremove);
                adapter.notifyDataSetChanged();*/
                Message toDelete = dataSnapshot.getValue(Message.class);
                Log.d("Removeddddddd", "onChildRemoved: " + toDelete.getMessage() );

               if( allMessages.contains(toDelete))
                {//(toDelete);
                    Log.d("RemovedddddddYess", "onChildRemoved: " + toDelete.getMessage() );
                }

                //   Log.d("XXXXXX",allMessages.toString());



                messageAdapter.notifyDataSetChanged();

                if(allMessages.size()!=0)
                {
                    noMsgDiv.setVisibility(View.GONE);
                }
                else {
                    noMsgDiv.setVisibility(View.VISIBLE);

                }

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_all_messages, container, false);
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
        void onMessageSelectFragmentInteraction(Uri uri);
        void refreshTheCount();
    }
}
