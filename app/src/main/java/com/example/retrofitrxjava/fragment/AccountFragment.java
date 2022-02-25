package com.example.retrofitrxjava.fragment;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.retrofitrxjava.Product;
import com.example.retrofitrxjava.R;
import com.example.retrofitrxjava.UserModel;
import com.example.retrofitrxjava.activity.MainActivity;
import com.example.retrofitrxjava.databinding.FragmentAccountBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jsoup.helper.StringUtil;

import static android.app.Activity.RESULT_OK;

public class AccountFragment extends BaseFragment<FragmentAccountBinding> {

    private Uri imageUri;

    public static AccountFragment newInstance() {
        Bundle args = new Bundle();
        AccountFragment fragment = new AccountFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initAdapter() {

    }

    @Override
    protected void initLiveData() {

    }

    @Override
    protected void initFragment() {
        showDialog();
        userModel = new UserModel();
        db.collection("account").document(currentUser.getUid()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    userModel = document.toObject(UserModel.class);
                }else {
                    userModel.setEmail(currentUser.getEmail());
                    userModel.setName(currentUser.getDisplayName());
                    db.collection("account").document(currentUser.getUid()).set(userModel);
                }
                binding.setItem(userModel);
            }
            dismissDialog();
        });

        binding.save.setOnClickListener(v -> {
            userModel = binding.getItem();
            if (imageUri != null) {
                StorageReference storageReferenc = FirebaseStorage.getInstance().getReference().child("image_account").child(System.currentTimeMillis() + "." + getFileEx(imageUri));
                storageReferenc.putFile(imageUri).addOnCompleteListener(task -> storageReferenc.getDownloadUrl().addOnSuccessListener(uri -> {
                    if (!StringUtil.isBlank(userModel.getImage())){
                        StorageReference delete = FirebaseStorage.getInstance().getReferenceFromUrl(userModel.getImage());
                        delete.delete().addOnSuccessListener(aVoid1 -> { });
                    }
                    userModel.setImage(uri.toString());
                    db.collection("account").document(currentUser.getUid()).set(userModel);
                    Toast.makeText(activity, "Save success", Toast.LENGTH_SHORT).show();
                    dismissDialog();
                }));
            } else {
                db.collection("account").document(currentUser.getUid()).set(userModel);
                Toast.makeText(activity, "Save success", Toast.LENGTH_SHORT).show();
            }
        });

        binding.camera.setOnClickListener(v -> {
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/*");
            photoPickerIntent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(photoPickerIntent, 1);
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            final Uri imageUri = data.getData();
            this.imageUri = imageUri;
            Glide.with(this)
                    .load(imageUri)
                    .error(R.drawable.ic_baseline_warning_24)
                    .into(binding.img);

        } else {
            Toast.makeText(activity, "You haven't picked Image", Toast.LENGTH_LONG).show();
        }
    }

    private String getFileEx(Uri u) {
        ContentResolver contentResolver = activity.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(u));
    }


    @Override
    protected int getID() {
        return R.layout.fragment_account;
    }
}
