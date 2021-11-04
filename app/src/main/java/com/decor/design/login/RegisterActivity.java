package com.decor.design.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

import com.decor.design.R;
import com.decor.design.databinding.ActivityRegisterBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        /// set tanggal lahir
        binding.view2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDateOfBirth();
            }
        });

        /// back
        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        /// register
        binding.customerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                formValidation("customer");
            }
        });
        binding.designerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                formValidation("designer");
            }
        });

    }

    private void setDateOfBirth() {
        // choose DOB,
        MaterialDatePicker datePicker = MaterialDatePicker.Builder.datePicker().setTitleText("Date of Birth").build();
        datePicker.show(getSupportFragmentManager(), datePicker.toString());
        datePicker.addOnPositiveButtonClickListener(selection -> {

            /// konversi tanggal yang sudah dipilih: contoh 19-02-2021
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String getDOB = sdf.format(new Date(Long.parseLong(selection.toString())));
            binding.dob.setText(getDOB);
        });
    }

    private void formValidation(String role) {
        String email = binding.email.getText().toString().trim();
        String password = binding.password.getText().toString().trim();
        String name = binding.name.getText().toString().trim();
        String dob = binding.dob.getText().toString().trim();

        // Choose Gender
        int selectId = binding.radioGroup2.getCheckedRadioButtonId();
        RadioButton radioButton = findViewById(selectId);

        if (name.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "Full Name must be filled", Toast.LENGTH_SHORT).show();
            return;
        } else if (dob.equals("Date of Birth")) {
            Toast.makeText(RegisterActivity.this, "Date of Birth must be filled", Toast.LENGTH_SHORT).show();
            return;
        } else if (email.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "Email must be filled", Toast.LENGTH_SHORT).show();
            return;
        } else if (password.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "Password must be filled", Toast.LENGTH_SHORT).show();
            return;
        } else if (radioButton == null) {
            Toast.makeText(RegisterActivity.this, "Gender must be filled", Toast.LENGTH_SHORT).show();
            return;
        }

        // simpan biodata kedalam database
        binding.progressBar.setVisibility(View.VISIBLE);
        /// create account
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        //// send email verification
                        FirebaseAuth.getInstance()
                                .getCurrentUser()
                                .sendEmailVerification()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            /// if operation complete then save user bio to database

                                            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                                            Map<String, Object> register = new HashMap<>();
                                            register.put("name", name);
                                            register.put("nameTemp", name.toLowerCase());
                                            register.put("dob", dob);
                                            register.put("email", email);
                                            register.put("password", password);
                                            register.put("gender", radioButton.getText().toString());
                                            register.put("uid", uid);
                                            register.put("role", role);
                                            register.put("phone", "not set");
                                            register.put("dp", "");
                                            register.put("username", "not set");
                                            /// for designer only
                                            register.put("background", "not set");
                                            register.put("work", "not set");
                                            register.put("skill", "not set");
                                            register.put("softSkill", "not set");
                                            register.put("organization", "not set");
                                            register.put("hobby", "not set");
                                            register.put("education", "not set");

                                            FirebaseFirestore
                                                    .getInstance()
                                                    .collection("users")
                                                    .document(uid)
                                                    .set(register)
                                                    .addOnCompleteListener(task2 -> {
                                                        if (task2.isSuccessful()) {
                                                            binding.progressBar.setVisibility(View.GONE);
                                                            showSuccessDialog();
                                                        } else {
                                                            binding.progressBar.setVisibility(View.GONE);
                                                            showFailureDialog();
                                                        }
                                                    });
                                        } else {
                                            binding.progressBar.setVisibility(View.GONE);
                                            showFailureDialog();
                                        }
                                    }
                                });
                    } else {
                        binding.progressBar.setVisibility(View.GONE);
                        showFailureDialog();
                    }
                });

    }

    /// show fail register
    private void showFailureDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Failed to Register")
                .setMessage("Please fill the column with correct information, or please check your internet connection")
                .setIcon(R.drawable.ic_baseline_clear_24)
                .setPositiveButton("OK", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                })
                .show();
    }

    /// show success register
    private void showSuccessDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Success to Register")
                .setMessage("Please login now")
                .setIcon(R.drawable.ic_baseline_check_circle_outline_24)
                .setPositiveButton("OK", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    onBackPressed();
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