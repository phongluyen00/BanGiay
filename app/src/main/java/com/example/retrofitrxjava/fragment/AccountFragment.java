package com.example.retrofitrxjava.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.retrofitrxjava.R;
import com.example.retrofitrxjava.UserModel;
import com.example.retrofitrxjava.activity.MainActivity;
import com.example.retrofitrxjava.databinding.FragmentAccountBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

public class AccountFragment extends BaseFragment<FragmentAccountBinding> {

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
        db.collection("account").document(currentUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
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
            }
        });

        binding.save.setOnClickListener(v -> {
            userModel = binding.getItem();
            db.collection("account").document(currentUser.getUid()).set(userModel);
            Toast.makeText(activity, "Save success", Toast.LENGTH_SHORT).show();
        });



    }

    @Override
    protected int getID() {
        return R.layout.fragment_account;
    }
}
