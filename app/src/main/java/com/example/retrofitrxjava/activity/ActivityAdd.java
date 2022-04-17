package com.example.retrofitrxjava.activity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.retrofitrxjava.Product;
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

import java.util.Objects;

import static com.example.retrofitrxjava.fragment.HomeFragment.EXTRA_DATA;

public class ActivityAdd extends AppCompatAct<ActivityAddProductBinding> {

    private Uri imageUri;
    private ProductCategories productCategories = new ProductCategories();
    private SetupViewModel setupViewModel;
    private boolean isEdit;

    @Override
    protected void initLayout() {
        setupViewModel = new ViewModelProvider(this).get(SetupViewModel.class);
        setupViewModel.getMarket(db);

        Intent intent = getIntent();
        if (intent != null) {
            productCategories = (ProductCategories) intent.getSerializableExtra(EXTRA_DATA);
            isEdit = intent.getBooleanExtra("edit", false);
            bd.setItem(productCategories);
        }
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
            String price = giaBan(Long.parseLong(Objects.requireNonNull(bd.price.getText()).toString().trim()), Long.parseLong(Objects.requireNonNull(bd.lai.getText()).toString().trim()));
            if (imageUri != null) {
                showDialog();
                if (isEdit) {
                    if (productCategories != null && !StringUtil.isBlank(productCategories.getImage()) && productCategories.getImage().contains("imageProduct")) {
                        setupViewModel.deleteImage(productCategories);
                    }
                    setupViewModel.updateProduct(this, db, imageUri, productCategories, getFileEx(imageUri), new SetupViewModel.listener() {
                        @Override
                        public void updateSuccess() {
                            dismissDialog();
                            finish();
                        }
                    });

                } else {
                    StorageReference storageReferenc = FirebaseStorage.getInstance().getReference().child("imageProduct").child(System.currentTimeMillis() + "." + getFileEx(imageUri));
                    storageReferenc.putFile(imageUri).addOnCompleteListener(task -> storageReferenc.getDownloadUrl().addOnSuccessListener(uri -> {
                        productCategories = new ProductCategories(bd.tvName.getText().toString(), uri.toString(),
                                price, bd.description.getText().toString(), bd.theLoai.getText().toString());
                        db.collection("product_markets").document().set(productCategories);
                        Toast.makeText(this, "Tạo mới sản phẩm thành công", Toast.LENGTH_SHORT).show();
                        dismissDialog();
                        finish();
                        EventBus.getDefault().post(new UpdateMain());
                    }));
                }
                finish();
                dismissDialog();
            } else {
                if (isEdit) {
                    String id = productCategories.getDocumentId();
                    productCategories = new ProductCategories(Objects.requireNonNull(bd.tvName.getText()).toString(), productCategories.getImage(),
                            price, Objects.requireNonNull(bd.description.getText()).toString(), bd.theLoai.getText().toString());
                    db.collection("product_markets").document(id).set(productCategories);
                    Toast.makeText(this, "Thay đổi thành công !", Toast.LENGTH_SHORT).show();
                    dismissDialog();
                    finish();
                    EventBus.getDefault().post(new UpdateMain());

                } else {
                    Toast.makeText(this, "Nhập đủ dữ liệu", Toast.LENGTH_SHORT).show();
                }
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

    private String giaBan(long giagoc, long phantram) {
        long giaban = (giagoc * phantram) / 100;
        return String.valueOf(giagoc + giaban);
    }
}
