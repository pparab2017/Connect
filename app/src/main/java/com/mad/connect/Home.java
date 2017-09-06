package com.mad.connect;

import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mad.connect.Adapters.AlbumAdapter;
import com.mad.connect.Adapters.UserAdapter;
import com.mad.connect.Entities.Photos;
import com.mad.connect.Entities.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class Home extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    User user;
    ImageView UserDP;
    TextView userName;
    LinearLayout edit;
    private FirebaseUser firebaseUseruser;
    LinearLayout noPhotoDiv;

    private RecyclerView recyclerView;
    private AlbumAdapter albumAdapterAdapter;
    private ArrayList<Photos> allPhotos = new ArrayList<Photos>();
    ImageView UserGender;
    TextView Gender;

    DatabaseReference mRoot  = FirebaseDatabase.getInstance().getReference();
    DatabaseReference  mConditionRef ;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public void setUserInfo(User user)
    {
        this.user = user;
    }

    public Home() {

    }


    // TODO: Rename and change types and number of parameters
    public static Home newInstance(String param1, String param2) {
        Home fragment = new Home();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        UserDP = (ImageView)getActivity().findViewById(R.id.imageViewUserDp);
        UserGender = (ImageView)getActivity().findViewById(R.id.imageViewMyGender);
        Gender = (TextView)getActivity().findViewById(R.id.textViewMyGender);
        Picasso.with(getActivity()).load(user.CheckAndGetDpUrl())
                .transform(new CircleTransform())
                .placeholder(user.isGender() ? R.drawable.u_female :R.drawable.u_male)
                    .into(UserDP);


        Gender.setText(user.getGenderText());
        if(user.isGender())
        {
            UserGender.setImageResource(R.drawable.female);
        }
        userName = (TextView)getActivity().findViewById(R.id.textViewUserName);
        userName.setText("Welcome, " + user.getFullName() );
        edit = (LinearLayout)getActivity().findViewById(R.id.buttonEditProfile);

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onFragmentInteraction(user);
            }
        });


        noPhotoDiv = (LinearLayout)getActivity().findViewById(R.id.linearLayoutNoPhoto);

        allPhotos = new ArrayList<Photos>();
        recyclerView = (RecyclerView)getActivity().findViewById(R.id.MyAlbumView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false));

        firebaseUseruser = FirebaseAuth.getInstance().getCurrentUser();

        albumAdapterAdapter = new AlbumAdapter(allPhotos,getActivity(),R.layout.item_photo,firebaseUseruser.getUid());
        albumAdapterAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(albumAdapterAdapter);


        mConditionRef = mRoot.child("Photos").child(firebaseUseruser.getUid());

        mConditionRef.addChildEventListener(new ChildEventListener() {


            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Photos toadd = dataSnapshot.getValue(Photos.class);

                    allPhotos.add(toadd);

              //  Log.d("XXXXXX",allUsers.toString());
                albumAdapterAdapter.notifyDataSetChanged();
                if(allPhotos.size() == 0)
                {
                    noPhotoDiv.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);

                }else
                {
                    noPhotoDiv.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
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

        return inflater.inflate(R.layout.fragment_home, container, false);
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

        void onFragmentInteraction(User user);

    }
}
