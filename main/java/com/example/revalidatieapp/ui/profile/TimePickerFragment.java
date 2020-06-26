package com.example.revalidatieapp.ui.profile;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class TimePickerFragment extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Log.d("TimePickerFragment", "test(5) dit is TimePickerFragment");
        Log.d("TimePickerFragment", "test(5) dit is getParentFragment: " + getActivity());
        return new TimePickerDialog(getActivity(), (TimePickerDialog.OnTimeSetListener) getActivity(), 0, 0, true);
    }


}
