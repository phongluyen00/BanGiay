package com.example.retrofitrxjava;

import android.view.View;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.retrofitrxjava.adapter.MutilAdt;
import com.example.retrofitrxjava.model.EBook;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class Utils extends ViewModel {

    public MutableLiveData<List<EBook>> eBookMutableLiveData = new MutableLiveData<>();

    public MutableLiveData<List<EBook>> getBookMutableLiveData() {
        return eBookMutableLiveData;
    }

    public void getDataAndType(String type, FirebaseFirestore db, String uid){
        db.collection("db_comics").whereEqualTo("type",type).get().
                addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ArrayList<EBook> continueReadingList = new ArrayList<>();
                        for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {
                            EBook product = documentSnapshot.toObject(EBook.class);
                            if (product != null) {
                                continueReadingList.add(product);
                            }
                        }
                        eBookMutableLiveData.setValue(continueReadingList);
                    }
                });
    }
}
