package com.example.mysecondclasshit.Activity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.Navigation;
import com.example.mysecondclasshit.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import model.Client;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        mAuth = FirebaseAuth.getInstance();
    }

    public void reg() {
        String email = ((EditText)findViewById(R.id.emailFragment2)).getText().toString();
        String password = ((EditText)findViewById(R.id.passwordFragment2)).getText().toString();

        mAuth.fetchSignInMethodsForEmail(email)
                .addOnCompleteListener(checkTask -> {
                    if (checkTask.isSuccessful()) {
                        if (checkTask.getResult().getSignInMethods().isEmpty()) {
                            mAuth.createUserWithEmailAndPassword(email, password)
                                    .addOnCompleteListener(this, task -> {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(MainActivity.this, "ההרשמה בוצעה בהצלחה", Toast.LENGTH_LONG).show();
                                            addDATA();
                                            Navigation.findNavController(findViewById(R.id.fragmentContainerView))
                                                    .navigate(R.id.action_fragmentTwo_to_fragmentOne);
                                        } else {
                                            Toast.makeText(MainActivity.this, "שגיאה בהרשמה", Toast.LENGTH_LONG).show();
                                        }
                                    });
                        } else {
                            Toast.makeText(MainActivity.this, "כתובת המייל כבר קיימת במערכת", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "שגיאה בבדיקת המייל", Toast.LENGTH_LONG).show();
                    }
                });
    }

    public void login() {
        String email = ((EditText)findViewById(R.id.email)).getText().toString();
        String password = ((EditText)findViewById(R.id.password)).getText().toString();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(MainActivity.this, "התחברת בהצלחה", Toast.LENGTH_LONG).show();
                        Navigation.findNavController(findViewById(R.id.fragmentContainerView))
                                .navigate(R.id.action_fragmentOne_to_fragmentTh);
                    } else {
                        String errorMessage = "שגיאה בהתחברות";
                        if (task.getException() != null) {
                            if (task.getException().getMessage().contains("password is invalid")) {
                                errorMessage = "סיסמא שגויה";
                            } else if (task.getException().getMessage().contains("no user record")) {
                                errorMessage = "משתמש לא קיים";
                            }
                        }
                        Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                    }
                });
    }

    public void addDATA() {
        String phone = ((EditText)findViewById(R.id.phoneFragment2)).getText().toString();
        String email = ((EditText)findViewById(R.id.emailFragment2)).getText().toString();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users").child(phone);

        Client client = new Client(email, phone);
        myRef.setValue(client);
    }

    public void getaStudent(String phone) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users").child(phone);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Client value = dataSnapshot.getValue(Client.class);
                if (value != null) {
                    Toast.makeText(MainActivity.this, value.getEmail(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(MainActivity.this, "שגיאה בקריאת הנתונים", Toast.LENGTH_LONG).show();
            }
        });
    }
}