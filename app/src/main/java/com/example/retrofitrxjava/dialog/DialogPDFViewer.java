package com.example.retrofitrxjava.dialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.retrofitrxjava.R;
import com.example.retrofitrxjava.activity.MainActivity;
import com.example.retrofitrxjava.adapter.MutilAdt;
import com.example.retrofitrxjava.databinding.DialogPdfViewerBinding;
import com.example.retrofitrxjava.model.EBook;
import com.example.retrofitrxjava.model.MessageEvent;
import com.github.barteksc.pdfviewer.listener.OnErrorListener;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.MetadataChanges;

import org.greenrobot.eventbus.EventBus;
import org.jsoup.helper.StringUtil;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class DialogPDFViewer extends BDialogFragment<DialogPdfViewerBinding> {

    private EBook eBook;
    private EBook eBookSave;

    public DialogPDFViewer(EBook eBook, EBook eBookSave) {
        this.eBook = eBook;
        this.eBookSave = eBookSave;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_pdf_viewer;
    }

    @Override
    protected void initLayout() {
        binding.title.setText(eBook.getTitle());
        binding.progressCircular.setVisibility(View.VISIBLE);

        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();

            }
        });

        new loadPdfURLViewer().execute(eBook.getFile_pdf());

        binding.save.setOnClickListener(v -> {
            if (binding.progressCircular.getVisibility() == View.VISIBLE){
                Snackbar snackbar = Snackbar
                        .make(binding.main, "File download in progress. Please wait !", Snackbar.LENGTH_LONG);
                snackbar.show();
                return;
            }
            eBook.setUid(currentUser.getUid());
            eBook.setId_book(eBook.getDocumentId());
            if (binding.save.getText().equals("Đọc xong")) {
                db.collection("continue_reading").document(eBookSave.getDocumentId()).delete()
                        .addOnSuccessListener(aVoid -> {
                        })
                        .addOnFailureListener(e -> {
                        });
            }else {
                if (eBookSave != null) {
                    eBookSave.setUid(currentUser.getUid());
                    db.collection("continue_reading").document(eBookSave.getDocumentId()).set(eBook);
                } else {
                    db.collection("continue_reading").document().set(eBook);
                }
            }

            EventBus.getDefault().post(new MessageEvent());
            Snackbar snackbar = Snackbar
                    .make(binding.main, "Added read later", Snackbar.LENGTH_LONG);
            snackbar.show();
            dismiss();
        });

    }

    @SuppressLint("StaticFieldLeak")
    class loadPdfURLViewer extends AsyncTask<String, Void, InputStream> implements OnLoadCompleteListener, OnErrorListener {
        @Override
        protected InputStream doInBackground(String... strings) {
            //We use InputStream to get PDF.
            InputStream inputStream = null;
            try {
                URL url = new URL(strings[0]);
                HttpURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                if (urlConnection.getResponseCode() == 200) {
                    // if response is success. we are getting input stream from url and storing it in our variable.
                    inputStream = new BufferedInputStream(urlConnection.getInputStream());
                }

            } catch (IOException e) {
                //method to handle errors.
                e.printStackTrace();
                return null;
            }
            return inputStream;
        }

        @Override
        protected void onPostExecute(InputStream inputStream) {
            //after the executing async task we load pdf in to pdfview.
            binding.idPDFView.fromStream(inputStream).defaultPage(eBookSave != null ? eBookSave.getPage() : eBook.getPage()).onPageChange((page, pageCount) -> {
                if (StringUtil.isBlank(eBook.getTotal_page())){
                    eBook.setTotal_page(String.valueOf(pageCount));
                    db.collection("db_comics").document(eBook.getDocumentId()).set(eBook);
                }
                eBook.setPage(page);
                pagegg = page + 1;

                pageCountttt = pageCount;

                float percent = (pagegg / pageCountttt) * 100;
                Log.d("AAAAAAAAAAAA", percent + " percent");
                int b = (int) percent + 1;
                eBook.setPercent(b);
                if (b >= 100) {
                    binding.save.setText("Đọc xong");
                }else {
                    binding.save.setText("Đọc sau");
                }
            }).onLoad(this).onError(this).load();
        }

        @Override
        public void loadComplete(int nbPages) {
            binding.progressCircular.setVisibility(View.GONE);
        }

        @Override
        public void onError(Throwable t) {
            binding.progressCircular.setVisibility(View.GONE);
            Toast.makeText(getActivity(), "Error:" + t.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    float pagegg, pageCountttt;

}
