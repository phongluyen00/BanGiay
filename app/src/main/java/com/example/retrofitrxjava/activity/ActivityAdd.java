package com.example.retrofitrxjava.activity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.retrofitrxjava.R;
import com.example.retrofitrxjava.databinding.ActivityAddProductBinding;
import com.example.retrofitrxjava.dialog.BottomSheetMarkets;
import com.example.retrofitrxjava.event.UpdateMain;
import com.example.retrofitrxjava.model.Markets;
import com.example.retrofitrxjava.model.ProductCategories;
import com.example.retrofitrxjava.viewmodel.SetupViewModel;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.greenrobot.eventbus.EventBus;
import org.jsoup.helper.StringUtil;

public class ActivityAdd extends AppCompatAct<ActivityAddProductBinding> {

    private Uri imageUri;
    private ProductCategories productCategories = new ProductCategories();
    private SetupViewModel setupViewModel;

    @Override
    protected void initLayout() {
        setupViewModel = new ViewModelProvider(this).get(SetupViewModel.class);
        setupViewModel.getMarket(db);
        bd.icon.setOnClickListener(v -> {
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/*");
            photoPickerIntent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(photoPickerIntent, 1);
        });
        bd.back.setOnClickListener(v -> finish());
        bd.theLoai.setOnClickListener(v -> {
            BottomSheetMarkets bottomSheetMarkets = new BottomSheetMarkets(new BottomSheetMarkets.listener() {
                @Override
                public void onClick(Markets markets) {
                    bd.theLoai.setText(markets.getDocumentId());
                }
            }, setupViewModel.getMarketsLiveData().getValue());
            bottomSheetMarkets.show(getSupportFragmentManager(), bottomSheetMarkets.getTag());
        });
        bd.submit.setOnClickListener(v -> {
            if (imageUri != null) {
                showDialog();
                StorageReference storageReferenc = FirebaseStorage.getInstance().getReference().child("imageProduct").child(System.currentTimeMillis() + "." + getFileEx(imageUri));
                storageReferenc.putFile(imageUri).addOnCompleteListener(task -> storageReferenc.getDownloadUrl().addOnSuccessListener(uri -> {
//                    if (!StringUtil.isBlank(productCategories.getImage())) {
//                        StorageReference delete = FirebaseStorage.getInstance().getReferenceFromUrl(userModel.getImage());
//                        delete.delete().addOnSuccessListener(aVoid1 -> {
//                        });
//                    }
                    productCategories = new ProductCategories(bd.tvName.getText().toString(), uri.toString(),
                            bd.price.getText().toString(), bd.description.getText().toString(), bd.theLoai.getText().toString());
                    db.collection("product_markets").document().set(productCategories);
                    Toast.makeText(this, "Tạo mới sản phẩm thành công", Toast.LENGTH_SHORT).show();
                    dismissDialog();
                    finish();
                    EventBus.getDefault().post(new UpdateMain());
                }));
            } else {
                Toast.makeText(this, "Nhập đủ dữ liệu", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getFileEx(Uri u) {
        ContentResolver contentResolver = this.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(u));
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
                    .into(bd.icon);

        } else {
            Toast.makeText(this, "You haven't picked Image", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected int getID() {
        return R.layout.activity_add_product;
    }
}
