package com.example.revalidatieapp.ui.profile;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.revalidatieapp.NavigationInterface;
import com.example.revalidatieapp.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;


public class ChangePasswordFragment extends Fragment {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private NavigationInterface navigationInterface;
    private String userId;

    private TextInputLayout oldPassword;
    private TextInputLayout newPassword;
    private TextInputLayout repeatNewPassword;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_change_password, container, false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        userId = navigationInterface.getUserId();

        oldPassword = (TextInputLayout) root.findViewById(R.id.oldPassword);
        newPassword = (TextInputLayout) root.findViewById(R.id.newPassword);
        repeatNewPassword = (TextInputLayout) root.findViewById(R.id.repeatNewPassword);

        Button changePasswordButon = (Button) root.findViewById(R.id.confirmNewPasswordButton);
        changePasswordButon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("users").document(userId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String realCurrentPassword = documentSnapshot.getString("password");
                        String oldPasswordInput = oldPassword.getEditText().getText().toString();
                        String newPasswordInput = newPassword.getEditText().getText().toString();
                        String repeatNewPasswordInput = repeatNewPassword.getEditText().getText().toString();

                        oldPassword.setError(null);
                        newPassword.setError(null);
                        repeatNewPassword.setError(null);

                        //validates if the users gave the right current password
                        boolean validationCurrentPassword;
                        if(realCurrentPassword.equals(oldPasswordInput)) {
                            validationCurrentPassword = true;
                        }
                        else {
                            validationCurrentPassword = false;
                            oldPassword.setError(getResources().getString(R.string.profile_error_wrong_password));
                        }

                        //validates if the user passed a password longer then 5 characters and if newPassword and repeatNewPassword are the same.
                        boolean validationNewPassword;
                        if(newPasswordInput.length() > 5) {
                            if(newPasswordInput.equals(repeatNewPasswordInput)) {
                                validationNewPassword = true;
                            }
                            else {
                                validationNewPassword = false;
                                newPassword.setError(getResources().getString(R.string.profile_error_different_passwords));
                                repeatNewPassword.setError(getResources().getString(R.string.profile_error_different_passwords));
                            }
                        }
                        else {
                            validationNewPassword = false;
                            newPassword.setError(getResources().getString(R.string.profile_error_too_short_password));
                            repeatNewPassword.setError(getResources().getString(R.string.profile_error_too_short_password));
                        }

                        if(validationCurrentPassword && validationNewPassword) {
                            HashMap<String,Object> update = new HashMap<>();
                            update.put("password", newPasswordInput);
                            db.collection("users").document(userId).update(update);

                            Toast.makeText(getContext(), R.string.profile_password_is_changed, Toast.LENGTH_LONG).show();
                            navigationInterface.switchFragment(R.id.appbar_profile);

                        }

                    }
                });
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
}