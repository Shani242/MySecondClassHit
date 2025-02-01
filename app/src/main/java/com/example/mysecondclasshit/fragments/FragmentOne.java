package com.example.mysecondclasshit.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.mysecondclasshit.Activity.MainActivity;
import com.example.mysecondclasshit.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class FragmentOne extends Fragment {
    private FirebaseAuth mAuth;
    private EditText emailInput;
    private EditText passwordInput;
    private Button loginButton;
    private Button registerButton;

    public FragmentOne() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
        // בדיקה אם המשתמש כבר מחובר
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            navigateToShoppingList();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_one, container, false);

        initializeViews(view);
        setupClickListeners();

        return view;
    }

    private void initializeViews(View view) {
        emailInput = view.findViewById(R.id.email);
        passwordInput = view.findViewById(R.id.password);
        loginButton = view.findViewById(R.id.button1ToFrag3);
        registerButton = view.findViewById(R.id.button1ToFrag2);
    }

    private void setupClickListeners() {
        // כפתור למעבר למסך הרשמה
        registerButton.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_fragmentOne_to_fragmentTwo)
        );

        // כפתור להתחברות
        loginButton.setOnClickListener(v -> {
            if (validateInput()) {
                String email = emailInput.getText().toString().trim();
                String password = passwordInput.getText().toString().trim();

                // הצגת loading
                loginButton.setEnabled(false);
                loginButton.setText("מתחבר...");

                MainActivity mainActivity = (MainActivity) getActivity();
                if (mainActivity != null) {
                    mainActivity.login();
                }
            }
        });
    }

    private boolean validateInput() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        // בדיקת אימייל
        if (email.isEmpty()) {
            emailInput.setError("נא להזין אימייל");
            return false;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInput.setError("נא להזין כתובת אימייל תקינה");
            return false;
        }

        // בדיקת סיסמא
        if (password.isEmpty()) {
            passwordInput.setError("נא להזין סיסמא");
            return false;
        }
        if (password.length() < 6) {
            passwordInput.setError("הסיסמא חייבת להכיל לפחות 6 תווים");
            return false;
        }

        return true;
    }

    public void navigateToShoppingList() {
        if (isAdded() && getView() != null) {
            Navigation.findNavController(getView()).navigate(R.id.action_fragmentOne_to_fragmentTh);
        }
    }

    public void resetLoginButton() {
        if (loginButton != null) {
            loginButton.setEnabled(true);
            loginButton.setText("התחבר");
        }
    }

    public void showError(String message) {
        if (isAdded()) {
            Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
            resetLoginButton();
        }
    }
}