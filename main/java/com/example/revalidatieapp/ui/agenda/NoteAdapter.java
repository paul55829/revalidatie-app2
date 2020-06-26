package com.example.revalidatieapp.ui.agenda;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.revalidatieapp.R;
import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.Date;

public class NoteAdapter extends BaseAdapter {

    private ArrayList<Note> notes;
    LayoutInflater inflater;

    public NoteAdapter(Context context, ArrayList<Note> theNotes) {
        notes = theNotes;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return notes.size();
    }

    @Override
    public Object getItem(int position) {
        return notes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = inflater.inflate(R.layout.listview_note, null);

        TextView titleTextView = (TextView) v.findViewById(R.id.titleTextView);
        TextView bodyTextView = (TextView) v.findViewById(R.id.bodyTextView);
        TextView timeTextView = (TextView) v.findViewById(R.id.timeTextView);

        Note note = notes.get(position);

        //gets title
        String title = note.getTitle();

        //gets body
        String body = note.getBody();

        //gets time
        Timestamp timestamp = note.getTime();
        Date date = timestamp.toDate();
        String time = date.toString();

        time = date.getDay() + "-" + date.getMonth() + "-" + (date.getYear() + 1900) + " " + date.getHours() + ":" + date.getMinutes();

        titleTextView.setText(title);
        bodyTextView.setText(body);
        timeTextView.setText(time);

        return v;
    }
}
