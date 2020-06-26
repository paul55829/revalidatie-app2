package com.example.revalidatieapp.ui.exercise;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.revalidatieapp.R;

import java.util.ArrayList;

public class ExerciseAdapter extends BaseAdapter {

    LayoutInflater inflater;
    private ArrayList<Exercise> exercises;
    private static final String TAG = "ExerciseAdapter";

    public ExerciseAdapter(Context context, ArrayList<Exercise> theExercises){
        exercises = theExercises;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return exercises.size();
    }

    @Override
    public Object getItem(int position) {
        return exercises.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = inflater.inflate(R.layout.listview_exercise, null);

        TextView exerciseTextView = (TextView) v.findViewById(R.id.titleExercise);

        Exercise exercise =  exercises.get(position);
        String title = exercise.getTitle();
        Log.d(TAG, "getView: title = " + title);
        exerciseTextView.setText(title);

        return v;
    }

}
