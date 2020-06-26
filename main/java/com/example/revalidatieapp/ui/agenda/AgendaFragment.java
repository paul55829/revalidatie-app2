package com.example.revalidatieapp.ui.agenda;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.revalidatieapp.Alarm;
import com.example.revalidatieapp.NavigationInterface;
import com.example.revalidatieapp.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class AgendaFragment extends Fragment {

    private NavigationInterface navigationInterface;
    private String userId;
    private static final String TAG = "AgendaFragment";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_agenda, container, false);

        userId = navigationInterface.getUserId();

        Button createNoteButton = (Button) root.findViewById(R.id.saveNoteButton);
        createNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigationInterface.switchFragment(R.id.fragment_create_notification);
            }
        });

        Button practicePlanningButton = (Button) root.findViewById(R.id.practicePlanningButton);
        practicePlanningButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigationInterface.switchFragment(R.id.fragment_practice_planning);
            }
        });

        Button startPracticeButton = (Button) root.findViewById(R.id.startPracticeButton);
        startPracticeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //plans the practiceTimes and sets notifications at those times
                Alarm alarm = new Alarm(getContext(), userId);
                alarm.setAlarmWithNewPracticeTimes();
            }
        });

        final ListView listviewNotes = (ListView) root.findViewById(R.id.listviewNotes);

        //gets the notes and creates a list of them.
        db.collection("users").document(userId).collection("notes").orderBy("date", Query.Direction.DESCENDING).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        ArrayList<Note> notes = new ArrayList<>();

                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            String title = documentSnapshot.getString("title");
                            String body = documentSnapshot.getString("note");
                            Timestamp time = documentSnapshot.getTimestamp("date");

                            Note note = new Note();
                            note.setTitle(title);
                            note.setBody(body);
                            note.setTime(time);

                            notes.add(note);
                        }

                        NoteAdapter noteAdapter = new NoteAdapter(getContext(), notes);
                        listviewNotes.setAdapter(noteAdapter);
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
