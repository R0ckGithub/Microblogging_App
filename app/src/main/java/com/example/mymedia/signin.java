package com.example.mymedia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class signin extends AppCompatActivity {

    private static final String TAG = "signin";

    private EditText email_txt,password_txt,confrm_password_txt;
    private Button signin;
    private TextView register,forgot_pass;
    private String email,password,confrm_password;
    private Context mcontext = this;
    private FirebaseAuth mAuth ;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();
    private  int state=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        setupfirebaseauth();

        //setup buttons
        email_txt = findViewById(R.id.sigin_email);
        password_txt = findViewById(R.id.signin_password);
        signin = findViewById(R.id.detail_btn_signin);
        register = findViewById(R.id.register_txt_btn);
        confrm_password_txt = findViewById(R.id.signin_confrm_password);
        forgot_pass = findViewById(R.id.forgot_password);


        //signin Activity
        confrm_password_txt.setVisibility(View.GONE);
        register.setVisibility(View.VISIBLE);
        signin.setText("SIGN IN");
        forgot_pass.setVisibility(View.VISIBLE);
        password_txt.setVisibility(View.VISIBLE);
        state=0;

        //converting to register activity
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Intent intent = new Intent(getBaseContext(),Profiledetails.class);
                startActivity(intent);*/
                confrm_password_txt.setVisibility(View.VISIBLE);
                register.setVisibility(View.INVISIBLE);
                signin.setText("SIGN UP");
                forgot_pass.setVisibility(View.GONE);
                password_txt.setVisibility(View.VISIBLE);
                state=1;
            }
        });

        //signin
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(state == 0)
                {
                    Log.d(TAG, "onClick: signin");
                    SigninFun();
                }
                else if(state==1)
                {
                    Log.d(TAG, "onClick: signup");
                    signup_fun();
                }
                else if(state==2)
                {
                    Log.d(TAG, "onClick: reset password");
                    reset_pwd();
                }
            }
        });

        forgot_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                password_txt.setVisibility(View.GONE);
                register.setVisibility(View.GONE);
                signin.setText("RESET PASSWORD");
                forgot_pass.setVisibility(View.GONE);
                state=2;
            }
        });
    }

    private void setupfirebaseauth()
    {
        mAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    //     Toast.makeText(getBaseContext(), "Signed in", Toast.LENGTH_SHORT).show();
                } else {

                }
            }
        };
    }

    private void SigninFun() {
        email = email_txt.getText().toString();
        password = password_txt.getText().toString();

        if (!email.isEmpty() && !password.isEmpty()) {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "signInWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                try{
                                    if(user.isEmailVerified()){
                                        Log.d(TAG, "onComplete: success email verified");
                                        db.collection("users")
                                                .document(FirebaseAuth.getInstance().getUid())
                                                .get()
                                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        if(task.isSuccessful())
                                                        {
                                                            DocumentSnapshot doc = task.getResult();
                                                            if(doc.exists())
                                                            {
                                                                Log.d(TAG, "onComplete: not a new user");
                                                                AuthUserSetter();
                                                            }
                                                            else
                                                            {
                                                                Log.d(TAG, "onComplete: new user");
                                                                open_profile_deatils();
                                                            }
                                                        }
                                                        else
                                                        {
                                                            Log.d(TAG, "onComplete: unsuccesful ");
                                                        }
                                                    }
                                                });

                                    }
                                    else{
                                        Toast.makeText(signin.this, "Email not Verified", Toast.LENGTH_SHORT).show();
                                        mAuth.signOut();
                                    }
                                }
                                catch (NullPointerException e) {
                                    Log.e(TAG, "onComplete: NullPointerException" + e.getMessage());
                                }


                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                Toast.makeText(mcontext, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                                mAuth.signOut();
                            }

                        }
                    });

        } else {
            Toast.makeText(mcontext, "Fill all details Properly", Toast.LENGTH_LONG);
        }
    }


    public void AuthUserSetter()
    {
        Log.d(TAG, "AuthUserSetter: ");
        welcome.user1.setUser_uid(mAuth.getCurrentUser().getUid());
        DocumentReference mDocRef = db.collection("users")
                .document(mAuth.getCurrentUser().getUid());
        mDocRef
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful())
                        {
                            DocumentSnapshot doc = task.getResult();
                            welcome.user1.setAuthuser(doc.get("user_name").toString());
                            welcome.user1.setAuthuser_url(doc.get("user_pic_url").toString());
                        }
                        else
                        {
                            Log.d(TAG, "onComplete: No user exists");
                        }
                    }
                });
        if(welcome.user1.user_uid!=null) {
            Intent intent = new Intent(this,mainUI.class);
            finish();
            startActivity(intent);
        }

    }

    void callMainUI()
    {
        Intent intent = new Intent(this,mainUI.class);
        startActivity(intent);
    }


    //user_signup
    private  void signup_fun()
    {
        email = email_txt.getText().toString();
        password = password_txt.getText().toString();
        confrm_password = confrm_password_txt.getText().toString();
        Log.d(TAG, "signup_fun: "+password+" "+confrm_password);
        if(!(password.equals(confrm_password)))
        {
            Toast.makeText(getBaseContext(),"Password did not Match",Toast.LENGTH_SHORT).show();
        }
        else
        if(password.length()<6)
        {
            Toast.makeText(getBaseContext(),"Very Short Password (min 6 char)",Toast.LENGTH_SHORT).show();
        }
        else {
            Log.d(TAG, "signup_fun: " + email );
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "createUserWithEmail:success");
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                EmailVerififcation(user);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                Toast.makeText(getBaseContext(), "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
        //   finish();
    }

    //send verification email

    void EmailVerififcation(FirebaseUser user)
    {
        Log.d(TAG, "EmailVerififcation: "+user);

        if(user!=null){
            user.sendEmailVerification()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Log.d(TAG, "onComplete: Verfication email sent");
                                Toast.makeText(getBaseContext(), "verification email sent", Toast.LENGTH_SHORT).show();
                                mAuth.signOut();
                                confrm_password_txt.setVisibility(View.GONE);
                                register.setVisibility(View.VISIBLE);
                                signin.setText("SIGN IN");
                                state=0;
                                email_txt.setText("");
                                password_txt.setText("");
                                forgot_pass.setVisibility(View.VISIBLE);

                            }
                            else{
                                Toast.makeText(getBaseContext(), "couldn't send verification email", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
        }
        else
        {
            Log.d(TAG, "EmailVerififcation: null user");
        }
    }

    void open_profile_deatils()
    {
        Intent intent = new Intent(this,Profiledetails.class);
        startActivity(intent);
    }

    void reset_pwd()
    {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String emailAddress = email_txt.getText().toString();
        if(emailAddress.isEmpty())
        {
            Toast.makeText(getBaseContext(), "Invalid Email ID", Toast.LENGTH_SHORT).show();
        }

        auth.sendPasswordResetEmail(emailAddress)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Email sent.");
                            Toast.makeText(getBaseContext(),"Email Sent",Toast.LENGTH_SHORT).show();
                            confrm_password_txt.setVisibility(View.GONE);
                            register.setVisibility(View.VISIBLE);
                            signin.setText("SIGN IN");
                            forgot_pass.setVisibility(View.VISIBLE);
                            password_txt.setVisibility(View.VISIBLE);
                            state=0;

                        }
                        else
                        {
                            Log.d(TAG, "onComplete: INvalid email");
                            Toast.makeText(getBaseContext(),"Invalid Email",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


}