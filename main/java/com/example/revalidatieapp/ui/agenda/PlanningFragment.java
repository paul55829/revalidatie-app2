package com.example.revalidatieapp.ui.agenda;

import android.content.Context;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.alamkanak.weekview.MonthLoader;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;
import com.example.revalidatieapp.NavigationInterface;
import com.example.revalidatieapp.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class PlanningFragment extends Fragment implements WeekView.EventClickListener, MonthLoader.MonthChangeListener, WeekView.EventLongPressListener, WeekView.EmptyViewLongPressListener{

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private NavigationInterface navigationInterface;
    private ListView listView;

    private String userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_planning, container, false);

        userId = navigationInterface.getUserId();

        db.collection("users").document(userId).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Map user = documentSnapshot.getData();
                        HashMap<String, Timestamp> practiceTimes = (HashMap<String, Timestamp>) user.get("practiceTimes");

                        ArrayList<String> exerciseTimes = new ArrayList<>();

                        //for each practiceTime in practiceTimes there is an notification created
                        for (Map.Entry practiceTime : practiceTimes.entrySet()) {
                            //gets the timeStamp of the practiceTimes
                            Timestamp timestamp = (Timestamp) practiceTime.getValue();

                            //converts the Timestamp a date
                            Date date = timestamp.toDate();
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(date);
                            String time = calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE);
                            exerciseTimes.add(time);
                        }
                        createDayView(exerciseTimes);
                    }
                });

        // Get a reference for the week view in the layout.
        WeekView dayView = (WeekView) root.findViewById(R.id.dayView);

        // Show a toast message about the touched event.
        dayView.setOnEventClickListener(this);

        // The week view has infinite scrolling horizontally. We have to provide the events of a
        // month every time the month changes on the week view.
        dayView.setMonthChangeListener(this);

        // Set long press listener for events.
        dayView.setEventLongPressListener(this);

        // Set long press listener for empty view
        dayView.setEmptyViewLongPressListener(this);

        return root;
    }

    private void createDayView(ArrayList<String> exerciseTimes){

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

    @Override
    public List<? extends WeekViewEvent> onMonthChange(int newYear, int newMonth) {
        return null;
    }

    @Override
    public void onEmptyViewLongPress(Calendar time) {

    }

    @Override
    public void onEventClick(WeekViewEvent event, RectF eventRect) {

    }

    @Override
    public void onEventLongPress(WeekViewEvent event, RectF eventRect) {

    }
}
