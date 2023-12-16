package com.example.outdoorgear;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private DatabaseReference databaseReference;

    private EditText signupEmail, signupPassword;
    private Button signupButton;
    private TextView loginRedirectText;

    public class User {
        private String userId;
        private String email;
        private String password;  // Sesuaikan dengan informasi tambahan yang ingin disimpan

        // Konstruktor kosong diperlukan untuk Firebase
        public User() {
        }

        // Konstruktor dengan parameter
        public User(String userId, String email, String password) {
            this.userId = userId;
            this.email = email;
            this.password = password;
        }

        // Getter dan setter sesuaikan dengan kebutuhan
        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        signupEmail = findViewById(R.id.signup_email);
        signupPassword = findViewById(R.id.signup_password);
        signupButton = findViewById(R.id.signup_button);
        loginRedirectText = findViewById(R.id.loginRedirectText);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String user = signupEmail.getText().toString().trim();
                String pass = signupPassword.getText().toString().trim();

                // Inisialisasi variabel user
                final String userEmail;

                if (user.isEmpty()) {
                    signupEmail.setError("Email cannot be empty");
                    return; // Stop execution if the email is empty
                } else {
                    userEmail = user;  // Set nilai user jika tidak kosong
                }

                if (pass.isEmpty()) {
                    signupPassword.setError("Password cannot be empty");
                    return; // Stop execution if the password is empty
                }

                // Continue with Firebase authentication
                auth.createUserWithEmailAndPassword(userEmail, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Pendaftaran berhasil
                            Toast.makeText(SignUpActivity.this, "SignUp Successful", Toast.LENGTH_SHORT).show();

                            // Simpan informasi pengguna ke dalam Realtime Database
                            FirebaseUser currentUser = auth.getCurrentUser();
                            String userId = currentUser.getUid();

                            User newUser = new User(userId, userEmail, pass); // Sesuaikan dengan struktur User Anda
                            databaseReference.child(userId).setValue(newUser);

                            // Lanjutkan ke LoginActivity atau lakukan tindakan lainnya
                            startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                        } else {
                            // Pendaftaran gagal
                            Toast.makeText(SignUpActivity.this, "SignUp Failed" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });



        loginRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
            }
        });
    }
}
