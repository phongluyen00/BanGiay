package com.example.retrofitrxjava.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.retrofitrxjava.UserModel;
import com.example.retrofitrxjava.activity.MainActivity;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SetupViewModel extends ViewModel {

    private MutableLiveData<UserModel> userModelMutableLiveData = new MutableLiveData<>();

    public MutableLiveData<UserModel> getUserModelMutableLiveData() {
        return userModelMutableLiveData;
    }

    public void loadAccount(FirebaseFirestore db, FirebaseUser firebaseUser){
        db.collection("account").document(firebaseUser.getUid()).get().addOnCompleteListener(task -> {
            UserModel userModel = new UserModel();
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    userModel = document.toObject(UserModel.class);
                }else {
                    userModel.setDocumentId(document.getId());
                    userModel.setEmail(firebaseUser.getEmail());
                    userModel.setName(firebaseUser.getDisplayName());
                    db.collection("account").document(firebaseUser.getUid()).set(userModel);
                }
                MainActivity.userModel = userModel;
                userModelMutableLiveData.setValue(userModel);
            }
        });
    }
}
