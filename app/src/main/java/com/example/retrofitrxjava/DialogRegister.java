package com.example.retrofitrxjava;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.LayoutRes;
import androidx.databinding.DataBindingUtil;

import com.example.retrofitrxjava.databinding.DialogRegisterBinding;

public class DialogRegister {
    private AlertDialog mDialog;
    private Context context;
    private AlertDialog.Builder mDialogBuilder;
    private DialogRegisterBinding binding;
    private RegisterListener listener;
    private UserModel userModel;


    public DialogRegister(Context context, RegisterListener listener) {
        this.context = context;
        this.mDialogBuilder = new AlertDialog.Builder(context);
        this.listener = listener;
        binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.dialog_register, null, false);
    }

    public void show(boolean isCancelable) {
        this.userModel = new UserModel();
        binding.setUser(this.userModel);
        mDialogBuilder.setView(binding.getRoot());
        if (mDialog == null) {
            mDialog = mDialogBuilder.create();
        }
        mDialog.setCanceledOnTouchOutside(isCancelable);
        mDialog.setCancelable(isCancelable);

        Activity activity = null;
        try {
            activity = ((Activity) context);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (activity != null && activity.isFinishing()) {
            return;
        }

        try {
            mDialog.show();
        } catch (WindowManager.BadTokenException e) {
            //use a log message
        }

        binding.tvRegister.setOnClickListener(v -> {
            if (listener != null) {
                userModel = binding.getUser();
                userModel.setPermission(1);
                listener.registerSuccess(userModel);
            }
        });

        binding.tvCancel.setOnClickListener(v -> dismiss());

    }

    public void dismiss() {
        if (mDialog != null) {
            mDialog.dismiss();
        }
    }

    public interface RegisterListener {
        void registerSuccess(UserModel userModel);
    }

}
