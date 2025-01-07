package com.example.mysecondclasshit.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.mysecondclasshit.Activity.MainActivity;
import com.example.mysecondclasshit.R;

public class FragmentTwo extends Fragment {

    private TextView emailReg;
    private TextView passwordReg;
    private TextView repasswordReg;
    private TextView phoneReg;
    private Button buttonReg;

    public FragmentTwo() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_two, container, false);
        Button button1= view.findViewById(R.id.buttonReg);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity mainActivity = (MainActivity) getActivity();
                mainActivity.reg();
                mainActivity.addDATA();

            }
        });{
        return view;

        }

                // קישור המשתנים
       // emailReg = view.findViewById(R.id.emailFragment2);
        //passwordReg = view.findViewById(R.id.passwordFragment2);
       // repasswordReg = view.findViewById(R.id.repasswordFragment2);
       // phoneReg = view.findViewById(R.id.phoneFragment2);
        // buttonRegi = view.findViewById(R.id.buttonReg);
    }
    private boolean validatePasswords() {
        String password = passwordReg.getText().toString();
        String rePassword = repasswordReg.getText().toString();

        if (password.length() < 8) {
            passwordReg.setError("Password must be at least 8 characters");
            return false;
        }

        if (!password.matches(".*[A-Za-z].*") || !password.matches(".*[0-9].*")) {
            passwordReg.setError("Password must contain both letters and numbers");
            return false;
        }

        if (!password.equals(rePassword)) {
            passwordReg.setError("Passwords don't match");
            return false;
        }

        return true;
    }

    private boolean validateAllFields() {
        boolean isValid = true;

        String email = emailReg.getText().toString().trim();
        if (email.isEmpty()) {
            emailReg.setError("Email is required");
            isValid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailReg.setError("Enter a valid email address");
            isValid = false;
        }

        String phone = phoneReg.getText().toString().trim();
        if (phone.isEmpty()) {
            phoneReg.setError("Phone number is required");
            isValid = false;
        }

        if (!validatePasswords()) {
            isValid = false;
        }

        return isValid;
    }
}