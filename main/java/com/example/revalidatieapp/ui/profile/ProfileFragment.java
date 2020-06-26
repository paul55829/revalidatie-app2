package com.example.revalidatieapp.ui.profile;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.example.revalidatieapp.Alarm;
import com.example.revalidatieapp.NavigationInterface;
import com.example.revalidatieapp.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;

public class ProfileFragment extends Fragment{

    private NavigationInterface navigationInterface;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Switch switchReminders;
    private String userId;
    private TextView timeTextField;
    private String TAG = "ProfileFragment";
    int practiceDurationIndex;
    int restDurationIndex;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_profile, container, false);
        userId = navigationInterface.getUserId();

        //when the fragment is created, the setonItemListener is activated and position 0 is given as parameter.
        //this causes the practiceDuration and restDuration to be updated to a wrong value.
        //when the fragment is created practiceDurationIndex and restDurationIndex are 0
        // At the end of onItemSelectedListener the index is increased by 1 so when the index is 0 everything inside the listener shoudn't be executed.
        practiceDurationIndex = 0;
        restDurationIndex = 0;

        final Spinner spinnerPracticeDuration = (Spinner) root.findViewById(R.id.spinnerPracticeDuration);
        ArrayAdapter<CharSequence> adapterPracticeDuration = ArrayAdapter.createFromResource(getContext(), R.array.spinner_practice_duration, android.R.layout.simple_spinner_item);
        adapterPracticeDuration.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPracticeDuration.setAdapter(adapterPracticeDuration);

        spinnerPracticeDuration.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(practiceDurationIndex != 0){
                    String practiceDurationString = parent.getItemAtPosition(position).toString();
                    int practiceDurationInteger =  Integer.parseInt(practiceDurationString);
                    HashMap<String, Object> update = new HashMap<>();
                    update.put("practiceDuration", practiceDurationInteger);
                    db.collection("users").document(userId).update(update);
                }
                else{
                    db.collection("users").document(userId).get()
                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    try {
                                        //displays the existing variable practiceDuration on the screen
                                        int practiceDurationDatabase = Integer.parseInt(documentSnapshot.getLong("practiceDuration").toString());
                                        String[] practiceDurationsString= getResources().getStringArray(R.array.spinner_practice_duration);

                                        for(int i = 0; i < practiceDurationsString.length;i++) {
                                            int practiceDurationInt = Integer.parseInt(practiceDurationsString[i]);
                                            if (practiceDurationInt == practiceDurationDatabase) {
                                                spinnerPracticeDuration.setSelection(i);
                                            }
                                        }
                                    }
                                    catch(NullPointerException e) {
                                        //the variable practiceDuration doesn't exist, the variable is created and assigned first value of the spinner_practice_duration array.
                                        String[] practiceDurationsString= getResources().getStringArray(R.array.spinner_practice_duration);
                                        HashMap<String, Object> update = new HashMap<>();
                                        update.put("practiceDuration", 5);
                                        db.collection("users").document(userId).update(update);
                                    }
                                }
                            });
                }
                practiceDurationIndex++;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        final Spinner spinnerRestDuration = (Spinner) root.findViewById(R.id.spinnerRestDuration);
        ArrayAdapter<CharSequence> adapterRestDuration = ArrayAdapter.createFromResource(getContext(), R.array.spinner_rest_duration, android.R.layout.simple_spinner_item);
        adapterRestDuration.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRestDuration.setAdapter(adapterRestDuration);

        spinnerRestDuration.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (restDurationIndex != 0){
                    String restDuration = parent.getItemAtPosition(position).toString();
                    HashMap<String, Object> update = new HashMap<>();
                    update.put("restDuration", restDuration);
                    db.collection("users").document(userId).update(update);
                }
                else{
                    db.collection("users").document(userId).get()
                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    try {
                                        String restDurationDatabase = documentSnapshot.getString("restDuration");
                                        if(restDurationDatabase != null) {
                                            //displays the existing variable restDuration on the screen
                                            String[] restTimeStrings= getResources().getStringArray(R.array.spinner_rest_duration);

                                            for(int i = 0; i < restTimeStrings.length;i++){
                                                String restTimeString = restTimeStrings[i];
                                                if(restTimeString.equals(restDurationDatabase)){
                                                    spinnerRestDuration.setSelection(i);
                                                }
                                            }
                                        }
                                        else {
                                            //the variable restDuration doesn't exist, the variable is created and assigned first value of the spinner_rest_duration array.
                                            String[] restTimeStrings= getResources().getStringArray(R.array.spinner_rest_duration);
                                            HashMap<String, Object> update = new HashMap<>();
                                            update.put("restDuration", restTimeStrings[0]);
                                            db.collection("users").document(userId).update(update);
                                        }

                                    }
                                    catch(NullPointerException e) {
                                        Toast.makeText(getContext(), "something went wrong", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });
                }
                restDurationIndex++;

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        switchReminders = (Switch) root.findViewById(R.id.switchReminders);
        timeTextField = (TextView) root.findViewById(R.id.timeTextField);

        db.collection("users").document(userId).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        try {
                            boolean receiveReminders = documentSnapshot.getBoolean("receiveReminders");
                            if (receiveReminders) {
                                switchReminders.setChecked(true);
                                switchReminders.setText(R.string.notification_on);
                            } else {
                                switchReminders.setChecked(false);
                                switchReminders.setText(R.string.notification_off);

                            }
                         }
                        catch(NullPointerException e) {
                            HashMap<String, Object> update = new HashMap<>();
                            update.put("receiveReminders", getResources().getBoolean(R.bool.profile_page_default_receive_reminders));
                            db.collection("users").document(userId).update(update);

                            switchReminders.setChecked(true);
                            switchReminders.setText(R.string.notification_on);
                        }
                    }
                });


        switchReminders.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    switchReminders.setText(R.string.notification_on);
                    changeReceiveReminders(true);
                }
                else {
                    switchReminders.setText(R.string.notification_off);
                    changeReceiveReminders(false);
                }
            }
        });

        Button changeTimeButton = (Button) root.findViewById(R.id.changeTimeButton);
        changeTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getActivity().getSupportFragmentManager(), "time picker");
            }
        });

        Button changePasswordButton = (Button) root.findViewById(R.id.changePasswordButton);
        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigationInterface.switchFragment(R.id.fragment_change_password);
            }
        });


        return root;
    }



    @Override
    public void onStart() {
        super.onStart();
        db.collection("users").document(userId)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                        if (documentSnapshot.exists()) {
                            try {
                                Map user = documentSnapshot.getData();
                                HashMap<String, String> bedtime = (HashMap<String, String>) user.get("bedtime");
                                String hour = bedtime.get("hour");
                                String minutes = bedtime.get("minute");

                                timeTextField.setText("meldingen stoppen om " + hour + ":" + minutes);
                            }
                            catch(NullPointerException exception){
                                HashMap<String, Object> bedtime = new HashMap<>();
                                HashMap<String, Object> update = new HashMap<>();

                                bedtime.put("hour", getResources().getString(R.string.profile_page_default_hour));
                                bedtime.put("minute", getResources().getString(R.string.profile_page_default_minute));
                                update.put("bedtime", bedtime);
                                db.collection("users").document(userId).update(update);
                            }
                            catch (Exception exception) {
                                timeTextField.setText("error: " + exception.toString());
                            }
                        }
                    }
                });
    }

    private void changeReceiveReminders(boolean receiveReminders){
        HashMap<String, Object> update = new HashMap<>();
        update.put("receiveReminders", receiveReminders);
        db.collection("users").document(userId).update(update);

        Alarm alarm = new Alarm(getContext(), userId);
        if (receiveReminders) {
            alarm.setAlarmWithCurrentTimes();
        }
        else {

            alarm.cancelAlarms();
        }
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
