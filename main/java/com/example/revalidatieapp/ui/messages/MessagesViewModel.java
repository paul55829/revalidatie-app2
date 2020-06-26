package com.example.revalidatieapp.ui.messages;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;


public class MessagesViewModel extends AndroidViewModel {

    private MutableLiveData<String> mText;
    private Database database;
    private String userId;

    public MessagesViewModel(@NonNull Application application) {
        super(application);
        mText = new MutableLiveData<>();
        mText.setValue("this is message fragment");
    }

    public void setUserId(String theUserId){
        userId = theUserId;
    }

    public String getUserId(){
        return userId;
    }
}