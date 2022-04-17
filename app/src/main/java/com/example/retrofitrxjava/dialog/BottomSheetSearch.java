package com.example.retrofitrxjava.dialog;

import androidx.appcompat.widget.SearchView;

import com.example.retrofitrxjava.ItemListener;
import com.example.retrofitrxjava.R;
import com.example.retrofitrxjava.UserModel;
import com.example.retrofitrxjava.activity.MainActivity;
import com.example.retrofitrxjava.adapter.MutilAdt;
import com.example.retrofitrxjava.databinding.BottomSheetBuyBinding;
import com.example.retrofitrxjava.databinding.BottomSheetSearchBinding;
import com.example.retrofitrxjava.model.ProductCategories;

import java.util.ArrayList;
import java.util.List;

public class BottomSheetSearch extends BaseBottomSheet<BottomSheetSearchBinding> implements ItemListener<ProductCategories>, MutilAdt.ListItemListener {

    private List<ProductCategories> categories;
    private MutilAdt<ProductCategories> categoriesAdt;

    public BottomSheetSearch(List<ProductCategories> list) {
        this.categories = list;
    }

    @Override
    protected int layoutId() {
        return R.layout.bottom_sheet_search;
    }

    @Override
    protected void initLayout() {
        categoriesAdt = new MutilAdt<>(getActivity(), MainActivity.userModel.isAdmin() ? R.layout.item_product_admin : R.layout.item_product);
        categoriesAdt.setListener(this);
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

    }

    @Override
    public void onDeleteProduct(ProductCategories productCategories, int position) {

    }

    @Override
    public void onClickProduct(ProductCategories productCategories) {

    }

    @Override
    public void onAddProduct(ProductCategories productCategories) {

    }
}
