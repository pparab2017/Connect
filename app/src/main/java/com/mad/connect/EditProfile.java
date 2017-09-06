package com.mad.connect;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

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
import com.mad.connect.Entities.Photos;
import com.mad.connect.Entities.User;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Date;


public class EditProfile extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private EditText firstName,lastName;
    private ProgressDialog mProgress;
    private User SELECTED_USER;
    private Button btnUpdate;
    static final int REQUEST_IMAGE_GET = 1;
    private Uri fullPhotoUri;
    private DatabaseReference mRoot  = FirebaseDatabase.getInstance().getReference();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef;
    ImageView imageViewUserDP;
    Switch genderSwitch;


    private FirebaseUser user;


    private OnFragmentInteractionListener mListener;



    public EditProfile() {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode!=0) {
            fullPhotoUri = data.getData();
            Picasso.with(getActivity()).load(fullPhotoUri).resize(600, 600) // resizes the image to these dimensions (in pixel)
                    .centerCrop()
                    .into(imageViewUserDP);
        }
        else
        {
            Picasso.with(getActivity()).load(SELECTED_USER.CheckAndGetDpUrl()).placeholder(SELECTED_USER.isGender() ? R.drawable.u_female :R.drawable.u_male)
                   .into(imageViewUserDP);
        }

    }



    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        firstName =(EditText)getActivity().findViewById(R.id.editTextEditFirstName);
        lastName =(EditText)getActivity().findViewById(R.id.editTextLastName);
        imageViewUserDP= (ImageView)getActivity().findViewById(R.id.imageViewEditUserDP);
        user = FirebaseAuth.getInstance().getCurrentUser();
        genderSwitch = (Switch)getActivity().findViewById(R.id.switchGender);
        btnUpdate = (Button)getActivity().findViewById(R.id.buttonUpdateProfile);

        imageViewUserDP.setOnClickListener(new View.OnClickListener() {
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



        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showProgressDialog();
                final String key = mRoot.child("ProfileDP").push().getKey();
                final String AlbumKey = mRoot.child("ProfileDPForAlbum").push().getKey();

                storageRef = storage.getReference();
                StorageReference riversRef = storageRef.child("Images/UserPhotos/" + SELECTED_USER.getUserID() + "/"+ key+ ".png");


                if(fullPhotoUri!=null) {
                   // UploadTask uploadTask = riversRef.putFile(fullPhotoUri);

                    imageViewUserDP.setDrawingCacheEnabled(true);
                    imageViewUserDP.buildDrawingCache();
                    Bitmap original = imageViewUserDP.getDrawingCache();
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    original.compress(Bitmap.CompressFormat.JPEG, 50, out);
                    Bitmap decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));


                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    decoded.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] data = baos.toByteArray();

                    UploadTask uploadTask = riversRef.putBytes(data);

                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Uri downloadUrl = taskSnapshot.getDownloadUrl();
                            // toSend.setImageUrl(downloadUrl.toString());
                            User toUpdate = SELECTED_USER;
                            toUpdate.setFirstName(firstName.getText().toString());
                            toUpdate.setLastName(lastName.getText().toString());
                            toUpdate.setGender(genderSwitch.isChecked());
                            toUpdate.setDpUrl(downloadUrl.toString());
                            Photos toAdd = new Photos();
                            toAdd.setPhotoUrl(downloadUrl.toString());
                            toAdd.setDate(new Date());




                            mRoot.child("Users").child(SELECTED_USER.getUserID()).setValue(toUpdate);
                            mRoot.child("Photos").child(SELECTED_USER.getUserID()).child(AlbumKey).setValue(toAdd);

                            hideProgressDialog();
                            mListener.onFragmentEditInteraction();

                        }
                    });
                }
                else
                {
                    User toUpdate = SELECTED_USER;
                    toUpdate.setFirstName(firstName.getText().toString());
                    toUpdate.setLastName(lastName.getText().toString());
                    toUpdate.setGender(genderSwitch.isChecked());

                    mRoot.child("Users").child(SELECTED_USER.getUserID()).setValue(toUpdate);

                    hideProgressDialog();
                    mListener.onFragmentEditInteraction();
                }

                //toUpdate.s


            }
        });

        mRoot.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.child("Users").hasChild(user.getUid())) {
                    DataSnapshot toCheck = snapshot.child("Users").child(user.getUid());
                    SELECTED_USER = toCheck.getValue(User.class);
                    Log.d("UserUserUserUser",SELECTED_USER.getFirstName());

                    firstName.setText(SELECTED_USER.getFirstName().toString());
                    lastName.setText(SELECTED_USER.getLastName().toString());
                    Picasso.with(getActivity()).load(SELECTED_USER.CheckAndGetDpUrl()).placeholder(SELECTED_USER.isGender() ? R.drawable.u_female :R.drawable.u_male)
                            .into(imageViewUserDP);
                    if(SELECTED_USER.isGender())
                    {
                        genderSwitch.setChecked(true);
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




    }

    public static EditProfile newInstance(String param1, String param2) {
        EditProfile fragment = new EditProfile();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_profile, container, false);
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



    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentEditInteraction();
    }
}
