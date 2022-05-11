package com.example.retrofitrxjava.dialog;

import android.content.Intent;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.ViewModelProvider;

import com.example.retrofitrxjava.ItemListener;
import com.example.retrofitrxjava.ItemOnclickProductListener;
import com.example.retrofitrxjava.R;
import com.example.retrofitrxjava.UserModel;
import com.example.retrofitrxjava.activity.ActivityAdd;
import com.example.retrofitrxjava.activity.DetailActivity;
import com.example.retrofitrxjava.activity.MainActivity;
import com.example.retrofitrxjava.adapter.MutilAdt;
import com.example.retrofitrxjava.databinding.BottomSheetBuyBinding;
import com.example.retrofitrxjava.databinding.BottomSheetSearchBinding;
import com.example.retrofitrxjava.model.ProductCategories;
import com.example.retrofitrxjava.viewmodel.SetupViewModel;

import java.util.ArrayList;
import java.util.List;

public class BottomSheetSearch extends BaseBottomSheet<BottomSheetSearchBinding> implements ItemOnclickProductListener<ProductCategories>,ItemListener<ProductCategories>, MutilAdt.ListItemListener {

    private List<ProductCategories> categories;
    public static final String EXTRA_DATA = "extra_data";
    private MutilAdt<ProductCategories> categoriesAdt;
    private SetupViewModel setupViewModel;

    public BottomSheetSearch(List<ProductCategories> list) {
        this.categories = list;
    }

    @Override
    protected int layoutId() {
        return R.layout.bottom_sheet_search;
    }

    @Override
    protected void initLayout() {
        setupViewModel = new ViewModelProvider(this).get(SetupViewModel.class);
        if (MainActivity.userModel.isAdmin()){
            categoriesAdt = new MutilAdt<>(getActivity(), R.layout.item_product_admin);
            categoriesAdt.setListener(this);

        }else {
            categoriesAdt = new MutilAdt<>(getActivity(), MainActivity.userModel.isAdmin() ? R.layout.item_product_admin : R.layout.item_product);
        }
        categoriesAdt.setDt((ArrayList<ProductCategories>) categories);
        binding.rclProduct.setAdapter(categoriesAdt);

        binding.search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                categoriesAdt.setDt((ArrayList<ProductCategories>) getListSearch(newText,categories));
                return true;
            }
        });
    }

    @Override
    public void onEditProduct(ProductCategories productCategories, int position) {
        Intent intent = new Intent(getActivity(), ActivityAdd.class);
        intent.putExtra(EXTRA_DATA, productCategories);
        intent.putExtra("edit", true);
        startActivity(intent);
    }

    @Override
    public void onDeleteProduct(ProductCategories productCategories, int position) {
        Toast.makeText(getActivity(), "onDeleteProduct" + productCategories.getDocumentId(), Toast.LENGTH_SHORT).show();
        setupViewModel.deleteProduct(getActivity(), db, productCategories, categories);
    }

    @Override
    public void onClickProduct(ProductCategories productCategories) {
        Intent intent = new Intent(getActivity(), DetailActivity.class);
        intent.putExtra(EXTRA_DATA, productCategories);
        startActivity(intent);
    }

    @Override
    public void onAddProduct(ProductCategories productCategories) {
        long quantity = Long.parseLong(productCategories.getQuantity()) + 1;
        productCategories.setQuantity(String.valueOf(quantity));
        db.collection("product_markets").document(productCategories.getDocumentId()).set(productCategories);
        Toast.makeText(getActivity(), "Thêm số lượng thành công", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemProductClick(ProductCategories productCategories) {
        Intent intent = new Intent(getActivity(), DetailActivity.class);
        intent.putExtra(EXTRA_DATA, productCategories);
//        intent.putExtra("idUser", userModel.getIdUser());
        startActivity(intent);
    }

    @Override
    public void onItemAddListener(ProductCategories productCategories) {

    }
}
