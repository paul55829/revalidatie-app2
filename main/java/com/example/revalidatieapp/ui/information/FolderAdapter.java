package com.example.revalidatieapp.ui.information;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.revalidatieapp.R;

import java.util.ArrayList;

public class FolderAdapter extends BaseAdapter {

    LayoutInflater inflater;
    private ArrayList<Folder> folders;
    private String TAG = "MessageAdapter";

    public FolderAdapter(Context context, ArrayList<Folder> theFolders){
        folders = theFolders;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return folders.size();
    }

    @Override
    public Object getItem(int position) {
        return folders.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = inflater.inflate(R.layout.listview_folder, null);

        TextView titleTextView = (TextView) v.findViewById(R.id.title);
        TextView descriptionTextView = (TextView) v.findViewById(R.id.description);

        titleTextView.setText(folders.get(position).getTitle());
        descriptionTextView.setText((folders.get(position).getDescription()));

        return v;
    }
}
