package com.grademojo.facebookpractice;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageInstaller;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AlertDialog.Builder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.login.widget.ProfilePictureView;
import com.facebook.share.widget.ShareDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.logging.StreamHandler;

import okhttp3.Dispatcher;

import static com.grademojo.facebookpractice.R.id.fb;

public class MainActivity extends AppCompatActivity {

    private ImageView face_image;
    private CardView card_view_facebook;

    private CallbackManager callbackManager;
    private ShareDialog shareDialog;
    private LoginButton login;
    private ProfilePictureView profile;
    private Dialog details_dialog;
    private TextView details_txt;
    private String facebook_id,f_name, m_name, l_name, gender, profile_image, full_name, email_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FacebookSdk.sdkInitialize(getApplicationContext());

        callbackManager = CallbackManager.Factory.create();
        login = (LoginButton)findViewById(R.id.login_button);
        face_image = (ImageView) findViewById(fb);
        card_view_facebook = (CardView) findViewById(R.id.card_face);



        shareDialog = new ShareDialog(this);
        login.setReadPermissions("public_profile email");

        details_dialog = new Dialog(this);
        details_dialog.setContentView(R.layout.dialog_details);
        details_dialog.setTitle("Details");


        getKeyHash();



        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(AccessToken.getCurrentAccessToken() != null) {

                    //profile.setProfileId(null);
                }
            }
        });

        login.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                facebook_id = f_name = m_name = l_name = profile_image = full_name = "";

                if (AccessToken.getCurrentAccessToken() != null) {
                    RequestData();
                    Profile profile = Profile.getCurrentProfile();
                    if (profile != null) {
                        facebook_id = profile.getId();
                        f_name = profile.getFirstName();
                        m_name = profile.getMiddleName();
                        l_name = profile.getLastName();
                        full_name = profile.getName();
                        profile_image = profile.getProfilePictureUri(400, 400).toString();
                    }
                    //   details.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancel() {
                Toast.makeText(MainActivity.this, "Login Cancelled", Toast.LENGTH_LONG).show();
//                Intent intent = new Intent(MainActivity.this, MainActivity.class);
//                finish();
//                startActivity(intent);
            }

            @Override
            public void onError(FacebookException exception) {
                Toast.makeText(MainActivity.this, "Invalid Login Details \n" + exception, Toast.LENGTH_LONG).show();
            }
        });

    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }




    // Initialize the dispatcher




    private void getKeyHash() {

        PackageInfo info;
        try{
             info = getPackageManager().getPackageInfo(
                    "com.grademojo.facebookpractice", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:",Base64.encodeToString(md.digest(), Base64.DEFAULT));

            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    }

    public void RequestData(){
        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {

                JSONObject json = response.getJSONObject();
                System.out.println("Json data :"+json);
                try {
                    if(json != null){
                        String text = "Name"+json.getString("name")+"Email : "+json.getString("email")+"Profile link : "+json.getString("link");
                        // details_txt.setText(Html.fromHtml(text));
                        //  details_txt.setText(json.getString(text));
                        profile.setProfileId(json.getString("id"));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,link,email,picture");
        request.setParameters(parameters);
        request.executeAsync();
    }



    public void click_card_image(View v) {
        if (v == card_view_facebook) {

            login.performClick();
        }
    }
    public void click_image(View v) {
        if (v == face_image) {
            login.performClick();
        }
    }

}



