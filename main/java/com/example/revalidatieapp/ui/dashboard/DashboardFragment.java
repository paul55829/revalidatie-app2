package com.example.revalidatieapp.ui.dashboard;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.revalidatieapp.NavigationInterface;
import com.example.revalidatieapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class DashboardFragment extends Fragment {

    private DashboardViewModel dashboardViewmodel;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private NavigationInterface navigationInterface;

    private static final String TAG = "DashboardFragment";
    private String userId;

    private TextView goal1TextView;
    private TextView goal2TextView;
    private TextView firstNameTextView;
    private TextView messagesTextView;
    private Button makeNotificationButton;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        dashboardViewmodel = ViewModelProviders.of(this).get(DashboardViewModel.class);
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        userId = navigationInterface.getUserId();

//        final TextView textView = (TextView) root.findViewById(R.id.text_dashboard);
//        dashboardViewmodel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {

//            @Override
//            public void onChanged(String s) {
//                textView.setText(s);
//            }
//        });
        firstNameTextView = (TextView) root.findViewById(R.id.firstNameTextView);
        goal1TextView = (TextView) root.findViewById(R.id.goal1);
        goal2TextView = (TextView) root.findViewById(R.id.goal2);
        TextView goalsTextView = (TextView) root.findViewById(R.id.goalsTextView);
        goalsTextView.setText(R.string.dashboard_goals);
        messagesTextView = (TextView) root.findViewById(R.id.messagesTextView);


        db.collection("users").document(userId).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String firstName = documentSnapshot.getString("firstName");
                        String greeting = getResources().getString(R.string.dashboard_greeting);
                        firstNameTextView.setText(greeting + " " +firstName + ",");

                        try {
                            Map user = documentSnapshot.getData();
                            HashMap<String, String> goals = (HashMap<String, String>) user.get("goals");
                            String goal1 = goals.get("goal1");
                            String goal2 = goals.get("goal2");

                            goal1TextView.setText("- " + goal1);
                            goal2TextView.setText("- " + goal2);
                        }
                        catch(Exception exception){
                            Log.d(TAG, "dit is de error: " + exception.toString());
                        }
                    }
                });

        db.collection("users").document(userId).collection("messages").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        int messages = 0;
                        for (QueryDocumentSnapshot message : queryDocumentSnapshots) {
                            Boolean opened = message.getBoolean("opened");
                            if (!opened) {
                                messages++;
                            }
                        }
                        if (messages > 0) {
                            messagesTextView.setText("U heeft " + messages + " ongelezen bericht(en)");
                        }
                        else{
                            messagesTextView.setText(R.string.dashboard_no_new_messages);
                            Log.d(TAG, "onSuccess: else statement has been executed");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        messagesTextView.setText(R.string.dashboard_no_new_messages);
                        Log.d(TAG, "onFailure: onFailure method has been executed");
                    }
                });

        makeNotificationButton = (Button) root.findViewById(R.id.makeNotificationButton);
        makeNotificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 navigationInterface.switchFragment(R.id.fragment_create_notification);
            }
        });

        return root;
    }

    //instantiates a Navigationinterface
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof NavigationInterface){
            navigationInterface = (NavigationInterface) context;
        }
        else{
            Toast.makeText(context, "something went wrong", Toast.LENGTH_SHORT).show();
        }
    }

}
