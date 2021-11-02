package com.decor.design.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.decor.design.R;
import com.decor.design.databinding.ActivityLoginBinding;
import com.decor.design.homepage.HomepageActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        /// tampilkan ikon
        Glide.with(this)
                .load(R.drawable.logo)
                .into(binding.logo);

        /// login otomatis
        autoLogin();

        /// login
        binding.loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                formValidation();
            }
        });

        /// registrasi
        binding.registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

    }

    private void autoLogin() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            if (FirebaseAuth.getInstance().getCurrentUser().isEmailVerified()) {
                /// jika pernah login sebelumnya & email sudah di verifikasi, maka bisa login
                startActivity(new Intent(this, HomepageActivity.class));
                finish();
            } else {
                /// jika belum memverifikasi email, maka harus verifikasi terlebih dahulu
                showUnverifyDialog();
            }
        }
    }

    private void formValidation() {
        String email = binding.email.getText().toString().trim();
        String password = binding.password.getText().toString().trim();

        if(email.isEmpty()) {
            Toast.makeText(this, "Email tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        } else if (password.isEmpty()) {
            Toast.makeText(this, "Kata Sandi tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }

        /// cek apakah pengguna sudah terdaftar atau belum
        binding.progressBar.setVisibility(View.VISIBLE);
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        /// cek apakah email user udah di verifikasi atau belum
                        if(task.isSuccessful()) {
                            if (FirebaseAuth.getInstance().getCurrentUser().isEmailVerified()) {
                                binding.progressBar.setVisibility(View.GONE);

                                startActivity(new Intent(LoginActivity.this, HomepageActivity.class));
                                finish();
                            } else {
                                binding.progressBar.setVisibility(View.GONE);
                                showUnverifyDialog();
                            }
                        } else {
                            binding.progressBar.setVisibility(View.GONE);
                            showFailureDialog();
                        }
                    }
                });
    }

    /// jika gagal login, munculkan alert dialog gagal
    private void showFailureDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Failure login")
                .setMessage("There something wrong with your email/password or your connection still trouble, please try again later")
                .setIcon(R.drawable.ic_baseline_clear_24)
                .setPositiveButton("OK", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                })
                .show();
    }

    /// jika email belum di verifikasi, maka muncul dialog box
    private void showUnverifyDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Failure login")
                .setMessage("Your account not verified, please check your Gmail and verified account")
                .setIcon(R.drawable.ic_baseline_clear_24)
                .setPositiveButton("OK", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                })
                .setNegativeButton("Send verification", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    FirebaseAuth.getInstance().getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                Toast.makeText(LoginActivity.this, "Success to send verification, Please check your Gmail and verify your account", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                })
                .show();
    }

    /// HAPUSKAN ACTIVITY KETIKA SUDAH TIDAK DIGUNAKAN, AGAR MENGURANGI RISIKO MEMORY LEAKS
    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}