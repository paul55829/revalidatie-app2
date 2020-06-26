package com.example.revalidatieapp.ui.messages;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.revalidatieapp.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class MessageFragment extends Fragment {

    private String userId;
    private String messageId;

    private TextView from;
    private TextView message;
    private TextView subject;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_message, container, false);
        Log.d("MessageFragment", "TEST0");
        Bundle bundle = getArguments();
        String[] info = bundle.getStringArray("com.example.revalidatieapp.INFO");
        messageId = info[0];
        userId = info[1];


        Log.d("MessageFragment", "this is the username: " + userId);

        from = (TextView) root.findViewById(R.id.textViewFrom);
        message = (TextView) root.findViewById(R.id.textViewMessage);
        subject = (TextView) root.findViewById(R.id.textViewSubject);

        db.collection("users").document(userId).collection("messages").document(messageId).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String from = documentSnapshot.getString("from");
                        String subject = documentSnapshot.getString("subject");
                        String message = documentSnapshot.getString("message");
                        message = message.replace("\\n","\n");

                        setFrom(from);
                        setSubject(subject);
                        setMessage(message);
                        setOpened();
                    }
                });

        return root;
    }

    private void setFrom(String theFrom){
        from.setText("Ontvangen van " + theFrom);
    }

    private void setMessage(String theMessage){
        message.setText(theMessage);
    }

    private void setSubject(String theSubject){
        subject.setText(theSubject);
    }

    private void setOpened(){
        HashMap<String, Object> update = new HashMap<>();
        update.put("opened", true);
        db.collection("users").document(userId).collection("messages").document(messageId).update(update);
    }
}
//