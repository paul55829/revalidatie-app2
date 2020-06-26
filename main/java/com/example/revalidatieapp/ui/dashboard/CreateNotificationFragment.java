package com.example.revalidatieapp.ui.dashboard;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.revalidatieapp.NavigationInterface;
import com.example.revalidatieapp.R;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;


public class CreateNotificationFragment extends Fragment {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private NavigationInterface navigationInterface;

    private String userId;

    private EditText notificationEditText;
    private EditText titleEditText;
    private Button saveNoteButton;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_create_notification, container, false);
        userId = navigationInterface.getUserId();

        titleEditText = (EditText) root.findViewById(R.id.titleEditText);
        notificationEditText = (EditText) root.findViewById(R.id.notificationEditText);
        saveNoteButton = (Button) root.findViewById(R.id.saveNoteButton);

        saveNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = titleEditText.getText().toString();
                String note = notificationEditText.getText().toString();
                Timestamp date = Timestamp.now();

                HashMap<String, Object> update = new HashMap<>();
                update.put("title", title);
                update.put("note", note);
                update.put("date", date);

                db.collection("users").document(userId).collection("notes").add(update);
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
            Toast.makeText(context, "something went wrong", Toast.LENGTH_SHORT);
        }
    }
}

