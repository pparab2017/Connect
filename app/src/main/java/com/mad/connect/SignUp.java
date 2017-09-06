package com.mad.connect;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mad.connect.Entities.User;

public class SignUp extends AppCompatActivity implements View.OnClickListener {

    private EditText mEmailField;
    private EditText mPasswordField;
    private EditText mfirstName,mLastName;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String TAG = "signActivity";
    ProgressDialog progress;
    Button signUp,cancle;
    DatabaseReference mRoot  = FirebaseDatabase.getInstance().getReference();
    FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mEmailField = (EditText) findViewById(R.id.editTextEmailSi);
        mPasswordField = (EditText) findViewById(R.id.editTextPassSI);

        mfirstName = (EditText) findViewById(R.id.editTextFirstName);
        mLastName = (EditText) findViewById(R.id.editTextLastName);

        signUp = (Button) findViewById(R.id.signupButtonIn);
        signUp.setOnClickListener(this);

        cancle = (Button) findViewById(R.id.button2Cancle);
        cancle.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {

                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {

                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }


            }
        };
    }


    private void StoreUserInDB()
    {

        user = FirebaseAuth.getInstance().getCurrentUser();


                    User toInsert = new User();
                    String[] name = user.getDisplayName().split(" ");
                    toInsert.setFirstName(name[0]);
                    toInsert.setLastName(name[1]);
                    toInsert.setDpUrl("");
                    toInsert.setGender(false);
                    toInsert.setUserID(user.getUid());
                    toInsert.setUserName(user.getEmail());
                    mRoot.child("Users").child(user.getUid()).setValue(toInsert);

                    Intent welcome = new Intent(SignUp.this,Welcome.class);
                    startActivity(welcome);






    }

    private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);
        if (!validateForm()) {
            return;
        }

        showProgressDialog();

        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());


                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithEmail:failed", task.getException());
                            Toast.makeText(SignUp.this, "Failed",
                                    Toast.LENGTH_SHORT).show();
                        }


                        else {

                        }
                        hideProgressDialog();

                    }
                });

    }

    private void hideProgressDialog() {
        progress.hide();
    }

    private void showProgressDialog() {
        progress = new ProgressDialog(SignUp.this);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setCancelable(false);
        progress.setMessage("Please Wait...");
        progress.show();
    }


    private void createAccount(String email, String password) {
        Log.d(TAG, "createAccount:" + email);
        if (!validateForm()) {
            return;
        }

        showProgressDialog();


        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());


                        if (!task.isSuccessful()) {
                            Toast.makeText(SignUp.this, task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(mfirstName.getText().toString() + " " + mLastName.getText().toString())
                                    .build();

                            user.updateProfile(profileUpdates)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d(TAG, "User profile updated.");
                                                updateUI();
                                                StoreUserInDB();
                                            }
                                        }
                                    });


                        }
                        hideProgressDialog();

                    }
                });
    }


    private boolean validateForm() {
        boolean valid = true;

        String email = mEmailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailField.setError("Required.");
            valid = false;
        } else {
            mEmailField.setError(null);
        }

        String password = mPasswordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPasswordField.setError("Required.");
            valid = false;
        } else {

            mPasswordField.setError(null);

        }

        String firstName = mfirstName.getText().toString();
        if (TextUtils.isEmpty(firstName)) {
            mfirstName.setError("Required.");
            valid = false;
        } else {
            mfirstName.setError(null);
        }


        String lastName = mLastName.getText().toString();
        if (TextUtils.isEmpty(lastName)) {
            mLastName.setError("Required.");
            valid = false;
        } else {
            mLastName.setError(null);
        }

        return valid;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    private void updateUI() {
        mEmailField.setText("");
        mPasswordField.setText("");
        mfirstName.setText("");
        mLastName.setText("");
    }

    @Override
    public void onClick(View v) {

        switch (v.getId())
        {

            case R.id.signupButtonIn:
                FirebaseAuth.getInstance().signOut();
                createAccount(mEmailField.getText().toString(), mPasswordField.getText().toString());


                //
                break;
            case R.id.button2Cancle:
                Intent login = new Intent(SignUp.this,MainActivity.class);
                startActivity(login);
                break;
        }

    }
}
