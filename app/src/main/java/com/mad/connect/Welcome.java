package com.mad.connect;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mad.connect.Adapters.AlbumAdapter;
import com.mad.connect.Adapters.MessageAdapter;
import com.mad.connect.Adapters.MessageDetailAdapter;
import com.mad.connect.Adapters.UserAdapter;
import com.mad.connect.Entities.Message;
import com.mad.connect.Entities.Photos;
import com.mad.connect.Entities.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class Welcome extends AppCompatActivity implements View.OnClickListener, Home.OnFragmentInteractionListener
,EditProfile.OnFragmentInteractionListener,Users.OnFragmentInteractionListener
,UserAdapter.ItemClickCallBack,OtherUser.OnFragmentInteractionListener
,CreateMessage.OnFragmentInteractionListener
,MessageAdapter.ItemClickCallBack
,AllMessages.OnFragmentInteractionListener
,DetailMessage.OnFragmentInteractionListener
,MessageDetailAdapter.ItemClickCallBack
,AlbumAdapter.ItemClickCallBack
{

    FirebaseUser user;
    ImageView buttonUsers,buttonHome,buttonMessage;
    DatabaseReference mRoot  = FirebaseDatabase.getInstance().getReference();
    User LOGGED_IN_USER;
    TextView textViewCount ;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef;

    private ArrayList<Message> allMessages = new ArrayList<Message>();
    DatabaseReference  mConditionRef ;

    private FirebaseAuth mAuth;

    private void setDefaultImages()
    {
        buttonHome.setImageResource(R.drawable.home);
        buttonMessage.setImageResource(R.drawable.message);
        buttonUsers.setImageResource(R.drawable.user);
    }


    private void SetTheAppIcon()
    {
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.my_con);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
    }
    private void setRefreshedUser()
    {
        user = FirebaseAuth.getInstance().getCurrentUser();



        mRoot.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.child("Users").hasChild(user.getUid())) {

                    DataSnapshot toCheck = snapshot.child("Users").child(user.getUid());
                    LOGGED_IN_USER = toCheck.getValue(User.class);
                    //Log.d("User Is This",currUser.getFirstName());
                    Home toSend = new Home();
                    toSend.setUserInfo(LOGGED_IN_USER);
                    getFragmentManager().beginTransaction().add(R.id.container, toSend, "home")
                            .commit();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        SetTheAppIcon();
        buttonUsers = (ImageView) findViewById(R.id.imageViewUser_G);
        buttonHome = (ImageView) findViewById(R.id.imageViewHome_G);
        buttonMessage = (ImageView) findViewById(R.id.imageViewMessage_G);
        textViewCount = (TextView) findViewById(R.id.textViewCount);
        buttonHome.setImageResource(R.drawable.home_sel);

        buttonUsers.setOnClickListener(this);
        buttonHome.setOnClickListener(this);
        buttonMessage.setOnClickListener(this);

        user = FirebaseAuth.getInstance().getCurrentUser();



        setRefreshedUser();
        UnreadMsgCount();

    }


    @Override
    public void onFragmentInteraction(User user) {

        EditProfile toEdit = new EditProfile();
       // toEdit.setUserInfo(user);
        getFragmentManager().beginTransaction().replace(R.id.container,new EditProfile(),"editProfile")
                .addToBackStack(null)
               .commit();
    }



    @Override
    public void onClick(View v) {

        switch (v.getId())
        {
            case R.id.imageViewHome_G:
                mConditionRef = mRoot.child("Users").child(user.getUid());
                mConditionRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        LOGGED_IN_USER = dataSnapshot.getValue(User.class);
                        setDefaultImages();
                        buttonHome.setImageResource(R.drawable.home_sel);
                        Home toSend = new Home();
                        toSend.setUserInfo(LOGGED_IN_USER);
                        getFragmentManager().beginTransaction().replace(R.id.container, toSend, "home")
                                .addToBackStack(null)
                                .commit();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


                break;
            case R.id.imageViewUser_G:
                setDefaultImages();
                buttonUsers.setImageResource(R.drawable.user_sel);
                getFragmentManager().beginTransaction().replace(R.id.container,new Users(),"users")
                        .addToBackStack(null)
                        .commit();
                break;


            case R.id.imageViewMessage_G:
                setDefaultImages();
                buttonMessage.setImageResource(R.drawable.message_sel);
                getFragmentManager().beginTransaction().replace(R.id.container,new AllMessages(),"allMessages")
                        .addToBackStack(null)
                        .commit();
                break;
        }

    }

    @Override
    public void onUserFragmentInteraction(Uri uri) {

    }

    @Override
    public void OnItemClick(User selectedUser) {

        OtherUser toSend = new OtherUser();
        toSend.setSelectedUser(selectedUser);
        getFragmentManager().beginTransaction().replace(R.id.container,toSend,"otherUsers")
                .addToBackStack(null)
                .commit();

    }

    @Override
    public void OnItemMessageIconClick(User selectedUser) {
        CreateMessage toCreate = new CreateMessage();
        toCreate.setUser(selectedUser);
        getFragmentManager().beginTransaction().replace(R.id.container,toCreate,"createMessage")
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void OnItemClick(Message selectedMessage) {
        DetailMessage toSend = new DetailMessage();
        toSend.SetDetailsObj(selectedMessage.getAllMessages(),selectedMessage);
        getFragmentManager().beginTransaction().replace(R.id.container,toSend,"detailMessages")
                .addToBackStack(null)
                .commit();


    }

    @Override
    public void OnItemMessageDeleteClick(Message toDelete) {

        mConditionRef = mRoot.child("Messages").child(user.getUid()).child("Received");
        mConditionRef.child(toDelete.getKey()).removeValue();

        Log.d("theKEYIS",toDelete.getKey());

        if(toDelete.isImage()) {
            storageRef = storage.getReference();
            String toDeleteString  = "Images/Messages/" +user.getUid().toString() +"/" +toDelete.getKey() + ".png";
            Log.d("thethedelete",toDeleteString);
            StorageReference riversRef = storageRef.child(toDeleteString);

            riversRef.delete().addOnSuccessListener(new OnSuccessListener() {

                @Override
                public void onSuccess(Object o) {
                    UnreadMsgCount();
                    Toast.makeText(Welcome.this, " Deleted",
                            Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Uh-oh, an error occurred!
                }
            });
        }
        else
        {
            UnreadMsgCount();
            Toast.makeText(Welcome.this, " Deleted",
                    Toast.LENGTH_SHORT).show();

        }




    }





    @Override
    public void onCreateMessageEvtFragmentInteraction(Uri uri) {

    }

    @Override
    public void onOtherUserMessageCreateFragmentInteraction(User Recent_user) {
        CreateMessage toCreate = new CreateMessage();
        toCreate.setUser(Recent_user);
        getFragmentManager().beginTransaction().replace(R.id.container,toCreate,"createMessage")
                .addToBackStack(null)
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.Logout:
                mAuth = FirebaseAuth.getInstance();
                mAuth.signOut();
                LoginManager.getInstance().logOut();


                Intent Login = new Intent(Welcome.this,MainActivity.class);
                startActivity(Login);

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater myMenu = getMenuInflater();
        myMenu.inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onMessageSelectFragmentInteraction(Uri uri) {

    }

    @Override
    public void refreshTheCount() {
        UnreadMsgCount();
    }

    @Override
    public void onFragmentInteractionOfDetailMessage(Message toReply) {
        CreateMessage toCreate = new CreateMessage();
        toCreate.setOldMessage(toReply,1);
        getFragmentManager().beginTransaction().replace(R.id.container,toCreate,"createMessage")
                .addToBackStack(null)
                .commit();
    }


    @Override
    public void onFragmentEditInteraction() {
        mConditionRef = mRoot.child("Users").child(user.getUid());
        mConditionRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(LOGGED_IN_USER!=null) {
                    LOGGED_IN_USER = dataSnapshot.getValue(User.class);
                    setDefaultImages();
                    buttonHome.setImageResource(R.drawable.home_sel);
                    Home toSend = new Home();
                    toSend.setUserInfo(LOGGED_IN_USER);
                    getFragmentManager().beginTransaction().replace(R.id.container, toSend, "home")
                            .addToBackStack(null)
                            .commit();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void refreshCount() {
        UnreadMsgCount();
    }

    private void UnreadMsgCount()
    {
        allMessages = new ArrayList<Message>();
        mConditionRef = mRoot.child("Messages").child(user.getUid()).child("Received");

        mConditionRef.addChildEventListener(new ChildEventListener() {


            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Message toadd = dataSnapshot.getValue(Message.class);

                if(!toadd.isRead()) {
                    allMessages.add(toadd);
                }
                if(allMessages.size() == 0) {
                    textViewCount.setVisibility(View.GONE);

                }
                else
                {
                    textViewCount.setVisibility(View.VISIBLE);
                    textViewCount.setText(String.valueOf(allMessages.size()));
                }
            }



            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {


            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

                Message toadd = dataSnapshot.getValue(Message.class);
                for(Message m : allMessages)
                {
                    if(m.equals(toadd))
                    {
                        allMessages.remove(m);
                    }
                }

                if(allMessages.size() == 0) {
                    textViewCount.setVisibility(View.GONE);

                }
                else
                {
                    textViewCount.setVisibility(View.VISIBLE);
                    textViewCount.setText(String.valueOf(allMessages.size()));
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
    public void OnItemClick(Photos selectedPhoto) {

    }
}
