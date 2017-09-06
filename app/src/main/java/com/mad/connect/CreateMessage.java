package com.mad.connect;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mad.connect.Entities.Message;
import com.mad.connect.Entities.MessageContent;
import com.mad.connect.Entities.User;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;


public class CreateMessage extends Fragment {

User sentUser ;
    private ProgressDialog mProgress;
    private EditText messageText;
    private TextView toText ;
    private Button btnMsgSend;
    private ImageView imgMessage;
    static final int REQUEST_IMAGE_GET = 1;
    private Uri msgPhotoUri;
    private OnFragmentInteractionListener mListener;
    DatabaseReference mConditionRef ;
    DatabaseReference mRoot  = FirebaseDatabase.getInstance().getReference();
    FirebaseUser user;
    User Selected_USER;

    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef;
private  int mReply =0;
    private Message mMessage ;

    public CreateMessage() {
        // Required empty public constructor
    }


  public void setUser(User user)
  {
      this.sentUser = user;
   }

    public void setOldMessage(Message message,int reply)
    {
        this.mMessage = message;
        this.mReply = reply;
    }


    private void hideProgressDialog() {
        mProgress.hide();
    }

    private void showProgressDialog() {
        mProgress = new ProgressDialog(getActivity());
        mProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgress.setCancelable(false);
        mProgress.setMessage("Please Wait...");
        mProgress.show();
    }

    private void SetUpReply()
    {

        mConditionRef = mRoot.child("Users").child(mMessage.getSender());
        mConditionRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Selected_USER = dataSnapshot.getValue(User.class);

                toText.setText("To: "+ Selected_USER.getFullName());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void setUpNew()
    {


        mConditionRef = mRoot.child("Users").child(sentUser.getUserID());
        mConditionRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Selected_USER = dataSnapshot.getValue(User.class);

                toText.setText("To: "+ Selected_USER.getFullName());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        messageText = (EditText) getActivity().findViewById(R.id.editTextMessageText);
        toText = (TextView)getActivity().findViewById(R.id.textViewTo);
        btnMsgSend =(Button)getActivity().findViewById(R.id.buttonSendMessage);
        imgMessage = (ImageView)getActivity().findViewById(R.id.imageViewMessageImage);

        if(mReply == 0)
        {
            setUpNew();
        }
        else {
            SetUpReply();
        }




        imgMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new  Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivityForResult(intent, REQUEST_IMAGE_GET);
                }

            }
        });

        btnMsgSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user = FirebaseAuth.getInstance().getCurrentUser();
                showProgressDialog();

