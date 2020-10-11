package com.example.test201009_2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class Login extends AppCompatActivity {

    //Firebase Instance
    private FirebaseAuth mAuth;
    private EditText email;
    private EditText password;

    private Button login;
    private Button signUp;

    private SignInButton signInButton;
    private ProgressBar loadingBar;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        FirebaseApp.initializeApp(this);

        //Firebase Instance reset
        mAuth = FirebaseAuth.getInstance();

        loadingBar = findViewById(R.id.Loading_bar);
        loadingBar.setVisibility(View.GONE);

        btn_set();
        setSignUp();
        setLogin();
        setGoogleSignIn();

        if(mAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check user signed
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }


    protected void btn_set(){
        email = findViewById(R.id.email_text);
        password = findViewById(R.id.password_text);

        login = findViewById(R.id.login_btn);
        signUp = findViewById(R.id.signUp_btn);
    }

    protected void setGoogleSignIn(){
        signInButton = findViewById(R.id.signInGoogleButton);

        //Google
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
    }


    protected void setSignUp(){

            signUp.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String em = email.getText().toString().trim();
            String pass = password.getText().toString().trim();

            //email 및 password 검사
            if(TextUtils.isEmpty(em)){
                email.setError("Email is Empty");
                return;
            }
            if(TextUtils.isEmpty(pass)){
                email.setError("Email is Password");
                return;
            }
            loadingBar.setVisibility(View.VISIBLE);
            mAuth.createUserWithEmailAndPassword(em,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {


                    loadingBar.setVisibility(View.VISIBLE);
                    if(task.isSuccessful()){
                        Toast.makeText(Login.this, "User Created", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    }else{
                        Toast.makeText(Login.this, "User Created fail", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }});
    }

    protected void setLogin(){
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String em = email.getText().toString().trim();
                String pass = password.getText().toString().trim();

                //email 및 password 검사
                if(TextUtils.isEmpty(em)){
                    email.setError("Email is Empty");
                    return;
                }
                if(TextUtils.isEmpty(pass)){
                    email.setError("Email is Password");
                    return;
                }
                loadingBar.setVisibility(View.VISIBLE);
                mAuth.signInWithEmailAndPassword(em,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(Login.this, "User Login", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        }else{
                            Toast.makeText(Login.this, "User Login fail", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }});
    }

    private void signIn(){
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> task){
        try{
            GoogleSignInAccount acc = task.getResult(ApiException.class);
            Toast.makeText(Login.this, "User Login", Toast.LENGTH_SHORT).show();
            FirebaseGoogleAuth(acc);
        }catch (ApiException e){
            Toast.makeText(Login.this, "User Login fail", Toast.LENGTH_SHORT).show();
            FirebaseGoogleAuth(null);
        }
    }

    private void FirebaseGoogleAuth(GoogleSignInAccount acc){
        AuthCredential autoCloseable = GoogleAuthProvider.getCredential(acc.getIdToken(), null);
        mAuth.signInWithCredential(autoCloseable).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(Login.this, "User Login", Toast.LENGTH_SHORT).show();
                    FirebaseUser user = mAuth.getCurrentUser();
                    updateUI(user);
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));

                }else{
                    Toast.makeText(Login.this, "User Login fail", Toast.LENGTH_SHORT).show();
                    updateUI(null);
                }
            }
        });
    }

    private void updateUI(FirebaseUser user){
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if(account != null){
            String personName = account.getDisplayName();
            String personGiveName = account.getGivenName();
            String personFamilyName = account.getFamilyName();
            String personEmail = account.getEmail();
            String personId = account.getId();
            Uri personPhoto = account.getPhotoUrl();
            Toast.makeText(Login.this, personName +"User Login fail", Toast.LENGTH_SHORT).show();
        }
    }
}