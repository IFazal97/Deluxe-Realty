package com.example.deluxerealty.screens;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.deluxerealty.R;
import com.example.deluxerealty.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText userName,userEmail,userPassword,confirmPassword, user_phone;
    private DatabaseReference mRef;
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private TextView textView;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        TextView haveAccount = findViewById(R.id.already_have_account);
        userName = findViewById(R.id.user_name);
        userEmail = findViewById(R.id.user_email);
        userPassword = findViewById(R.id.user_password);
        user_phone = findViewById(R.id.user_phone);
        confirmPassword = findViewById(R.id.user_confirm_password);


        //radioGroup = findViewById(R.id.radioGroup);

        
        AppCompatButton signUpButton = findViewById(R.id.signup_button);

        mAuth = FirebaseAuth.getInstance();
        mRef = FirebaseDatabase.getInstance().getReference();



        haveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                finish();
            }
        });
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (userName.getText().toString().trim().isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "Enter Name", Toast.LENGTH_SHORT).show();
                } else if (userEmail.getText().toString().trim().isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "Enter Valid Email", Toast.LENGTH_SHORT).show();
                }else if(userPassword.getText().toString().trim().isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "Enter Password", Toast.LENGTH_SHORT).show();
                }else if(!userPassword.getText().toString().trim().equals(confirmPassword.getText().toString().trim())){
                    Toast.makeText(SignUpActivity.this, "Enter Valid Password", Toast.LENGTH_SHORT).show();
                }else if(user_phone.getText().toString().trim().isEmpty()){
                    Toast.makeText(SignUpActivity.this, "Enter Phone", Toast.LENGTH_SHORT).show();
                } else{
                    if(emailChecker(userEmail.getText().toString().trim())){

                        createUser(userEmail.getText().toString().trim(),
                                userPassword.getText().toString().trim(),
                                userName.getText().toString().trim(),
                                user_phone.getText().toString().trim());

                    }else{
                        Toast.makeText(SignUpActivity.this, "Enter Valid Email", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    boolean emailChecker(String email){ return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    void createUser(String email, String password, String name, String phone){
        progressDialog = new ProgressDialog(SignUpActivity.this);
        progressDialog.setMessage("loading");
        progressDialog.show();

        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    User user = new User(FirebaseAuth.getInstance().getCurrentUser().getUid(),name,email, phone, "https://firebasestorage.googleapis.com/v0/b/deluxe-realty.appspot.com/o/images%2Fbf62ecb9-3447-4276-8d38-3b15e680eea2?alt=media&token=9b4378ed-9d83-4c83-8bbf-895b9de8d47d");

                    FirebaseDatabase.getInstance().getReference("users")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        progressDialog.hide();

                                        Toast.makeText(SignUpActivity.this, "Registered Successfully", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(SignUpActivity.this, HomeActivity.class));
                                        finish();
                                    }else {
                                        progressDialog.hide();

                                        Toast.makeText(SignUpActivity.this, "Registration Failed", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                }else{
                    progressDialog.hide();
                    Toast.makeText(SignUpActivity.this, "Server ERROR", Toast.LENGTH_SHORT).show();


                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SignUpActivity.this, "Email already exits", Toast.LENGTH_SHORT).show();
            }
        });

    }


}