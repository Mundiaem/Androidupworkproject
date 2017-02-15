package com.ict2105_team05_2017.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ict2105_team05_2017.R;
import com.ict2105_team05_2017.model.User;
import com.ict2105_team05_2017.utils.FirebaseUserData;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

public class LoginActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = "LoginActivity";
    private static final int RC_SIGN_IN = 9001;

    private FirebaseAuth mAuth;

    private EditText _emailText;
    private EditText _passwordText;
    private Button _loginButton;
    private Button buttonGoogleLogin;
    private Button buttonFacebookLogin;
    private CallbackManager mCallaback;

    private FirebaseAuth.AuthStateListener mAuthListener;
    private GoogleApiClient mGoogleApiClient;
    private FirebaseUserData mFirebaseUserData;

    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        /*Getting reference to the node*/
        mFirebaseInstance = FirebaseDatabase.getInstance();

        // get reference to 'users' node
        mFirebaseDatabase = mFirebaseInstance.getReference("usersCollection");
        // Storing the app title to the node
        mFirebaseInstance.getReference("app_title").setValue("ict2105team052017");
        getKeyHash();
        mAuthListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
            } else {
                Log.d(TAG, "onAuthStateChanged:signed_out");
            }
        };

        mAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        _emailText = (EditText) findViewById(R.id.editTextEmail);
        _passwordText = (EditText) findViewById(R.id.editTextPassword);
        _loginButton = (Button) findViewById(R.id.buttonLogin);
        buttonGoogleLogin = (Button) findViewById(R.id.buttonGoogleLogin);
        buttonFacebookLogin = (Button) findViewById(R.id.buttonFacebookLogin);
     /*   //Facebook Login button
        LoginButton fbLoginBtn = (LoginButton) findViewById(R.id.login_button);
*/

        _loginButton.setOnClickListener(view -> login());

        buttonGoogleLogin.setOnClickListener(view -> google_login());

        buttonFacebookLogin.setOnClickListener(view -> facebook_login(getApplicationContext(), mFirebaseInstance, mFirebaseDatabase));


        //Facebook login
        mCallaback = CallbackManager.Factory.create();


    }

    private void facebook_login(final Context context, final FirebaseDatabase mFirebaseInstance, final DatabaseReference mFirebaseDatabase) {
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email", "user_friends", "user_about_me"
                , "public_profile", "user_likes", "user_birthday", "user_location"));
       /* LoginManager.getInstance().setReadPermissions("email", "user_friends", "user_about_me"
                , "public_profile", "user_likes", "user_birthday", "user_location");*/
        LoginManager.getInstance().registerCallback(mCallaback, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.e(TAG, "facebook:onSuccess:" + loginResult);
                //"id":"748129345359073"
                Log.e(TAG, "UserID: " + loginResult.getAccessToken().getUserId());
                handleFacebookAccessToken(loginResult.getAccessToken(), context, mFirebaseInstance, mFirebaseDatabase);
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "Facebook:onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "Facebook:onError", error);
            }
        });
    }

    private void handleFacebookAccessToken(final AccessToken token, final Context context, final FirebaseDatabase mFirebaseInstance, final DatabaseReference mFirebaseDatabase) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                    // If sign in fails, display a message to the user. If sign in succeeds
                    // the auth state listener will be notified and logic to handle the
                    // signed in user can be handled in the listener.
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "signInWithCredential", task.getException());
                        Toast.makeText(LoginActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        createUser(token, context, mFirebaseInstance, mFirebaseDatabase);
                        onLoginSuccess();
                    }

                    // ...
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Passing the Activity result to the Facebook SDK
        mCallaback.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                Toast.makeText(LoginActivity.this, R.string.auth_failed, Toast.LENGTH_SHORT).show();
            }
        }
    }

    // checking if the User is LoggedIn
    private void userExixt() {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

    private void onLogin(String email, String password, ProgressDialog progressDialog) {


        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

            if (task.isSuccessful()) {

                onLoginSuccess();
                return;
            } else {
                showErrorMessage();
            }

        });
    }

    private void google_login() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        final ProgressDialog progressdialog = new ProgressDialog(LoginActivity.this);
        progressdialog.setMessage("Please Wait....");
        progressdialog.show();

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {


                    if (!task.isSuccessful()) {
                        Log.w(TAG, "signInWithCredential", task.getException());
                        Toast.makeText(LoginActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                        Toast.makeText(LoginActivity.this, "Successfully Logged In", Toast.LENGTH_SHORT).show();
                        onLoginSuccess();
                    }

                    progressdialog.hide();
                });
    }


    private void getKeyHash() {

        PackageInfo info;
        try {
            info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String keyhash = new String(Base64.encode(md.digest(), 0));
                // String something = new String(Base64.encodeBytes(md.digest()));
                Log.e("keyhash", "keyhash= " + keyhash);
                System.out.println("keyhash= " + keyhash);
            }
        } catch (PackageManager.NameNotFoundException e1) {
            Log.e("name not found", e1.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.e("no such an algorithm", e.toString());
        } catch (Exception e) {
            Log.e("exception", e.toString());
        }

    }

    //create firebase user after login
    private void createUser(AccessToken token, Context context, FirebaseDatabase mFirebaseInstance, DatabaseReference mFirebaseDatabase) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            String name = firebaseUser.getDisplayName();
            String email = firebaseUser.getEmail();
            String facebookID = token.getUserId();
            String id = firebaseUser.getUid();
            List providerData = firebaseUser.getProviderData();
            Log.e(TAG, " name: " + name + " Email: " + email + " providerId: " + facebookID + " firebaseUserId: " + id + " ProviderData: " + providerData);

            User user = new User(id, name, email, facebookID);

            mFirebaseUserData = new FirebaseUserData(context, mFirebaseInstance, mFirebaseDatabase, user);

            mFirebaseUserData.CreateUser();
        } else {
            Log.e(TAG, "All the user data is null!!!");
        }


    }

    /*
    * TODO: THis is emailpassword Login*/
    private void showErrorMessage() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(LoginActivity.this);
        dialogBuilder.setMessage("Incorrect user details");
        dialogBuilder.setPositiveButton("Ok", null);
        dialogBuilder.show();
    }

    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }
        _loginButton.setEnabled(false);
        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        // TODO: Implement your own authentication logic here.
        new android.os.Handler().postDelayed(
                () -> {
                    // On complete call either onLoginSuccess or onLoginFailed
                    onLogin(email, password, progressDialog);

                }, 3000);
        progressDialog.dismiss();
    }


    public void onLoginSuccess() {
        _loginButton.setEnabled(true);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed Enter correct credentials ", Toast.LENGTH_LONG).show();
        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }
}
