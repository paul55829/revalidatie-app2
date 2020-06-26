package com.example.revalidatieapp.ui.information;

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

import com.example.revalidatieapp.NavigationInterface;
import com.example.revalidatieapp.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

    public class FoldersFragment extends Fragment {

        private FirebaseFirestore db = FirebaseFirestore.getInstance();
        private NavigationInterface navigationInterface;
        private ListView theListView;
        private String userId;
        private String TAG = "FoldersFragment";

        private ArrayList<Folder> folders;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_folders, container, false);

        theListView = (ListView) root.findViewById(R.id.foldersListview);
        userId = navigationInterface.getUserId();

        folders = new ArrayList<>();

        db.collection("users").document(userId).collection("folders").whereLessThan("startDate", Timestamp.now()).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        Log.d(TAG, "test(2) eerste onSucces begint");
                        ArrayList<String> folderIds = new ArrayList<>();
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            String folderId = documentSnapshot.getString("folderId");
                            folderIds.add(folderId);
                        }

                        int i = 0;
                        for(String folderId: folderIds){
                            Log.d(TAG, "test(2) folderId = " + folderId);
                            db.collection("folders").document(folderId).get()
                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            Log.d(TAG, "test(2) tweede onSucces begint");
                                            String title = documentSnapshot.getString("title");
                                            String description = documentSnapshot.getString("description");
                                            String url = documentSnapshot.getString("url");
                                            String name = title;

                                            Log.d(TAG, "test(2) " + "title = " + title + "\n" + "description = " + description);

                                            Folder folder = new Folder();
                                            folder.setDescription(description);
                                            folder.setTitle(title);
                                            folder.setUrl(url);
                                            folder.setName(name);
                                            addFolder(folder);

                                            createListView();
                                        }
                                    });
                        }
                    }
                });

        //when the user clicks on a messages, this will switch the fragment to that of the specific message.
        theListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Folder folder = getFolders().get(position);
                String url = folder.getUrl();
                String name = folder.getName();
                String[] info = {url, name};

                Bundle args = new Bundle();
                args.putStringArray("com.example.revalidatieapp.INFO", info);

                int fragmentId = R.id.fragment_folder;

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

    public void addFolder(Folder folder){
        folders.add(folder);
    }

    public ArrayList<Folder> getFolders() {
        return folders;
    }

    public void createListView(){
        FolderAdapter folderAdapter = new FolderAdapter(getContext(), folders);
        theListView.setAdapter(folderAdapter);
    }

}
