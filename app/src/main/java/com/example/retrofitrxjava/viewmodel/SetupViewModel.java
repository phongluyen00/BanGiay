package com.example.retrofitrxjava.viewmodel;

import android.content.Context;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.retrofitrxjava.UserModel;
import com.example.retrofitrxjava.activity.MainActivity;
import com.example.retrofitrxjava.activity.MainActivityAdmin;
import com.example.retrofitrxjava.model.Markets;
import com.example.retrofitrxjava.model.ProductCategories;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jsoup.helper.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class SetupViewModel extends ViewModel {

    private MutableLiveData<UserModel> userModelMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<ProductCategories> productCategoriesMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<List<ProductCategories>> productCategoriesList = new MutableLiveData<>();
    private MutableLiveData<ProductCategories> productCart = new MutableLiveData<>();
    private MutableLiveData<List<Markets>> marketsLiveData = new MutableLiveData<>();
    private MutableLiveData<ArrayList<ProductCategories>> productMarkets = new MutableLiveData<>();
    private MutableLiveData<List<ProductCategories>> deleteProduct = new MutableLiveData<>();

    public MutableLiveData<List<ProductCategories>> getDeleteProduct() {
        return deleteProduct;
    }

    public MutableLiveData<List<Markets>> getMarketsLiveData() {
        return marketsLiveData;
    }

    public MutableLiveData<ProductCategories> getProductCart() {
        return productCart;
    }

    public MutableLiveData<List<ProductCategories>> getProductCategoriesList() {
        return productCategoriesList;
    }

    public MutableLiveData<ProductCategories> getProductCategoriesMutableLiveData() {
        return productCategoriesMutableLiveData;
    }

    public MutableLiveData<UserModel> getUserModelMutableLiveData() {
        return userModelMutableLiveData;
    }

    public MutableLiveData<ArrayList<ProductCategories>> getProductMarkets() {
        return productMarkets;
    }

    public void loadAccount(FirebaseFirestore db, FirebaseUser firebaseUser) {
        db.collection("account").document(firebaseUser.getUid()).get().addOnCompleteListener(task -> {
            UserModel userModel = new UserModel();
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    userModel = document.toObject(UserModel.class);
                } else {
                    userModel.setDocumentId(document.getId());
                    userModel.setEmail(firebaseUser.getEmail());
                    userModel.setName(firebaseUser.getDisplayName());
                    db.collection("account").document(firebaseUser.getUid()).set(userModel);
                }
                MainActivity.userModel = userModel;
                MainActivityAdmin.userModel = userModel;
                userModelMutableLiveData.setValue(userModel);
            }
        });
    }

    public void getDocumentIdFavorite(FirebaseFirestore db, FirebaseUser currentUser, ProductCategories productCategories) {
        db.collection("shopping_favorite").whereEqualTo("uid", currentUser.getUid()).whereEqualTo("id_document", productCategories.getDocumentId()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {
                    ProductCategories product = documentSnapshot.toObject(ProductCategories.class);
                    productCategoriesMutableLiveData.setValue(product);
                }
            }
        });
    }

    public void checkProductExitsCart(FirebaseFirestore db, FirebaseUser currentUser, ProductCategories productCategories) {
        db.collection("cart").whereEqualTo("uid", currentUser.getUid()).whereEqualTo("id_document", productCategories.getDocumentId()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                ProductCategories product = null;
                for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {
                    product = documentSnapshot.toObject(ProductCategories.class);
                    product.setDocumentId(documentSnapshot.getId());
                    break;
                }
                productCart.postValue(product);
            }
        });
    }

    public void getProductMarket(FirebaseFirestore db, String id_Markets) {
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

    public void getMarket(FirebaseFirestore db) {
        List<Markets> productList = new ArrayList<>();
        db.collection("my_markets").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {
                    Markets product = documentSnapshot.toObject(Markets.class);
                    if (product != null) {
                        product.setDocumentId(documentSnapshot.getId());
                        productList.add(product);
                    }
                }
                marketsLiveData.setValue(productList);
            }
        });
    }

    public void getProductMarketHome(FirebaseFirestore db) {
        ArrayList<ProductCategories> productCategoriesList = new ArrayList<>();
        db.collection("product_markets").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {
                    ProductCategories product = documentSnapshot.toObject(ProductCategories.class);
                    if (product != null) {
                        product.setDocumentId(documentSnapshot.getId());
                        productCategoriesList.add(product);
                    }
                }
                productMarkets.postValue(productCategoriesList);
            }
        });
    }

    public void deleteProduct(Context context, FirebaseFirestore db, ProductCategories productCategories, List<ProductCategories> productCategoriesList) {
        db.collection("product_markets").document(productCategories.getDocumentId()).delete().addOnSuccessListener(aVoid -> {
            if (!StringUtil.isBlank(productCategories.getImage())) {
                StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(productCategories.getImage());
                storageReference.delete().addOnSuccessListener(aVoid1 -> {
                });
            }
            productCategoriesList.remove(productCategories);
            deleteProduct.postValue(productCategoriesList);
        }).addOnFailureListener(command -> {
            deleteProduct.postValue(null);
            Toast.makeText(context, "Xóa thất bại", Toast.LENGTH_SHORT).show();
        });
    }
}
