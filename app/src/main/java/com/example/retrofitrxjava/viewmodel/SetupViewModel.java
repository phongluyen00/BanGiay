package com.example.retrofitrxjava.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.retrofitrxjava.R;
import com.example.retrofitrxjava.UserModel;
import com.example.retrofitrxjava.activity.MainActivity;
import com.example.retrofitrxjava.adapter.MutilAdt;
import com.example.retrofitrxjava.model.ProductCategories;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class SetupViewModel extends ViewModel {

    private MutableLiveData<UserModel> userModelMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<ProductCategories> productCategoriesMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<List<ProductCategories>> productCategoriesList = new MutableLiveData<>();

    public MutableLiveData<List<ProductCategories>> getProductCategoriesList() {
        return productCategoriesList;
    }

    public MutableLiveData<ProductCategories> getProductCategoriesMutableLiveData() {
        return productCategoriesMutableLiveData;
    }

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

    public void getDocumentIdFavorite(FirebaseFirestore db, FirebaseUser currentUser, ProductCategories productCategories){
        db.collection("shopping_favorite").whereEqualTo("uid", currentUser.getUid()).whereEqualTo("id_document",productCategories.getDocumentId()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {
                    ProductCategories product = documentSnapshot.toObject(ProductCategories.class);
                    productCategoriesMutableLiveData.setValue(product);
                }
            }
        });
    }

    public void getProductMarket(FirebaseFirestore db, String id_Markets){
        db.collection("product_markets").whereEqualTo("id_markets", id_Markets).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<ProductCategories> productCategories = new ArrayList<>();
                for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {
                    ProductCategories product = documentSnapshot.toObject(ProductCategories.class);
                    if (product != null) {
                        product.setDocumentId(documentSnapshot.getId());
                        productCategories.add(product);
                    }
                }
                productCategoriesList.postValue(productCategories);
            }
        });
    }
}