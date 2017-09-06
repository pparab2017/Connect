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
import com.mad.connect.Adapters.UserAdapter;
import com.mad.connect.Entities.User;

import java.util.ArrayList;


public class Users extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private ArrayList<User> allUsers = new ArrayList<User>();

    private FirebaseUser user;
    DatabaseReference mRoot  = FirebaseDatabase.getInstance().getReference();
    DatabaseReference  mConditionRef ;
    LinearLayout noUserDiv ;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;



    public Users() {

    }


    public static Users newInstance(String param1, String param2) {
        Users fragment = new Users();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        allUsers = new ArrayList<User>();
        recyclerView = (RecyclerView)getActivity().findViewById(R.id.UsersView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false));

        user = FirebaseAuth.getInstance().getCurrentUser();

        userAdapter = new UserAdapter(allUsers,getActivity(),R.layout.item_user,user.getUid());
        userAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(userAdapter);
        noUserDiv = (LinearLayout)getActivity().findViewById(R.id.NoUsersDiv);

        mConditionRef = mRoot.child("Users");

        mConditionRef.addChildEventListener(new ChildEventListener() {


            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                User toadd = dataSnapshot.getValue(User.class);

                if(!toadd.getUserID().equals(user.getUid()) ) {
                    allUsers.add(toadd);
                }
                Log.d("XXXXXX",allUsers.toString());
                userAdapter.notifyDataSetChanged();



                if(allUsers.size()!=0)
                {
                    noUserDiv.setVisibility(View.GONE);
                }
                else {
                    noUserDiv.setVisibility(View.VISIBLE);

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
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_users, container, false);
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
        void onUserFragmentInteraction(Uri uri);
    }
}
