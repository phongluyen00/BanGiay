package com.example.retrofitrxjava.fragment;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.retrofitrxjava.Product;
import com.example.retrofitrxjava.R;
import com.example.retrofitrxjava.UserModel;
import com.example.retrofitrxjava.activity.ActivityAdd;
import com.example.retrofitrxjava.activity.LoginActivity;
import com.example.retrofitrxjava.activity.MainActivity;
import com.example.retrofitrxjava.activity.MainActivityAdmin;
import com.example.retrofitrxjava.databinding.CustomLayoutBinding;
import com.example.retrofitrxjava.databinding.FragmentAccountBinding;
import com.example.retrofitrxjava.dialog.BottomSheetEditAdmin;
import com.example.retrofitrxjava.dialog.BottomSheetStatistic;
import com.example.retrofitrxjava.viewmodel.SetupViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jsoup.helper.StringUtil;

import static android.app.Activity.RESULT_OK;

public class AccountFragment extends BaseFragment<FragmentAccountBinding> {

    private Uri imageUri;
    private SetupViewModel setupViewModel;

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
        setupViewModel = new ViewModelProvider(this).get(SetupViewModel.class);
        userModel = new UserModel();
        if (MainActivity.userModel != null){
            userModel = MainActivity.userModel;
            binding.setItem(userModel);
        }else {
            setupViewModel.loadAccount(db,currentUser);
        }

        setupViewModel.getUserModelMutableLiveData().observe(this, new Observer<UserModel>() {
            @Override
            public void onChanged(UserModel userModel) {
                binding.setItem(userModel);
            }
        });

        binding.save.setOnClickListener(v -> {
            userModel = binding.getItem();
            if (imageUri != null) {
                showDialog();
                StorageReference storageReferenc = FirebaseStorage.getInstance().getReference().child("uploads").child(System.currentTimeMillis() + "." + getFileEx(imageUri));
                storageReferenc.putFile(imageUri).addOnCompleteListener(task -> storageReferenc.getDownloadUrl().addOnSuccessListener(uri -> {
                    if (!StringUtil.isBlank(userModel.getImage())){
                        StorageReference delete = FirebaseStorage.getInstance().getReferenceFromUrl(userModel.getImage());
                        delete.delete().addOnSuccessListener(aVoid1 -> { });
                    }
                    userModel.setImage(uri.toString());
                    MainActivity.userModel = userModel;
                    db.collection("account").document(currentUser.getUid()).set(userModel);
                    Toast.makeText(activity, "Save success", Toast.LENGTH_SHORT).show();
                    dismissDialog();
                }));
            } else {
                MainActivity.userModel = userModel;
                db.collection("account").document(currentUser.getUid()).set(userModel);
                Toast.makeText(activity, "Save success", Toast.LENGTH_SHORT).show();
            }

        });

        binding.include.statistic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetStatistic bottomSheetStatistic = new BottomSheetStatistic();
                bottomSheetStatistic.show(getChildFragmentManager(),bottomSheetStatistic.getTag());
            }
        });

        binding.include.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetEditAdmin bottomSheetEditAdmin = new BottomSheetEditAdmin();
                bottomSheetEditAdmin.show(getChildFragmentManager(),bottomSheetEditAdmin.getTag());
            }
        });

        binding.include.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, ActivityAdd.class);
                startActivity(intent);
            }
        });
        binding.camera.setOnClickListener(v -> {
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/*");
            photoPickerIntent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(photoPickerIntent, 1);
        });

        binding.logout.setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(activity, LoginActivity.class);
            startActivity(intent);
            activity.finish();
        });

        binding.changePassword.setOnClickListener(v -> checkAccount());
    }

    private void checkAccount() {
        if (StringUtil.isBlank(userModel.getPassword())){
            Toast.makeText(activity, "Account condition change password is not enough !", Toast.LENGTH_SHORT).show();
            return;
        }
        showDialog();
        db.collection("account").document(currentUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        userModel = document.toObject(UserModel.class);
                        showAlertDialogButtonClicked(userModel);
                    } else {
                        Toast.makeText(activity, "Account condition change password is not enough !", Toast.LENGTH_SHORT).show();
                    }
                    dismissDialog();
                }
                dismissDialog();
            }
        });
    }

    public void showAlertDialogButtonClicked(UserModel userModel) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Change password");
        CustomLayoutBinding customLayout = DataBindingUtil.inflate(LayoutInflater.from(activity), R.layout.custom_layout, null, false);
        builder.setView(customLayout.getRoot());
        AlertDialog dialog = builder.create();
        customLayout.setItem(userModel);
        customLayout.save.setOnClickListener(v -> {
            if (StringUtil.isBlank(getText(customLayout.password)) ||
                    !userModel.getPassword().equalsIgnoreCase(getText(customLayout.password)) ||
                    getText(customLayout.password).length() < 7) {
                customLayout.error.setText("Invalid old password");
                customLayout.error.setVisibility(View.VISIBLE);
                return;
            }
            if (StringUtil.isBlank(getText(customLayout.edtPasswordNew)) || getText(customLayout.edtPasswordNew).length() < 7) {
                customLayout.error.setText("Invalid new password");
                customLayout.error.setVisibility(View.VISIBLE);
                return;
            }
            customLayout.error.setVisibility(View.GONE);
            currentUser.updatePassword(getText(customLayout.edtPasswordNew))
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            userModel.setPassword(getText(customLayout.edtPasswordNew));
                            db.collection("account").document(currentUser.getUid()).set(userModel);
                            Toast.makeText(activity, "Change password success", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(activity, "Change password failed", Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                    });
            hideKeyboard(activity);

        });
        dialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            assert data != null;
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

    @Override
    public void onStart() {
        super.onStart();
    }
}
