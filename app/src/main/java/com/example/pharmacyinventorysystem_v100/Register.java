package com.example.pharmacyinventorysystem_v100;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Register extends AppCompatActivity {

    // View variables
    private EditText editTextEmail, editTextPassword, editTextPassword2;
    private Button buttonRegister;
    private ProgressBar progressBar;

    // Firebase variables
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        setResolution();
        initializeVariables();

        buttonRegister.setOnClickListener(registerUser);
    }

    // Events ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    View.OnClickListener registerUser = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String emailAddress = editTextEmail.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();
            String password2 = editTextPassword2.getText().toString().trim();

            if (emailAddress.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches()) {
                editTextEmail.setError("Please enter a valid email");
                editTextEmail.requestFocus();
                return;
            }

            if (password.isEmpty()) {
                editTextPassword.setError("Required field");
                editTextPassword.requestFocus();
                return;
            }

            if (password2.isEmpty()) {
                editTextPassword2.setError("Required field");
                editTextPassword2.requestFocus();
                return;
            }

            if (!password.equals(password2)) {
                Toast.makeText(Register.this, "Password did not match", Toast.LENGTH_SHORT).show();
                editTextPassword.setText("");
                editTextPassword2.setText("");
                return;
            }

            progressBar.setVisibility(View.VISIBLE);
            mAuth.createUserWithEmailAndPassword(emailAddress,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(Register.this, "Account Successfully Created", Toast.LENGTH_LONG).show();
                        finish();
                    }
                    else {
                        Toast.makeText(Register.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                }
            });

        }
    };

    // Methods ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    private void setResolution() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        Double width = dm.widthPixels*0.8;
        Double height = dm.heightPixels*0.6;
        getWindow().setLayout(width.intValue(),height.intValue());
    }

    private void initializeVariables() {
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextPassword2 = findViewById(R.id.editTextPassword2);
        buttonRegister = findViewById(R.id.buttonRegister);
        progressBar = findViewById(R.id.progressBar);

        mAuth = FirebaseAuth.getInstance();
    }
}
