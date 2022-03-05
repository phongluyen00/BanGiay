package com.example.retrofitrxjava.activity;

import android.content.Intent;
import android.util.Log;
import android.view.View;

import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.example.retrofitrxjava.R;
import com.example.retrofitrxjava.adapter.DetailImageAdapter;
import com.example.retrofitrxjava.adapter.MutilAdt;
import com.example.retrofitrxjava.databinding.DetailActivityBinding;
import com.example.retrofitrxjava.dialog.BDialogFragment;
import com.example.retrofitrxjava.dialog.DialogPDFViewer;
import com.example.retrofitrxjava.model.EBook;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class DetailEBookDialog extends BDialogFragment<DetailActivityBinding> {

    private EBook eBook;
    private MutilAdt<EBook> adapterSimilar;
    private List<EBook> eBookList = new ArrayList<>();
    private int position = 0;
    private DetailImageAdapter slideBook;
    private EBook eBookSave;

    public DetailEBookDialog(EBook eBook, int position,  List<EBook> eBookList) {
        this.eBook = eBook;
        this.eBookList = eBookList;
        this.position = position;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.detail_activity;
    }

    @Override
    protected void initLayout() {
        if (eBook != null){
            binding.setItem(eBook);
        }

        eBook = eBookList.get(position);
        slideBook = new DetailImageAdapter(getContext(), eBookList);
        binding.viewPager.setClipToPadding(false);
        binding.viewPager.setPageMargin(12);
        binding.viewPager.setAdapter(slideBook);

        binding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrollStateChanged(int state) {}
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            public void onPageSelected(int position) {
                eBook = eBookList.get(position);
                binding.setItem(eBook);
                // Check if this is the page you want.
            }
        });

        binding.viewPager.setCurrentItem(position);

        binding.back.setOnClickListener(v -> dismiss());

        binding.read.setOnClickListener(v -> {
            DialogPDFViewer dialogPDFViewer = new DialogPDFViewer(eBook,eBookSave);
            dialogPDFViewer.show(getChildFragmentManager(),dialogPDFViewer.getTag());
        });

        binding.share.setOnClickListener(new View.OnClickListener() {
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

        binding.favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFavorite(eBook);
            }
        });

        db.collection("continue_reading").whereEqualTo("uid", currentUser.getUid()).whereEqualTo("documentId", eBook.getDocumentId()).get().
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
    }

    private void addFavorite(EBook eBook){
        eBook.setId_document(eBook.getDocumentId());
        eBook.setUid(currentUser.getUid());
        binding.favorite.setImageResource(R.drawable.ic_baseline_favorite_24);
        db.collection("book_favorite").document().set(eBook);
    }
}
