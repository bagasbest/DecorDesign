package com.decor.design.homepage.search;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.decor.design.R;
import com.decor.design.databinding.ActivitySearchDetailBinding;
import com.decor.design.homepage.HomepageActivity;
import com.decor.design.homepage.search.show_all_post_designer.ShowAllPostActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SearchDetailActivity extends AppCompatActivity {

    public static final String EXTRA_SEARCH = "uid";
    public static final String EXTRA_OPTION = "option";
    private ActivitySearchDetailBinding binding;
    private String role, designerId;
    private String designerName;
    private String designerDp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        designerId = getIntent().getStringExtra(EXTRA_SEARCH);

        /// check role, apakah saya merupakan customer atau bukan, untuk keperluan chat designer
        checkRole();

        /// isi field field profil dari designer
        setDetailDesignerField();

        /// kembali ke halaman sebelumnya
        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        /// show all post designer
        binding.showPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SearchDetailActivity.this, ShowAllPostActivity.class);
                intent.putExtra(ShowAllPostActivity.UID, designerId);
                startActivity(intent);
            }
        });

        /// hanya customer yang dapat chat designer
        binding.chatDesigner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.chatDesigner.setEnabled(false);
                chatDesigner();
            }
        });
    }


    private void setDetailDesignerField() {
        FirebaseFirestore
                .getInstance()
                .collection("users")
                .document(getIntent().getStringExtra(EXTRA_SEARCH))
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        designerName = "" + documentSnapshot.get("name");
                        designerDp = "" + documentSnapshot.get("dp");

                        if(!("" + documentSnapshot.get("dp")).equals("")) {
                            Glide.with(SearchDetailActivity.this)
                                    .load("" + documentSnapshot.get("dp"))
                                    .into(binding.dp);
                        }

                        binding.background.setText("" + documentSnapshot.get("background"));
                        binding.fullName.setText(designerName);
                        binding.username.setText("" + documentSnapshot.get("username"));
                        binding.gender.setText("" + documentSnapshot.get("gender"));
                        binding.dob.setText("" + documentSnapshot.get("dob"));
                        binding.phone.setText("" + documentSnapshot.get("phone"));
                        binding.email.setText("" + documentSnapshot.get("email"));
                        binding.work.setText("" + documentSnapshot.get("work"));
                        binding.education.setText("" + documentSnapshot.get("education"));
                        binding.skill.setText("" + documentSnapshot.get("skill"));
                        binding.softSkill.setText("" + documentSnapshot.get("softSkill"));
                        binding.organization.setText("" + documentSnapshot.get("organization"));
                        binding.hobby.setText("" + documentSnapshot.get("hobby"));
                    }
                });
    }

    private void checkRole() {
        String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore
                .getInstance()
                .collection("users")
                .document(myUid)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        /// check role, apakah admin, customer, atau designer, untuk keperluan chat
                        role = "" + documentSnapshot.get("role");
                        if(role.equals("customer") && getIntent().getStringExtra(EXTRA_OPTION).equals("search")){
                            binding.chatDesigner.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }

    private void chatDesigner() {
        String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        ProgressDialog mProgressDialog = new ProgressDialog(this);

        mProgressDialog.setMessage("Please wait until process finish...");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();

        FirebaseFirestore
                .getInstance()
                .collection("users")
                .document(myUid)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        Map<String, Object> consultation = new HashMap<>();
                        consultation.put("chatId", myUid+designerId);
                        consultation.put("designerId", designerId);
                        consultation.put("customerId", myUid);
                        consultation.put("designerName", designerName);
                        consultation.put("customerName", "" + documentSnapshot.get("name"));
                        if(designerDp.equals("")) {
                            consultation.put("designerDp", "");
                        } else {
                            consultation.put("designerDp", designerDp);
                        }
                        consultation.put("dateTime", "");
                        consultation.put("customerDp", "" + documentSnapshot.get("dp"));
                        consultation.put("lastMessage", "");

                        FirebaseFirestore
                                .getInstance()
                                .collection("chat")
                                .document(myUid+designerId)
                                .set(consultation)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        mProgressDialog.dismiss();
                                        showSuccessDialog();
                                    } else {
                                        mProgressDialog.dismiss();
                                        Log.e("Error Transaction", task.toString());
                                        Toast.makeText(SearchDetailActivity.this, "Failure to create new chat with designer", Toast.LENGTH_SHORT).show();
                                        binding.chatDesigner.setEnabled(true);
                                    }
                                });

                    }
                });
    }

    private void showSuccessDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Success Create Chat with Designer")
                .setMessage("You can start chat with designer in Chat Navigation\n\nThank you")
                .setIcon(R.drawable.ic_baseline_check_circle_outline_24)
                .setCancelable(false)
                .setPositiveButton("OK", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    binding.chatDesigner.setEnabled(true);
                    Intent intent = new Intent(SearchDetailActivity.this, HomepageActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                })
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}