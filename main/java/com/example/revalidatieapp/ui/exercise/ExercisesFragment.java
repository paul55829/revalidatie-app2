package com.example.revalidatieapp.ui.exercise;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.revalidatieapp.NavigationInterface;
import com.example.revalidatieapp.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class ExercisesFragment extends Fragment {

    private String userId;
    private NavigationInterface navigationInterface;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private ArrayList<Exercise> exercises = new ArrayList<>();
    private ListView theListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_exercises, container, false);

        userId = navigationInterface.getUserId();
        theListView = (ListView) root.findViewById(R.id.exerciseListview);

        db.collection("users").document(userId).collection("exercises").whereLessThan("startDate", Timestamp.now()).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        ArrayList<String> exerciseIds = new ArrayList<>();
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            String exerciseId = documentSnapshot.getString("id");
                            exerciseIds.add(exerciseId);
                        }

                        for(String exerciseId: exerciseIds){
                            db.collection("exercises").document(exerciseId).get()
                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            String title = documentSnapshot.getString("title");
                                            String url = documentSnapshot.getString("url");

                                            Exercise exercise = new Exercise();
                                            exercise.setTitle(title);
                                            exercise.setUrl(url);
                                            exercises.add(exercise);

                                            createListView();
                                        }
                                    });
                        }
                    }
                });

        //when the user clicks on a exercise, this will switch the fragment to that of the specific exercise.
        theListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Exercise exercise = exercises.get(position);
                String url = exercise.getUrl();
                String title = exercise.getTitle();
                String[] info = {url, title};

                Bundle args = new Bundle();
                args.putStringArray("com.example.revalidatieapp.INFO_EXERCISE", info);

                int fragmentId = R.id.fragment_exercise;

                navigationInterface.switchFragment(fragmentId, args);

            }
        });

        return root;
    }

    public void createListView(){
        ExerciseAdapter exerciseAdapter = new ExerciseAdapter(getContext(), exercises);
        theListView.setAdapter(exerciseAdapter);
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