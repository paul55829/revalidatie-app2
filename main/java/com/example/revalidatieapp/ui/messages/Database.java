package com.example.revalidatieapp.ui.messages;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class Database {

    private String userId;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String TAG = "Database";
    private MutableLiveData<ArrayList<Message>> messages;

    public Database(String theUserId){
        userId = theUserId;
    }

    public MutableLiveData<ArrayList<Message>> getMessages(){
        Log.d(TAG, "test(0)");
        db.collection("users").document(userId).collection("messages").orderBy("date", Query.Direction.DESCENDING).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        Log.d(TAG, "test(0) nu begint de tweede onsucces");
                        ArrayList<Message> messages = new ArrayList<>();

                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            String subject = documentSnapshot.getString("subject");
                            String from = documentSnapshot.getString("from");
                            String time = documentSnapshot.getString("time");
                            String id = documentSnapshot.getId();
                            boolean opened = documentSnapshot.getBoolean("opened");

                            Message extraMessage = new Message();
                            extraMessage.setFrom(from);
                            extraMessage.setSubject(subject);
                            extraMessage.setTime(time);
                            extraMessage.setId(id);
                            extraMessage.setOpened(opened);
                            messages.add(extraMessage);

                        }
                        setMessages(messages);
                    }
                });

        if(messages != null){
            Log.d(TAG, "test(0) size messsages = "+ messages.getValue().size());
        }
        else{
            Log.d(TAG, "test(0) messages is null");
        }
        return messages;
    }

    private void setMessages(ArrayList<Message> theMessages){
        Log.d(TAG, "test(0) nu begint de for loop test0");

        Log.d(TAG, "test(0) dit is de lengte van messages: " + theMessages.size());

        for(int i = 0; i< theMessages.size(); i++){
            Log.d(TAG,
                    "test(0) from is " + theMessages.get(i).getFrom() + "\n" +
                            "subject is " + theMessages.get(i).getSubject() + "\n" +
                            "time is " + theMessages.get(i).getTime() + "\n" +
                            "opened is " + theMessages.get(i).isOpened()
            );
        }


        messages.setValue(theMessages);
        Log.d(TAG, "test(0) nu begint de for loop test");
    }
}
