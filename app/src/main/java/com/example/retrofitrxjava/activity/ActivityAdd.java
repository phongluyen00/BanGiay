package com.example.retrofitrxjava.activity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.retrofitrxjava.ModelManager;
import com.example.retrofitrxjava.Product;
import com.example.retrofitrxjava.R;
import com.example.retrofitrxjava.constanst.Constants;
import com.example.retrofitrxjava.databinding.ActivityAddProductBinding;
import com.example.retrofitrxjava.dialog.BottomSheetMarkets;
import com.example.retrofitrxjava.event.UpdateMain;
import com.example.retrofitrxjava.model.Markets;
import com.example.retrofitrxjava.model.ProductCategories;
import com.example.retrofitrxjava.viewmodel.SetupViewModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.greenrobot.eventbus.EventBus;
import org.jsoup.helper.StringUtil;

import java.util.List;
import java.util.Objects;

import static com.example.retrofitrxjava.fragment.HomeFragment.EXTRA_DATA;

public class ActivityAdd extends AppCompatAct<ActivityAddProductBinding> {

    private Uri imageUri;
    private ProductCategories productCategories = new ProductCategories();
    private SetupViewModel setupViewModel;
    private boolean isEdit;
    private ModelManager objManager1331;
    private ModelManager objManager632;
    private ModelManager objManager112;

    @Override
    protected void initLayout() {
        setupViewModel = new ViewModelProvider(this).get(SetupViewModel.class);
        setupViewModel.getMarket(db);
        setupViewModel.loadManager(db, currentUser);

        setupViewModel.readDataManager(db);

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
            if (!validate()) {
                Toast.makeText(this, "Nhập đủ dữ liệu", Toast.LENGTH_SHORT).show();
                return;
            }
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
                        productCategories = new ProductCategories(bd.tvName.getText().toString(),
                                uri.toString(),
                                price,
                                Objects.requireNonNull(bd.description.getText()).toString(),
                                bd.theLoai.getText().toString(),
                                bd.price.getText().toString().trim(),
                                Objects.requireNonNull(bd.quantity.getText()).toString().trim());
                        db.collection("product_markets").document().set(productCategories);
                        Toast.makeText(this, "Tạo mới sản phẩm thành công", Toast.LENGTH_SHORT).show();

                        // update 1331, 632, 112
                        if (objManager1331 != null && objManager112 != null && objManager632 != null){
                            long price1331 = Long.parseLong(value1331(Long.parseLong(Objects.requireNonNull(bd.price.getText()).toString().trim())))
                                    + Long.parseLong(objManager1331.getIn_debt());
                            setupViewModel.updateDataManager(db, objManager1331.getDocumentId(), Constants.COLLECTION_IN_DEBT, String.valueOf(price1331));
                            long price632 = Long.parseLong(objManager632.getIn_debt()) + Long.parseLong(bd.price.getText().toString());
                            setupViewModel.updateDataManager(db, objManager632.getDocumentId(), Constants.COLLECTION_IN_DEBT, String.valueOf(price632));
                            long price112 = price632 + price1331 + Long.parseLong(objManager112.getIn_debt());
                            setupViewModel.updateDataManager(db, objManager112.getDocumentId(), Constants.COLLECTION_IN_DEBT, String.valueOf(price112));
                        }
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
                            price, Objects.requireNonNull(bd.description.getText()).toString(), bd.theLoai.getText().toString(), Objects.requireNonNull(bd.price.getText()).toString().trim(), Objects.requireNonNull(bd.quantity.getText()).toString().trim());
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

        initObserver();
    }

    private void initObserver() {
        setupViewModel.getListModelManagerMutableLiveData().observe(this, modelManagers -> {
            if (modelManagers.size() > 0){
                for (ModelManager modelManager : modelManagers) {
                    if (modelManager.getCode().equalsIgnoreCase(Constants.CODE_632)){
                        objManager632 = modelManager;
                    }
                    if (modelManager.getCode().equalsIgnoreCase(Constants.CODE_1331)){
                        objManager1331 = modelManager;
                    }

                    if (modelManager.getCode().equalsIgnoreCase(Constants.CODE_112)){
                        objManager112 = modelManager;
                    }
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
                    .error(R.drawable.placeholder)
                    .into(bd.icon);

        } else {
            Toast.makeText(this, "You haven't picked Image", Toast.LENGTH_LONG).show();
        }
    }

    private boolean validate() {
        if (StringUtil.isBlank(bd.tvName.getText().toString()) || StringUtil.isBlank(bd.price.getText().toString()) ||
                StringUtil.isBlank(bd.lai.getText().toString()) || StringUtil.isBlank(bd.description.getText().toString()) ||
                StringUtil.isBlank(bd.quantity.getText().toString()) || StringUtil.isBlank(bd.theLoai.getText().toString())) {
            return false;
        }
        return true;
    }

    @Override
    protected int getID() {
        return R.layout.activity_add_product;
    }

    private String giaBan(long giagoc, long phantram) {
        long giaban = (giagoc * phantram) / 100;
        return String.valueOf(giagoc + giaban);
    }

    private String value1331(long giagoc) {
        long value = (giagoc * 10) / 100;
        return String.valueOf(giagoc + value);
    }

}
