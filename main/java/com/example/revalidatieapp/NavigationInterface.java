package com.example.revalidatieapp;

import android.os.Bundle;

public interface NavigationInterface {

    public void switchFragment(int fragmentId, Bundle args);

    public void switchFragment(int fragmentId);

    public String getUserId();

}
