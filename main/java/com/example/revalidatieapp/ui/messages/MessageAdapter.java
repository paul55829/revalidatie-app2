package com.example.revalidatieapp.ui.messages;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.revalidatieapp.R;
import java.util.ArrayList;

public class MessageAdapter extends BaseAdapter {

    LayoutInflater inflater;
    private ArrayList<Message> messages;
    private String TAG = "MessageAdapter";

    public MessageAdapter(Context context, ArrayList<Message> theMessages){
        messages = theMessages;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public Object getItem(int position) {
        return messages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d(TAG, "test01");
        View v = inflater.inflate(R.layout.listview_message, null);
        TextView fromTextView = (TextView) v.findViewById(R.id.from);
        TextView subjectTextView = (TextView) v.findViewById(R.id.subject);
        TextView timeView = (TextView) v.findViewById(R.id.time);
        ImageView imageView = (ImageView) v.findViewById(R.id.imageView);

        Log.d(TAG, "test00");
        String from = messages.get(position).getFrom();
        String subject = messages.get(position).getSubject();
        String time = messages.get(position).getTime();
        boolean opened = messages.get(position).isOpened();
        Log.d(TAG, "message: " + subject + "\n" + "from: " + from + "\n" + "time: " + time + "\n" + "is opened: " + opened);

        fromTextView.setText(from);
        subjectTextView.setText(subject);
        timeView.setText(time);

        if(opened){
            imageView.setImageResource(R.drawable.open_envelope);
        }
        else{
            imageView.setImageResource(R.drawable.closed_envelope);
        }

        return v;
    }
}
