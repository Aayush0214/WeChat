package com.project.wechat.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.project.wechat.databinding.ActivitySignInBinding;
import com.project.wechat.utilities.Constants;
import com.project.wechat.utilities.PreferenceManager;

public class SignInActivity extends AppCompatActivity {

    private ActivitySignInBinding binding;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferenceManager = new PreferenceManager(getApplicationContext());

        if (preferenceManager.getBoolean(Constants.KEY_IS_SIGNED_IN)){
           startActivity(new Intent(getApplicationContext(), MainActivity.class));
           finish();
        }
        setListeners();
    }

    private void setListeners() {
        binding.createAccountTextBtn.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), SignUpActivity.class)));

        binding.signInButton.setOnClickListener(v -> {
            if (isValidSignInDetails()){
                signIn();
            }
        });
    }

    private void signIn(){
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_USERS)
                .whereEqualTo(Constants.KEY_EMAIL, binding.signInUserEmail.getText().toString())
                .whereEqualTo(Constants.KEY_PASSWORD, binding.signInUserPassword.getText().toString())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().getDocuments().isEmpty()) {
                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                        preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
                        preferenceManager.putString(Constants.KEY_USER_ID, documentSnapshot.getId());
                        preferenceManager.putString(Constants.KEY_NAME, documentSnapshot.getString(Constants.KEY_NAME));
                        preferenceManager.putString(Constants.KEY_IMAGE, documentSnapshot.getString(Constants.KEY_IMAGE));
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else {
                        loading(false);
                        showToast("Unable to sign-in");
                    }
                });
    }

    private void loading(Boolean isLoading){
        if (isLoading){
            binding.signInButton.setVisibility(View.INVISIBLE);
            binding.signInProgressBar.setVisibility(View.VISIBLE);
        } else {
            binding.signInButton.setVisibility(View.VISIBLE);
            binding.signInProgressBar.setVisibility(View.INVISIBLE);
        }
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private Boolean isValidSignInDetails() {
        if (binding.signInUserEmail.getText().toString().trim().isEmpty()){
            showToast("Enter email");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.signInUserEmail.getText().toString()).matches()) {
            showToast("Enter valid email");
            return false;
        } else if (binding.signInUserPassword.getText().toString().trim().isEmpty()){
            showToast("Enter password");
            return false;
        } else {
            return true;
        }
    }
}