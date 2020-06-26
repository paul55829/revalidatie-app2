package com.example.revalidatieapp.ui.information;

import android.Manifest;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.revalidatieapp.R;
import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;

public class FolderFragment extends Fragment {

    private static final int PERMISSION_STORAGE_CODE = 1000;
    private String TAG = "FolderFragment";
    private Uri url;
    private String name;
    private PDFView pdfView;
    private File file;

    private long downloadID;

    //when a file is downloaded this is executed
    BroadcastReceiver onDownloadComplete = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Fetching the download id received with the broadcast
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            //Checking if the received broadcast is for our enqueued download by matching download id
            if (downloadID == id) {
                pdfView.fromFile(file).load();
            }
        }
    };

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_folder, container, false);
        pdfView = (PDFView) root.findViewById(R.id.folder);

        //gets the name and url of the file that has to be showed
        Bundle bundle = getArguments();
        String[] info = bundle.getStringArray("com.example.revalidatieapp.INFO");
        url = Uri.parse(info[0]);
        name = info[1];

        //creates reference to the file
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        file = new File(path, name + ".pdf");


        if(file.exists()){
            //if file is allready in directory the file is showed
            pdfView.fromFile(file).load();
        }
        else{
            //if file is not in directory it will be downloaded
            startDownloadProces();
        }

        return root;
    }

     //handles permissions and then starts the startDownloading method
    private void startDownloadProces(){
        //handles runtime permission for OS Marshmallow and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getContext().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                //permission denied, request it
                String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                //show popup for runtime permission
                requestPermissions(permissions, PERMISSION_STORAGE_CODE);
            }
            else {
                //permission already granted, perform download
                startDownloading();
            }
        }
        else {
            //system os is less than marshmallow, perform download
            startDownloading();
        }
    }


    //starts downloading the url
    private void startDownloading(){

        //create download request
        DownloadManager.Request request = new DownloadManager.Request(url);

        //allow types of network to download file
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);

        //set title and description in download notification
        request.setTitle("Download");
        request.setDescription("Downloading the folder....");

        request.allowScanningByMediaScanner();;
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, name + ".pdf");

        //downloads file
        DownloadManager manager = (DownloadManager) getContext().getSystemService(Context.DOWNLOAD_SERVICE);
        downloadID = manager.enqueue(request);
    }


    //handles permission result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_STORAGE_CODE:{
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //permission granted from popup, perform download
                    startDownloading();
                }
                else {
                    Toast.makeText(getContext(), "cannot load folder", Toast.LENGTH_SHORT);
                }
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        getContext().registerReceiver(onDownloadComplete,filter);
    }

    @Override
    public void onStop() {
        super.onStop();
        getContext().unregisterReceiver(onDownloadComplete);
    }
}