if(mReply == 1)
{
    CreateReply();
}
                else
{
    CreateNewMessage();
}





            }
        });

    }

    private void updateUI() {

        messageText.setText("");
        imgMessage.setImageResource(R.drawable.gal);
    }


    private void CreateReply()
    {
        final String NewMessageKey = mRoot.child("Messages").push().getKey();
        storageRef = storage.getReference();
        StorageReference riversRef = storageRef.child("Images/Messages/" + Selected_USER.getUserID() + "/"+ NewMessageKey+ ".png");

        if(msgPhotoUri!=null) {
            //UploadTask uploadTask = riversRef.putFile(msgPhotoUri);


            UploadTask uploadTask = riversRef.putBytes(getReducedImage());
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();


                    Message toSend = new Message();
                    toSend.setMessage(messageText.getText().toString());
                    toSend.setSender(user.getUid());
                    toSend.setReceiver(Selected_USER.getUserID());
                    toSend.setSendTime(new Date());
                    toSend.setImage(true);
                    toSend.setKey(NewMessageKey);

                    ArrayList<MessageContent> contents = mMessage.getAllMessages();
                    MessageContent toAdd = new MessageContent();
                    toAdd.setSendTime(new Date());
                    toAdd.setMessage(messageText.getText().toString());
                    toAdd.setSender(user.getUid());
                    toAdd.setReceiver(Selected_USER.getUserID());
                    toAdd.setImgURL(downloadUrl.toString());
                    contents.add(toAdd);
                    toSend.setAllMessages(contents);


                    mRoot.child("Messages").child(Selected_USER.getUserID()).child("Received").child(NewMessageKey).setValue(toSend);
                 //   mRoot.child("Messages").child(user.getUid()).child("sent").child(NewMessageKey).setValue(toSend);

                    hideProgressDialog();
                    Toast.makeText(getActivity(),"Message Sent",Toast.LENGTH_LONG).show();
                    updateUI();
                }
            });


        }
        else
        {
            Message toSend = new Message();
            toSend.setMessage(messageText.getText().toString());
            toSend.setSender(user.getUid());
            toSend.setReceiver(Selected_USER.getUserID());
            toSend.setSendTime(new Date());
            toSend.setImage(false);
            toSend.setKey(NewMessageKey);
            ArrayList<MessageContent> contents = mMessage.getAllMessages();
            MessageContent toAdd = new MessageContent();
            toAdd.setSendTime(new Date());
            toAdd.setMessage(messageText.getText().toString());
            toAdd.setSender(user.getUid());
            toAdd.setImgURL("");
            toAdd.setReceiver(Selected_USER.getUserID());
            contents.add(toAdd);
            toSend.setAllMessages(contents);

            mRoot.child("Messages").child(Selected_USER.getUserID()).child("Received").child(NewMessageKey).setValue(toSend);
           // mRoot.child("Messages").child(user.getUid()).child("sent").child(NewMessageKey).setValue(toSend);
            hideProgressDialog();
            Toast.makeText(getActivity(),"Message Sent",Toast.LENGTH_LONG).show();
            updateUI();
        }



    }


    public static String getRealPathFromUri(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }


    private  byte[] getReducedImage()
    {
        Bitmap original = null;
        try {
            original = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), msgPhotoUri);
        } catch (IOException e) {
            e.printStackTrace();
        }

        int width = original.getWidth();
        int height = original.getHeight();
        int sizeinKB = original.getByteCount()/ 1024;
        int sizeinMB = sizeinKB/ 1024;

        if(sizeinMB > 1)
        {
            original = Bitmap.createScaledBitmap(original, width/8, height/8, true);
        }
        else if(sizeinKB > 500)
        {
            original = Bitmap.createScaledBitmap(original, width/3, height/3, true);
        }
        else
        {
            original = Bitmap.createScaledBitmap(original, width/2, height/2, true);
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        original.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
                return data;

    }

    private void CreateNewMessage()
    {

        final String NewMessageKey = mRoot.child("Messages").push().getKey();
        storageRef = storage.getReference();
        StorageReference riversRef = storageRef.child("Images/Messages/" + Selected_USER.getUserID() + "/"+ NewMessageKey+ ".png");

        if(msgPhotoUri!=null) {
           // UploadTask uploadTask = riversRef.putFile(msgPhotoUri);
           // imgMessage.setDrawingCacheEnabled(true);
           // imgMessage.buildDrawingCache();

            UploadTask uploadTask = riversRef.putBytes(getReducedImage());


            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();


                    Message toSend = new Message();
                    toSend.setMessage(messageText.getText().toString());
                    toSend.setSender(user.getUid());
                    toSend.setReceiver(Selected_USER.getUserID());
                    toSend.setSendTime(new Date());
                    toSend.setImage(true);
                    toSend.setKey(NewMessageKey);

                    ArrayList<MessageContent> contents = new  ArrayList<MessageContent>();
                    MessageContent toAdd = new MessageContent();
                    toAdd.setSendTime(new Date());
                    toAdd.setMessage(messageText.getText().toString());
                    toAdd.setSender(user.getUid());
                    toAdd.setReceiver(Selected_USER.getUserID());
                    toAdd.setImgURL(downloadUrl.toString());
                    contents.add(toAdd);
                    toSend.setAllMessages(contents);


                    mRoot.child("Messages").child(Selected_USER.getUserID()).child("Received").child(NewMessageKey).setValue(toSend);
                   // mRoot.child("Messages").child(user.getUid()).child("sent").child(NewMessageKey).setValue(toSend);
                    hideProgressDialog();
                    Toast.makeText(getActivity(),"Message Sent",Toast.LENGTH_LONG).show();
                    updateUI();
                }
            });


        }
        else
        {
            Message toSend = new Message();
            toSend.setMessage(messageText.getText().toString());
            toSend.setSender(user.getUid());
            toSend.setReceiver(Selected_USER.getUserID());
            toSend.setSendTime(new Date());
            toSend.setImage(false);
            toSend.setKey(NewMessageKey);
            ArrayList<MessageContent> contents = new  ArrayList<MessageContent>();
            MessageContent toAdd = new MessageContent();
            toAdd.setSendTime(new Date());
            toAdd.setMessage(messageText.getText().toString());
            toAdd.setSender(user.getUid());
            toAdd.setImgURL("");
            toAdd.setReceiver(Selected_USER.getUserID());
            contents.add(toAdd);
            toSend.setAllMessages(contents);

            mRoot.child("Messages").child(Selected_USER.getUserID()).child("Received").child(NewMessageKey).setValue(toSend);
           // mRoot.child("Messages").child(user.getUid()).child("sent").child(NewMessageKey).setValue(toSend);
            hideProgressDialog();
            Toast.makeText(getActivity(),"Message Sent",Toast.LENGTH_LONG).show();
            updateUI();
        }

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode!=0) {
            msgPhotoUri = new Uri.Builder().build();
            msgPhotoUri = data.getData();
            Picasso.with(getActivity()).load(msgPhotoUri)
                    .into(imgMessage);

            //reduseimage();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_message, container, false);
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
        void onCreateMessageEvtFragmentInteraction(Uri uri);
    }
}
