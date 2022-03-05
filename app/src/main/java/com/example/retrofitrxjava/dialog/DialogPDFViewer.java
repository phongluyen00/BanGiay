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
        binding.title.setOnClickListener(v -> {
            dismiss();
        });
        new loadPdfURLViewer().execute(eBook.getFile_pdf());

        binding.save.setOnClickListener(v -> {
            eBook.setUid(currentUser.getUid());
            if (eBookSave != null) {
                db.collection("continue_reading").document(eBookSave.getDocumentId()).set(eBook);
            } else {
                db.collection("continue_reading").document().set(eBook);
            }
            EventBus.getDefault().post(new MessageEvent());
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
                eBook.setPage(page);
                pagegg = page + 1;
                pageCountttt = pageCount;

                float percent = (pagegg / pageCountttt) * 100;
                Log.d("AAAAAAAAAAAA", percent+ " percent");
                int b = (int) percent + 1;
                eBook.setPercent(b);

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
