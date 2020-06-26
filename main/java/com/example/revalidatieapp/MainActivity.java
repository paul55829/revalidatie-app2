package com.example.revalidatieapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

/**
 * this class handles the login page
 * @auther Paul Kwakernaak
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private EditText editTextEmail;
    private EditText editTextPassword;

    //private FirebaseAnalytics mFirebaseAnalytics;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference usersRef = db.collection("users");

    /**
     * is executed when the activity is created
     * @param savedInstanceState a Bundle object containing the activity's previously saved state. It can be null if this is the first activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editTextEmail = findViewById(R.id.edit_email);
        editTextPassword = findViewById(R.id.edit_password);
        Log.d(TAG, "test(1) einde oncreate mainActivity");
    }

    /**
     * is executed when the user presses the login button. If the combination of username and password is correct,
     * the user is send to the navigation page with the dash fragment. If it isn't correct, the user will receive a toast message.
     * @param v contiains the view
     */
    public void login(View v){
        final String email = editTextEmail.getText().toString();
        final String password = editTextPassword.getText().toString();

        //gets the user from the database
        usersRef.whereEqualTo("email", email).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        //i will save the number of users that are returned from the database.
                        int i = 0;
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                            i++;
                            String correctPassword = documentSnapshot.getString("password");
                            if (password.equals(correctPassword)){
                                //creates the intend which will send the user to the navigation page and sends the userId along.
                                Intent startIntent = new Intent(getApplicationContext(), Navigation.class);
                                startIntent.putExtra("com.example.revalidatieapp.USERID", documentSnapshot.getId());
                                startActivity(startIntent);
                            }
                            else{
                                Toast.makeText(MainActivity.this, R.string.invalid_login_credentials, Toast.LENGTH_SHORT).show();
                            }
                        }
                        if (i == 0){
                            //if is 0 there were no users found with the given username.
                            Toast.makeText(MainActivity.this, R.string.invalid_login_credentials, Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "something went wrong", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
