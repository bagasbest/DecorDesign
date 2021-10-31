package com.decor.design.homepage.profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.decor.design.R;
import com.decor.design.databinding.ActivityProfileEditBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ProfileEditActivity extends AppCompatActivity {

    public static final String ROLE = "ROLE";
    public static final String EXTRA_PROFILE = "profile";
    private ActivityProfileEditBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ProfileModel model = getIntent().getParcelableExtra(EXTRA_PROFILE);

        if(getIntent().getStringExtra(ROLE).equals("designer")) {

            binding.background.setVisibility(View.VISIBLE);
            binding.designerForm.setVisibility(View.VISIBLE);

            binding.background.setText(model.getBackground());
            binding.education.setText(model.getEducation());
            binding.hobby.setText(model.getHobby());
            binding.name.setText(model.getName());
            binding.organization.setText(model.getOrganization());
            binding.phone.setText(model.getPhone());
            binding.skill.setText(model.getSkill());
            binding.softSkill.setText(model.getSoftSkill());
            binding.username.setText(model.getUsername());
            binding.work.setText(model.getWork());
        } else {
            binding.name.setText(model.getName());
            binding.username.setText(model.getUsername());
            binding.phone.setText(model.getPhone());
        }



        /// update profile
        binding.saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                formValidation();
            }
        });


    }

    private void formValidation() {
        if(getIntent().getStringExtra(ROLE).equals("designer")) {
            String background = binding.background.getText().toString().trim();
            String education = binding.education.getText().toString().trim();
            String hobby = binding.hobby.getText().toString().trim();
            String name  = binding.name.getText().toString().trim();
            String organization = binding.organization.getText().toString().trim();
            String phone = binding.phone.getText().toString().trim();
            String skill = binding.skill.getText().toString().trim();
            String softSkill = binding.softSkill.getText().toString().trim();
            String username = binding.username.getText().toString().trim();
            String work = binding.work.getText().toString().trim();

            if(background.isEmpty()) {
                Toast.makeText(this, "Background must be filled", Toast.LENGTH_SHORT).show();
                return;
            } else if (education.isEmpty()) {
                Toast.makeText(this, "Education must be filled", Toast.LENGTH_SHORT).show();
                return;
            }else if (hobby.isEmpty()) {
                Toast.makeText(this, "Hobby must be filled", Toast.LENGTH_SHORT).show();
                return;
            }else if (name.isEmpty()) {
                Toast.makeText(this, "Full Name must be filled", Toast.LENGTH_SHORT).show();
                return;
            }else if (organization.isEmpty()) {
                Toast.makeText(this, "Organization must be filled", Toast.LENGTH_SHORT).show();
                return;
            }else if (phone.isEmpty()) {
                Toast.makeText(this, "Phone Number must be filled", Toast.LENGTH_SHORT).show();
                return;
            }else if (skill.isEmpty()) {
                Toast.makeText(this, "Skill must be filled", Toast.LENGTH_SHORT).show();
                return;
            }else if (softSkill.isEmpty()) {
                Toast.makeText(this, "Soft Skill must be filled", Toast.LENGTH_SHORT).show();
                return;
            }else if (username.isEmpty()) {
                Toast.makeText(this, "Username must be filled", Toast.LENGTH_SHORT).show();
                return;
            }else if (work.isEmpty()) {
                Toast.makeText(this, "Work must be filled", Toast.LENGTH_SHORT).show();
                return;
            }


            /// save to database
            binding.progressBar.setVisibility(View.VISIBLE);
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();


            Map<String, Object> user = new HashMap<>();
            user.put("name", name);
            user.put("username", username);
            user.put("phone", phone);
            user.put("background", background);
            user.put("education", phone);
            user.put("hobby", phone);
            user.put("organization", phone);
            user.put("work", phone);
            user.put("skill", phone);
            user.put("softSkill", phone);

            FirebaseFirestore
                    .getInstance()
                    .collection("users")
                    .document(userId)
                    .update(user)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                binding.progressBar.setVisibility(View.GONE);
                                showSuccessDialog();
                            } else {
                                binding.progressBar.setVisibility(View.GONE);
                                showFailureDialog();
                            }
                        }
                    });
        } else {
            String username = binding.username.getText().toString().trim();
            String name  = binding.name.getText().toString().trim();
            String phone = binding.phone.getText().toString().trim();

            if(username.isEmpty()) {
                Toast.makeText(this, "Background must be filled", Toast.LENGTH_SHORT).show();
                return;
            } else if (name.isEmpty()) {
                Toast.makeText(this, "Education must be filled", Toast.LENGTH_SHORT).show();
                return;
            }else if (phone.isEmpty()) {
                Toast.makeText(this, "Hobby must be filled", Toast.LENGTH_SHORT).show();
                return;
            }


            /// save to database
            binding.progressBar.setVisibility(View.VISIBLE);
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();


            Map<String, Object> user = new HashMap<>();
            user.put("name", name);
            user.put("username", username);
            user.put("phone", phone);

            FirebaseFirestore
                    .getInstance()
                    .collection("users")
                    .document(userId)
                    .update(user)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                binding.progressBar.setVisibility(View.GONE);
                                showSuccessDialog();
                            } else {
                                binding.progressBar.setVisibility(View.GONE);
                                showFailureDialog();
                            }
                        }
                    });
        }

    }


    /// show fail update
    private void showFailureDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Failed to Update Profile")
                .setMessage("Please fill the column with correct information, or please check your internet connection")
                .setIcon(R.drawable.ic_baseline_clear_24)
                .setPositiveButton("OK", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                })
                .show();
    }

    /// show success update
    private void showSuccessDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Success to Update Profile")
                .setMessage("Operation Success")
                .setIcon(R.drawable.ic_baseline_check_circle_outline_24)
                .setPositiveButton("OK", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    onBackPressed();
                })
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}