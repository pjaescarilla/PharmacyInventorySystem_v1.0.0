package com.example.pharmacyinventorysystem_v100;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {

    // View variables
    private EditText editTextPassword, editTextEmail;
    private Button buttonSignIn;
    private TextView textViewPromptNewUser;
    private ProgressBar progressBar;
    private Spinner spinnerBranch;

    // Firebase variables
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initializeVariables();
        buttonSignIn.setOnClickListener(signIn);
        textViewPromptNewUser.setOnClickListener(openRegisterActivity);

        mAuth.signOut();
        if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(Login.this,displayProducts.class));
        }
    }

    // Events ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    View.OnClickListener signIn = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String emailAddress = editTextEmail.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();

            if (spinnerBranch.getSelectedItemPosition() == 0) {
                Toast.makeText(Login.this, "Please select a branch", Toast.LENGTH_SHORT).show();
                spinnerBranch.requestFocus();
                return;
            }

            if (emailAddress.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches()) {
                editTextEmail.setError("Please enter valid email address");
                editTextEmail.requestFocus();
                return;
            }

            if (password.isEmpty()) {
                editTextPassword.setError("Please enter password");
                editTextPassword.requestFocus();
                return;
            }

            progressBar.setVisibility(View.VISIBLE);
            mAuth.signInWithEmailAndPassword(emailAddress, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        ((GlobalVariables)getApplication()).setThisBranch(spinnerBranch.getSelectedItem().toString());
                        startActivity(new Intent(Login.this,displayProducts.class));
                    }
                    else {
                        Toast.makeText(Login.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                }
            });
        }
    };

    View.OnClickListener openRegisterActivity = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(Login.this,Register.class));
        }
    };

    // Methods ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    private void initializeVariables() {
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonSignIn = findViewById(R.id.buttonSignIn);
        textViewPromptNewUser = findViewById(R.id.textViewCreateAccount);
        progressBar = findViewById(R.id.progressBar);
        spinnerBranch = findViewById(R.id.spinnerBranch);

        mAuth = FirebaseAuth.getInstance();
    }
}
