package com.example.retrofitrxjava.fragment;

import android.os.Bundle;

import com.example.retrofitrxjava.ItemOnLongclickListener;
import com.example.retrofitrxjava.ItemOnclickListener;
import com.example.retrofitrxjava.R;
import com.example.retrofitrxjava.activity.DetailEBookDialog;
import com.example.retrofitrxjava.adapter.MutilAdt;
import com.example.retrofitrxjava.databinding.LayoutRecruitmentBinding;
import com.example.retrofitrxjava.model.EBook;
import com.example.retrofitrxjava.model.MessageEvent;
import com.example.retrofitrxjava.onItemBookContinueClickListener;
import com.google.firebase.firestore.DocumentSnapshot;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class MainFragment extends BaseFragment<LayoutRecruitmentBinding> implements ItemOnclickListener<EBook>, ItemOnLongclickListener<EBook>, MutilAdt.ListItemListener {

    private MutilAdt<EBook> eBookAdapter;
    private MutilAdt<EBook> authorAdapter;
    private MutilAdt<EBook> continueReadingAdapter;
    private List<EBook> eBookArrayList = new ArrayList<>();
    private List<EBook> eBookContinueReading = new ArrayList<>();

    public static MainFragment newInstance() {
        Bundle args = new Bundle();
        MainFragment fragment = new MainFragment();
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
        onReload();
        // read author
        db.collection("db_author").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                ArrayList<EBook> authorList = new ArrayList<>();
                for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {
                    EBook product = documentSnapshot.toObject(EBook.class);
                    if (product != null) {
                        product.setDocumentId(documentSnapshot.getId());
                        authorList.add(product);
                    }
                }
                authorAdapter = new MutilAdt<>(activity, R.layout.item_account);
                binding.rclAuthor.setAdapter(authorAdapter);
                authorAdapter.setDt(authorList);
                dismissDialog();
            }
        });
    }

    @Override
    protected int getID() {
        return R.layout.layout_recruitment;
    }


    @Override
    public void onItemBookClick(EBook eBook, int position) {
        DetailEBookDialog detailEBookDialog = new DetailEBookDialog(eBook, position, eBookArrayList);
        detailEBookDialog.show(getChildFragmentManager(), detailEBookDialog.getTag());
    }

    @Override
    public void onRemove(EBook eBook, int position) {

    }

    @Override
    public void onResume() {
        super.onResume();
        onReload();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        // Do something
        if (event != null){
            onReload();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    private void onReload(){
        getAllData(products -> {
            eBookArrayList.clear();
            eBookArrayList.addAll(products);
            eBookAdapter = new MutilAdt<>(activity, R.layout.item_trending_books);
            binding.rvRecruitment.setAdapter(eBookAdapter);
            eBookAdapter.setDt((ArrayList<EBook>) products);
            eBookAdapter.setListener(this);
        });

        // read
        db.collection("continue_reading").whereEqualTo("uid", currentUser.getUid()).get().
                addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ArrayList<EBook> continueReadingList = new ArrayList<>();
                        for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {
                            EBook product = documentSnapshot.toObject(EBook.class);
                            if (product != null) {
                                product.setDocumentId(documentSnapshot.getId());
                                continueReadingList.add(product);
                            }
                        }
                        eBookContinueReading.clear();
                        eBookContinueReading.addAll(continueReadingList);
                        continueReadingAdapter = new MutilAdt<>(activity, R.layout.item_continue_reading);
                        binding.rcContinue.setAdapter(continueReadingAdapter);
                        continueReadingAdapter.setDt(continueReadingList);
                        continueReadingAdapter.setListener(this);
                    }
                });
    }

    @Override
    public void onItemLongClick(EBook eBook, int index) {
        DetailEBookDialog detailEBookDialog = new DetailEBookDialog(eBook, index, eBookContinueReading);
        detailEBookDialog.show(getChildFragmentManager(), detailEBookDialog.getTag());
    }
}
