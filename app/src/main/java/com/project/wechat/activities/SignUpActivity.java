package com.project.wechat.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.project.wechat.databinding.ActivitySignUpBinding;
import com.project.wechat.utilities.Constants;
import com.project.wechat.utilities.PreferenceManager;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {

    private ActivitySignUpBinding binding;
    private String encodedImage;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        setListeners();
    }

    // action listener function
    private void setListeners() {
        binding.signInOptionTextBtn.setOnClickListener(v -> onBackPressed() );
        binding.signUpCreateAccountBtn.setOnClickListener(v -> {
            if (isValidSignUpDetails()){
                signUp();
            }
        });

        binding.userProfile.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.addFlags(Intent.FLAG_GRANT_PREFIX_URI_PERMISSION);
            pickImage.launch(intent);
        });
    }

    // showing function
    private void showToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    // signUp function
    private void signUp(){
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        HashMap<String , Object> user = new HashMap<>();
        user.put(Constants.KEY_NAME, binding.signUpUserName.getText().toString());
        user.put(Constants.KEY_EMAIL, binding.signUpUserEmail.getText().toString());
        user.put(Constants.KEY_PASSWORD, binding.signUpUserPassword.getText().toString());
        user.put(Constants.KEY_IMAGE, encodedImage);
        database.collection(Constants.KEY_COLLECTION_USERS)
                .add(user)
                .addOnSuccessListener(documentReference -> {
                    loading(true);
                    preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
                    preferenceManager.putString(Constants.KEY_USER_ID, documentReference.getId());
                    preferenceManager.putString(Constants.KEY_NAME, binding.signUpUserName.getText().toString());
                    preferenceManager.putString(Constants.KEY_IMAGE,encodedImage);
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                })
                .addOnFailureListener(exception ->{
                    loading(false);
                    showToast(exception.getMessage());
                });
    }

    private String encodeImage(Bitmap bitmap) {
        int previewWidth = 150;
        int previewHeight = bitmap.getHeight() * previewWidth / bitmap.getWidth();
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK){
                    if (result.getData() != null){
                        Uri imageUri = result.getData().getData();
                        try {
                            InputStream inputStream = getContentResolver().openInputStream(imageUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            binding.userProfile.setImageBitmap(bitmap);
                            encodedImage = encodeImage(bitmap);
                        }catch (FileNotFoundException e){
                            e.printStackTrace();
                        }
                    }
                }
            }
    );

    private boolean isValidSignUpDetails(){
        if (encodedImage == null){
            showToast("Select profile picture!");
            return false;
        } else if (binding.signUpUserName.getText().toString().trim().isEmpty()) {
            showToast("Enter name!");
            return false;
        } else if (binding.signUpUserEmail.getText().toString().trim().isEmpty()) {
            showToast("Enter email!");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.signUpUserEmail.getText().toString()).matches()) {
            showToast("Enter valid email!");
            return false;
        }else if (binding.signUpUserPassword.getText().toString().trim().isEmpty() || binding.signUpUserPassword.getText().toString().trim().length() < 6) {
            showToast("Enter password of least 6 character!");
            return false;
        } else if (binding.signUpUserCnfpassword.getText().toString().trim().isEmpty()) {
            showToast("Confirm password!");
            return false;
        } else if (!binding.signUpUserPassword.getText().toString().trim().equals(binding.signUpUserCnfpassword.getText().toString())) {
            showToast("Password & confirm password not matched ");
            return false;
        } else {
            return true;
        }
    }

    private void loading(Boolean isLoading){
        if(isLoading){
            binding.signUpCreateAccountBtn.setVisibility(View.INVISIBLE);
            binding.signUpProgressBar.setVisibility(View.VISIBLE);
        } else {
            binding.signUpProgressBar.setVisibility(View.INVISIBLE);
            binding.signUpCreateAccountBtn.setVisibility(View.VISIBLE);
        }
    }
}