package com.example.retrofitrxjava.activity;

import android.content.Intent;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.example.retrofitrxjava.ItemOnclickListener;
import com.example.retrofitrxjava.R;
import com.example.retrofitrxjava.Utils;
import com.example.retrofitrxjava.adapter.DetailImageAdapter;
import com.example.retrofitrxjava.adapter.MutilAdt;
import com.example.retrofitrxjava.databinding.DetailActivityBinding;
import com.example.retrofitrxjava.dialog.BDialogFragment;
import com.example.retrofitrxjava.dialog.BottomSheetComment;
import com.example.retrofitrxjava.dialog.DialogPDFViewer;
import com.example.retrofitrxjava.model.EBook;
import com.example.retrofitrxjava.model.ProductCategories;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.paypal.pyplcheckout.pojo.To;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;
import org.jsoup.helper.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class DetailEBookDialog extends AppCompatAct<DetailActivityBinding> implements PaymentResultListener, ItemOnclickListener<EBook>, MutilAdt.ListItemListener{

    private EBook eBook;
    private MutilAdt<EBook> eBookAdapter;
    private List<EBook> eBookList;
    private int position = 0;
    private DetailImageAdapter slideBook;
    private EBook eBookSave;
    private boolean isFavorite;
    private Utils viewModel;
    private boolean isReading;

    @Override
    protected void initLayout() {

        Intent intent = getIntent();
        if (intent != null){
            eBook = (EBook) intent.getSerializableExtra("ebook");
            position = intent.getIntExtra("index",0);
            eBookList= (List<EBook>) intent.getSerializableExtra("list");
            isReading = intent.getBooleanExtra("isReading",false);
        }

        if (eBook != null) {
            bd.setItem(eBook);
        }
        viewModel = new ViewModelProvider(this).get(Utils.class);
        viewModel.getDataAndType("see", db, currentUser.getUid());

        viewModel.getBookMutableLiveData().observe(this, new Observer<List<EBook>>() {
            @Override
            public void onChanged(List<EBook> eBooks) {
                if (eBooks != null && eBooks.size() > 0){
                    eBookAdapter.setDt((ArrayList<EBook>) eBooks);
                }
            }
        });

        eBook = eBookList.get(position);

        if (isReading){
            bd.favorite.setVisibility(View.GONE);
        }
        slideBook = new DetailImageAdapter(this, eBookList);
        bd.viewPager.setClipToPadding(false);
        bd.viewPager.setPageMargin(12);
        bd.viewPager.setAdapter(slideBook);

        bd.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrollStateChanged(int state) {
            }

            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            public void onPageSelected(int position) {
                eBook = eBookList.get(position);
                bd.setItem(eBook);
                // Check if this is the page you want.
            }
        });

        bd.viewPager.setCurrentItem(position);

        bd.back.setOnClickListener(v -> finish());

        bd.read.setOnClickListener(v -> {
            DialogPDFViewer dialogPDFViewer = new DialogPDFViewer(eBook, eBookSave);
            dialogPDFViewer.show(getSupportFragmentManager(), dialogPDFViewer.getTag());
        });

        bd.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, eBook.getFile_pdf());
                sendIntent.setType("text/plain");
                Intent shareIntent = Intent.createChooser(sendIntent, null);
                startActivity(shareIntent);
            }
        });

        bd.payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPayment();
            }
        });

        bd.favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFavorite(eBook);
            }
        });

        eBookAdapter = new MutilAdt<>(this, R.layout.item_trending_books);
        bd.rclSimilar.setAdapter(eBookAdapter);
        eBookAdapter.setListener(this);

        if (!StringUtil.isBlank(eBook.getId_book())) {
            db.collection("continue_reading").whereEqualTo("uid", currentUser.getUid()).
                    whereEqualTo("id_book", eBook.getId_book()).get().
                    addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {
                                EBook product = documentSnapshot.toObject(EBook.class);
                                if (product != null) {
                                    product.setDocumentId(documentSnapshot.getId());
                                    eBookSave = product;
                                    Log.d("AAAAAAAAAA", eBookSave.getDocumentId() + "--" + eBookSave.getPage() + " page");
                                }
                            }
                        }
                    });
        } else {
            db.collection("continue_reading").whereEqualTo("uid", currentUser.getUid()).get().
                    addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {
                                EBook product = documentSnapshot.toObject(EBook.class);
                                if (product != null) {
                                    product.setDocumentId(documentSnapshot.getId());
                                    if (product.getId_book().equalsIgnoreCase(eBook.getDocumentId())) {
                                        eBookSave = product;
                                        Log.d("AAAAAAAAAA", eBookSave.getDocumentId() + "--" + eBookSave.getPage() + " page");
                                        break;
                                    }
                                }
                            }
                        }
                    });
        }

        bd.comment.setOnClickListener(v -> {
            BottomSheetComment bottomSheetComment = new BottomSheetComment(eBook, MainActivity.userModel);
            bottomSheetComment.show(getSupportFragmentManager(), bottomSheetComment.getTag());
        });

        if (!eBook.isFavorite()) {
            db.collection("book_favorite").whereEqualTo("uid", currentUser.getUid()).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {
                        EBook eBookFavorite = documentSnapshot.toObject(EBook.class);
                        if (eBookFavorite != null && eBook.getDocumentId().equalsIgnoreCase(eBookFavorite.getId_book())) {
                            isFavorite = true;
                            bd.favorite.setImageResource(R.drawable.ic_baseline_favorite_24);
                            break;
                        }
                    }
                }
            });
        } else {
            isFavorite = true;
            bd.favorite.setImageResource(R.drawable.ic_baseline_favorite_24);
        }
    }

    @Override
    protected int getID() {
        return R.layout.detail_activity;
    }

    private void addFavorite(EBook eBook) {

        eBook.setId_book(eBook.getDocumentId());
        eBook.setUid(currentUser.getUid());

        if (!isFavorite) {
            bd.favorite.setImageResource(R.drawable.ic_baseline_favorite_24);
            db.collection("book_favorite").document().set(eBook);
            isFavorite = true;
        } else {
            Snackbar snackbar = Snackbar
                    .make(bd.main, "The product has been liked !", Snackbar.LENGTH_LONG);
            snackbar.show();
        }

    }

    @Override
    public void onItemBookClick(EBook eBook, int position) {

    }

    @Override
    public void onRemove(EBook eBook, int position) {

    }

    public void startPayment() {
        Checkout checkout = new Checkout();
        checkout.setKeyID("rzp_test_bvPYonKyVPrUPM");
        /**
         * Pass your payment options to the Razorpay Checkout as a JSONObject
         */
        try {
            JSONObject options = new JSONObject();

            options.put("name", "Merchant Name");
            options.put("description", "Reference No. #123456");
            options.put("currency", "INR");
            options.put("amount", "300");//pass amount in currency subunits
            options.put("prefill.email", "phongluyen1998@example.com");
            options.put("prefill.contact","0358844343");

            checkout.open(this, options);

        } catch(Exception e) {
            Log.e("TAG", "Error in starting Razorpay Checkout", e);
        }
    }
    @Override
    public void onPaymentSuccess(String razorpayPaymentID) {
        Toast.makeText(this, "Payment successfully done! " + razorpayPaymentID, Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onPaymentError(int code, String response) {
        try {
            Toast.makeText(this, "Payment error please try again", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e("OnPaymentError", "Exception in onPaymentError", e);
        }
    }
}
