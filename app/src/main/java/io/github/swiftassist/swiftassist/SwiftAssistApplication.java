package io.github.swiftassist.swiftassist;

import android.app.Application;
import android.widget.Toast;

import com.firebase.client.Firebase;

/**
 * Created by avik on 2/3/2016.
 */
public class SwiftAssistApplication extends Application {
    private Firebase mainFirebaseRef;
    @Override
    public void onCreate(){
        super.onCreate();
        Toast.makeText(SwiftAssistApplication.this, "Application onCreate()", Toast.LENGTH_SHORT).show();        // set application context for Firebase database
        Firebase.setAndroidContext(this.getApplicationContext());

        // Create reference to shared SwiftAssist database
        mainFirebaseRef = new Firebase("https://swiftassist.firebaseio.com/");
    }

    public Firebase getFirebaseRef(){
        return mainFirebaseRef;
    }

}
