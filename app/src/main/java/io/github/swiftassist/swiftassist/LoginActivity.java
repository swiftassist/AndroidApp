package io.github.swiftassist.swiftassist;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class LoginActivity extends Activity {
    EditText passwordField;
    EditText emailAddressField;
    Button loginButton;
    Button toSignUpButton;
    Firebase mainFirebaseRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mainFirebaseRef = ((SwiftAssistApplication)getApplication()).getFirebaseRef();

        // get UI elements from view
        initViews();

        // if the user is logged in, go to the main page
        if(mainFirebaseRef.getAuth() != null){
            //Toast.makeText(this, "Auth: "+mainFirebaseRef.getAuth().toString(), Toast.LENGTH_LONG).show();//startActivity(new Intent(getApplicationContext(), EmergencyButtonActivity.class));
            startActivity(new Intent(this, EmergencyActivity.class));
        }

        // respond to loginButton click
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainFirebaseRef.authWithPassword(emailAddressField.getText().toString(),
                        passwordField.getText().toString(),
                        new Firebase.AuthResultHandler() {
                            @Override
                            public void onAuthenticated(AuthData authData) {
                                // go to the main screen when the user logs in
                                startActivity(new Intent(getApplicationContext(), EmergencyActivity.class));
                            }

                            @Override
                            public void onAuthenticationError(FirebaseError firebaseError) {
                                String errorMessage;
                                switch(firebaseError.getCode()){
                                    case FirebaseError.INVALID_AUTH_ARGUMENTS:
                                        errorMessage = "Please enter your email and password to log in.";
                                        break;
                                    case FirebaseError.INVALID_EMAIL:
                                        errorMessage = "Invalid email. Please re-enter your email.";
                                        break;
                                    case FirebaseError.INVALID_PASSWORD:
                                        errorMessage = "Authentication error. Please re-enter your password.";
                                        break;
                                    default:
                                        errorMessage = "Authentication error. Please re-enter your login information";
                                }
                                Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                            }
                        }
                );
            }
        });


        toSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO Go to Sign up page
            }
        });
    }

    /**
     * Gets UI elements from layout
     */
    public void initViews(){
        emailAddressField = (EditText)findViewById(R.id.loginEmailAddressField);
        passwordField = (EditText)findViewById(R.id.loginPasswordField);
        loginButton = (Button)findViewById(R.id.loginButton);
        toSignUpButton = (Button)findViewById(R.id.loginToSignUpButton);
    }
    /**
     * Checks if String is a valid email address
     * Regex for email address taken from emailregex.com
     *
     * @param email  String to check if is valid email
     * @return boolean true for valid false for invalid
     */
    public static boolean isEmailValid(String email) {
        boolean isValid = false;

        String expression = "^[-a-z0-9~!$%^&*_=+}{\\'?]+(\\.[-a-z0-9~!$%^&*_=+}{\\'?]+)"
                + "*@([a-z0-9_][-a-z0-9_]*(\\.[-a-z0-9_]+)*\\"
                + ".(aero|arpa|biz|com|coop|edu|gov|info|int|mil|museum|name|net|org|pro|travel|mobi|"
                + "[a-z][a-z])|([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}))(:[0-9]{1,5})?$";

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }
}
