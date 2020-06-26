package com.example.revalidatieapp.ui.messages;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.revalidatieapp.NavigationInterface;
import com.example.revalidatieapp.R;
import com.example.revalidatieapp.TimeConverter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MessagesFragment extends Fragment {

    private NavigationInterface navigationInterface;
    private MessagesViewModel messagesViewModel;
    private String userId;
    private ListView theListView;
    private String TAG = "MessagesFragment";
    private ArrayList<Message> messages;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        messagesViewModel = ViewModelProviders.of(this).get(MessagesViewModel.class);
        View root = inflater.inflate(R.layout.fragment_messages, container, false);
        theListView = (ListView) root.findViewById(R.id.listViewMessages);
        /*

        messagesViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        */

        userId = navigationInterface.getUserId();

        //gets the messages and creates a list of them.
        db.collection("users").document(userId).collection("messages").orderBy("date", Query.Direction.DESCENDING).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        Log.d(TAG, "nu begint de tweede onsucces");
                        ArrayList<Message> messages = new ArrayList<>();

                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            //gets the time from database and converts the time from datatype timestamp to datatype String.
                            Timestamp timestamp = documentSnapshot.getTimestamp("date");
                            Date date = timestamp.toDate();

                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(date);

                            Log.d(TAG, "onSuccess: timestamp = " + timestamp.toString());
                            Log.d(TAG, "onSuccess: date = " + date.toString());

                            Log.d(TAG, "onSuccess: day = " + date.getDay());
                            Log.d(TAG, "onSuccess: month = " + date.getMonth());

                            String day = TimeConverter.convertTimeIntToString(calendar.get(Calendar.DAY_OF_MONTH));
                            String month =TimeConverter.convertTimeIntToString(calendar.get(Calendar.MONTH) + 1);
                            int yearInt = date.getYear() + 1900;
                            String year = yearInt + "";

                            String time = day + "-" + month + "-" + year + " " + date.getHours() + ":" + date.getMinutes();

                            //gets subject, from and id from the database
                            String subject = documentSnapshot.getString("subject");
                            String from = documentSnapshot.getString("from");
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
                        MessageAdapter messageAdapter = new MessageAdapter(getContext(), messages);
                        theListView.setAdapter(messageAdapter);
                    }
                });

        //when the user clicks on a messages, this will switch the fragment to that of the specific message.
        theListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Message message = getMessages().get(position);
                String theId = message.getId();

                Bundle args = new Bundle();
                String[] info = {theId, userId};
                args.putStringArray("com.example.revalidatieapp.INFO", info);

                int fragmentId = R.id.fragment_message;

                navigationInterface.switchFragment(fragmentId, args);

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

    public void setMessages(ArrayList<Message> theMessages){
        messages = theMessages;
    }

    public ArrayList<Message> getMessages(){
        return messages;
    }
}
