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
import com.google.firebase.database.ValueEventListener;
import com.mad.connect.Adapters.AlbumAdapter;
import com.mad.connect.Entities.Photos;
import com.mad.connect.Entities.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class OtherUser extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    User SelectedUser;
    DatabaseReference mRoot  = FirebaseDatabase.getInstance().getReference();
    User Recent_user;
    DatabaseReference  mConditionRef ;
    LinearLayout noPhotoDiv;
    ImageView UserIcon,UserGender;
    TextView UserName,Gender,SendMsg;
LinearLayout msgSend ;




    private RecyclerView recyclerView;
    private AlbumAdapter albumAdapterAdapter;
    private ArrayList<Photos> allPhotos = new ArrayList<Photos>();

    private OnFragmentInteractionListener mListener;

    public void setSelectedUser(User sentUser)
    {
        this.SelectedUser = sentUser;
    }

    public OtherUser() {}


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d("iAmTHis",SelectedUser.getFirstName());

        UserGender = (ImageView)getActivity().findViewById(R.id.imageViewGender);
        Gender = (TextView)getActivity().findViewById(R.id.textViewGender);
        UserIcon = (ImageView)getActivity().findViewById(R.id.imageViewUserDp);

        UserName = (TextView)getActivity().findViewById(R.id.textViewOtherUserName);

        SendMsg = (TextView)getActivity().findViewById(R.id.textViewSendMessage);

        msgSend = (LinearLayout)getActivity().findViewById(R.id.ViewSendMsg);
        msgSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            mListener.onOtherUserMessageCreateFragmentInteraction(Recent_user);
            }
        });

        mConditionRef = mRoot.child("Users").child(SelectedUser.getUserID());
        mConditionRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Recent_user = dataSnapshot.getValue(User.class);

                UserName.setText(Recent_user.getFullName());
                Gender.setText(Recent_user.getGenderText());
                SendMsg.setText("Send Message to " +Recent_user.getFullName() );
                Picasso.with(getActivity()).load(Recent_user.CheckAndGetDpUrl())
                        .transform(new CircleTransform())
                        .placeholder(Recent_user.isGender() ? R.drawable.u_female :R.drawable.u_male)
                        .into(UserIcon);
                if(Recent_user.isGender())
                {
                    UserGender.setImageResource(R.drawable.female);
                }
                setAlbum();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });





    }

private void setAlbum()
{
    allPhotos = new ArrayList<Photos>();
    recyclerView = (RecyclerView)getActivity().findViewById(R.id.MyAlbumView);
    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false));



    albumAdapterAdapter = new AlbumAdapter(allPhotos,getActivity(),R.layout.item_photo,Recent_user.getUserID());
    albumAdapterAdapter.notifyDataSetChanged();
    recyclerView.setAdapter(albumAdapterAdapter);
     noPhotoDiv = (LinearLayout)getActivity().findViewById(R.id.noPhotoView);


    mConditionRef = mRoot.child("Photos").child(Recent_user.getUserID());

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

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_other_user, container, false);
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
        void onOtherUserMessageCreateFragmentInteraction(User Recent_user);
    }
}
