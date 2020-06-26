package com.example.revalidatieapp.ui.agenda;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.revalidatieapp.R;

import java.util.ArrayList;

public class ExerciseAdapter extends BaseAdapter {

    LayoutInflater inflater;
    ArrayList<String> exercises;

    public ExerciseAdapter(Context context, ArrayList<String> exercises) {
        this.exercises = exercises;
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
        View v = inflater.inflate(R.layout.listview_exercise_planning, null);

        TextView exerciseTimeTextView = (TextView) v.findViewById(R.id.exerciseTime);
        exerciseTimeTextView.setText(exercises.get(position));

        return v;
    }
}
