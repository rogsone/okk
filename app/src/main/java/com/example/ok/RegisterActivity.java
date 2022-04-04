package com.example.ok;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
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

import com.example.ok.databinding.ActivityRegisterBinding;
import com.example.ok.utilities.Constants;
import com.example.ok.utilities.PreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {


    FirebaseFirestore db;
    private String encodedImage;
    private ActivityRegisterBinding binding;
    private PreferenceManager preferenceManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        setListeners();

        //Initialize Firebase Auth




        //Button ButtonSignUp = findViewById(R.id.buttonSignUp);
        //ButtonSignUp.setOnClickListener(new View.OnClickListener(){
            //@Override
            //public void onClick(View v) {
                //registerUser();
            //}

        //});

    }

    private void setListeners(){
        binding.logiinback.setOnClickListener(v -> onBackPressed());
        binding.buttonSignUp.setOnClickListener(v -> {
            if(isValidSignUpDetails()){
                signUp();
            }
        });
        binding.roundedImageView.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickImage.launch(intent);

        });
    }

    private void showToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void signUp(){
        loading(true);
        db = FirebaseFirestore.getInstance();
        Map<String, Object> user = new HashMap<>();
        user.put(Constants.KEY_EMAIL, binding.inputEmail.getText().toString());
        user.put(Constants.KEY_FULLNAME, binding.usernamee2.getText().toString());
        user.put(Constants.KEY_PSN, binding.PSN2.getText().toString());
        user.put(Constants.KEY_PASSWORD, binding.inputPassword3.getText().toString());
        user.put(Constants.KEY_IMAGE, encodedImage);
        db.collection(Constants.KEY_COLLECTION_USERS)
                .add(user)
                .addOnSuccessListener(documentReference -> {
                    loading(false);
                    preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
                    preferenceManager.putString(Constants.KEY_USER_ID, documentReference.getId());
                    preferenceManager.putString(Constants.KEY_FULLNAME, binding.usernamee2.getText().toString());
                    preferenceManager.putString(Constants.KEY_EMAIL, binding.inputEmail.getText().toString());
                    preferenceManager.putString(Constants.KEY_IMAGE, encodedImage);
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                    Toast.makeText(RegisterActivity.this, "Succes", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(exception -> {
                    loading(false);
                    showToast(exception.getMessage());
                });
    }

    private String encodeImage(Bitmap bitmap){
        int previewWidth = 150;
        int previewHeight = bitmap.getHeight() * previewWidth / bitmap.getWidth();
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG,50, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if(result.getResultCode() == RESULT_OK) {
                    if(result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        try {
                            InputStream inputStream = getContentResolver().openInputStream(imageUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            binding.roundedImageView.setImageBitmap(bitmap);
                            binding.addImage.setVisibility(View.GONE);
                            encodedImage = encodeImage(bitmap);
                        }catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });



    private boolean emailexists(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(Constants.KEY_COLLECTION_USERS).whereEqualTo(Constants.KEY_EMAIL, binding.inputEmail.getText().toString()).get().addOnCompleteListener(task -> {
            if(task.getResult() != null) {
                showToast("Email already exists");
            }
        });
        return true;
    }


    private void loading(Boolean isLoading) {
        if(isLoading) {
            binding.progressBar2.setVisibility(View.VISIBLE);
            binding.buttonSignUp.setVisibility(View.INVISIBLE);
        }else {
            binding.progressBar2.setVisibility(View.INVISIBLE);
            binding.buttonSignUp.setVisibility(View.VISIBLE);
        }
    }

    ////private void registerUser(){
        //EditText inputEmail = findViewById(R.id.inputEmail);
        //EditText usernamee2 = findViewById(R.id.usernamee2);
        //EditText PSN2 = findViewById(R.id.PSN2);
        //EditText inputPassword3 = findViewById(R.id.inputPassword3);
        //String Email = inputEmail.getText().toString();
        //String fullname = usernamee2.getText().toString();
        //String psn = PSN2.getText().toString();
        //String Password = inputPassword3.getText().toString();
        //Map<String, Object> user = new HashMap<>();
        //user.put("Email", Email);
        //user.put("fullname", fullname);
        //user.put("psn", psn);

        //if (Email.isEmpty() || fullname.isEmpty() || psn.isEmpty() || Password.isEmpty()) {
            //Toast.makeText(this, "Wypelnij wszystkie pola", Toast.LENGTH_LONG).show();
            //return;
        //}
        //db.collection("user")
                //.add(user)
                //.addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    //@Override
                    //public void onSuccess(DocumentReference documentReference) {
                        //Toast.makeText(RegisterActivity.this, "Succes", Toast.LENGTH_SHORT).show();
                    //}
                //}).addOnFailureListener(new OnFailureListener() {
            //@Override
            //public void onFailure(@NonNull Exception e) {
                //Toast.makeText(RegisterActivity.this, "Failed",Toast.LENGTH_SHORT).show();
            //}
        //});






    private void switchToLogin(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private Boolean isValidSignUpDetails() {
        if (encodedImage == null) {
            showToast("Select profile image");
            return false;

        } else if (binding.usernamee2.getText().toString().trim().isEmpty()) {
            showToast("Enter fullname");
            return false;
        } else if (binding.inputEmail.getText().toString().trim().isEmpty()) {
            showToast("Enter Email");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.inputEmail.getText().toString()).matches()) {
            showToast("Valid Email");
            return false;
        }else if(binding.inputEmail.getText().toString().equals(Constants.KEY_EMAIL)){
            showToast("Email already exists");
            return false;
        }else if(binding.inputPassword3.getText().toString().trim().isEmpty()) {
            showToast("Enter password");
            return false;
        }else if(binding.inputRePassword.getText().toString().trim().isEmpty()) {
            showToast("Confirm your password");
            return false;
        }else if(!binding.inputPassword3.getText().toString().equals(binding.inputRePassword.getText().toString())) {
            showToast("Password & confirm password must be same");
            return false;
        }else {
            return true;
        }

    }




}