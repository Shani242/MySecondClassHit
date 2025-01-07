package com.example.mysecondclasshit.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.mysecondclasshit.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import model.Student;

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

    public void reg(){

        String email = ((EditText)findViewById(R.id.emailFragment2)).getText().toString();
        String password= ((EditText)findViewById(R.id.passwordFragment2)).getText().toString();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(MainActivity.this, "reg ok",Toast.LENGTH_LONG).show();


                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(MainActivity .this, "reg fail",Toast.LENGTH_LONG).show();


                        }
                    }
                });
    }
    public void login(){

        String email = ((EditText)findViewById(R.id.email)).getText().toString();
        String password= ((EditText)findViewById(R.id.password)).getText().toString();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener <AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "login ok",Toast.LENGTH_LONG).show();
                        }
                        else {
                            Toast.makeText(MainActivity .this, "login fail",Toast.LENGTH_LONG).show();

                        }

                    }
                });

    }

    public void addDATA ()
    {
        String phone= ((EditText) findViewById(R.id.phoneFragment2)).getText().toString();
        String email= ((EditText) findViewById(R.id.emailFragment2)).getText().toString();
        FirebaseDatabase database= FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users").child(phone);

        Student s= new Student(email,phone);
        myRef.setValue(s);
    }

    public void getaStudent(String phone){
        // Read from the database
        FirebaseDatabase database= FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users").child(phone);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Student value = dataSnapshot.getValue(Student.class);
                Toast.makeText(MainActivity.this, value.getEmail(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });
    }

}
