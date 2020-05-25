package com.example.heroku;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private AccessTokenTracker accessTokenTracker;
    private static final String TAG = "FacebookAuthentication";
    private LoginButton loginButton;
    private CallbackManager mCallbackManager;
    ProgressDialog progressDialog;

    @BindView(R.id.tv_login)
    TextView tv_login;

    @BindView(R.id.et_register_email)
    EditText mEmail;

    @BindView(R.id.et_register_password)
    EditText mPassword;

    @BindView(R.id.et_confirmPassword)
    EditText mConfirm;

    @BindView(R.id.btn_register)
    Button mRegisterbtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Registering User...");

        FacebookSdk.sdkInitialize(getApplicationContext());
        mAuth = FirebaseAuth.getInstance();
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
        loginButton = findViewById(R.id.btn_facebook);//facebook

        loginButton.setReadPermissions("email", "public_profile");//login facebook logic
        mCallbackManager = CallbackManager.Factory.create();//facebook
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>(){
            @Override
            public void onSuccess(LoginResult loginResult){
                Log.d(TAG, "onSuccess" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }
            @Override
            public void onCancel() {
                Log.d(TAG, "onCancel" );
            }
            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "onError" + error);
            }
        });

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user!=null){
                    //updateUI(user);
                } else {
                    //updateUI(null);
                }
            }
        };

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                if(currentAccessToken == null){
                    mAuth.signOut();
                }
            }
        };

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void handleFacebookAccessToken(AccessToken token){
    Log.d(TAG, "handleFacebookAccessToken" + token);

    //AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null); comparing to facebook credentional
    AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
    mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    Toast.makeText(RegisterActivity.this, "FB access!!!! ", Toast.LENGTH_SHORT).show();
                    if(task.isSuccessful()){
                        Log.d(TAG, "signIn facebook with credential Successful");
                        FirebaseUser user = mAuth.getCurrentUser();

                        //if user is signing in for the first time
                        if(task.getResult().getAdditionalUserInfo().isNewUser()){
                            //Get user email and uid from auth
                            String email = user.getEmail();
                            String uid = user.getUid();
                            String name = user.getDisplayName();
                            String phone = user.getPhoneNumber();
                            //String image = user.

                            HashMap<Object, String> hashMap = new HashMap<>();

                            hashMap.put("email", email);
                            hashMap.put("uid", uid);
                            hashMap.put("name", name); // will add later
                            hashMap.put("phone", phone);
                            hashMap.put("image", "noImage");
                            hashMap.put("cover", "noImage");

                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            //path to store user data named users
                            DatabaseReference reference = database.getReference("Users");
                            //put data within hashmap database
                            reference.child(uid).setValue(hashMap);
                            startActivity(new Intent(RegisterActivity.this, FacebookProfileSetupActivity.class));
                            finish();
                        }
                        startActivity(new Intent(RegisterActivity.this, DashboardActivity.class));
                        finish();
                    } else {
                        Log.d(TAG, "xsign in with credential; failure", task.getException());
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onError");
            }
        });
    }

    @OnClick(R.id.tv_login)
    public void submit(View view) {
        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
    }

    @OnClick(R.id.btn_register)
    public void register(View view){
        String email = mEmail.getText().toString().trim();
        String password = mPassword.getText().toString().trim();
        String rpassword = mConfirm.getText().toString().trim();

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            mEmail.setError("Invalid Email");
            mEmail.setFocusable(true);
        }
        else if(password.length()<6){
            mPassword.setError("Password length at least 6 characters");
            return;
        }
        else if (!password.equals(rpassword)){
            Toast.makeText(RegisterActivity.this, "Passwords do not match.", Toast.LENGTH_SHORT).show();
        }
        else {
            registerUser(email,  password);
        }
    }

    private void registerUser(String email, String password) {
        progressDialog.show();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, dismiss dialog and start register activity
                            progressDialog.dismiss();
                            FirebaseUser user = mAuth.getCurrentUser();

                            //Get user email and uid from auth
                            String email = user.getEmail();
                            String uid = user.getUid();

                            HashMap<Object, String> hashMap = new HashMap<>();

                            hashMap.put("email", email);
                            hashMap.put("uid", uid);
                            hashMap.put("name", ""); // will add later
                            hashMap.put("phone", "");
                            hashMap.put("image", "noImage");
                            hashMap.put("cover", "noImage");

                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            //path to store user data named users
                            DatabaseReference reference = database.getReference("Users");
                            //put data within hashmap database
                            reference.child(uid).setValue(hashMap);
                            Toast.makeText(RegisterActivity.this, "Registered...\n"+user.getEmail(), Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(RegisterActivity.this, ProfileSetupActivity.class));
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            progressDialog.dismiss();
                            Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }


                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(RegisterActivity.this, ""+e.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });



    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
