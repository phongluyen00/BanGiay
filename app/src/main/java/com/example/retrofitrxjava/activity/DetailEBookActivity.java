package com.example.retrofitrxjava.activity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Toast;

import com.example.retrofitrxjava.R;
import com.example.retrofitrxjava.databinding.DetailActivityBinding;
import com.example.retrofitrxjava.dialog.DialogPDFViewer;
import com.example.retrofitrxjava.model.EBook;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class DetailEBookActivity extends AppCompatAct<DetailActivityBinding> {

    private EBook eBook;

    @Override
    protected void initLayout() {
        if (getIntent() != null) {
            eBook = (EBook) getIntent().getSerializableExtra("ebook");
            bd.setItem(eBook);
        }

        bd.read.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogPDFViewer dialogPDFViewer = new DialogPDFViewer(eBook.getTitle(), eBook.getFile_pdf());
                dialogPDFViewer.show(getSupportFragmentManager(),dialogPDFViewer.getTag());
            }
        });
    }

//    @SuppressLint("StaticFieldLeak")
//    class RetrievePdf extends AsyncTask<String, Void, InputStream> {
//        @Override
//        protected InputStream doInBackground(String... strings) {
//            InputStream inputStream = null;
//            try {
//                URL url = new URL(strings[0]);
//                HttpURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
//                if (urlConnection.getResponseCode() == 200) {
//                    inputStream = new BufferedInputStream(urlConnection.getInputStream());
//                }
//
//            } catch (IOException e) {
//                e.printStackTrace();
//                return null;
//            }
//            return inputStream;
//        }
//
//        @Override
//        protected void onPostExecute(InputStream inputStream) {
//            // after the execution of our async
//            // task we are loading our pdf in our pdf view.
//            bd.idPDFView.fromStream(inputStream)
//                    .password(null)
//                    .defaultPage(0)
//                    .enableSwipe(true)
//                    .swipeHorizontal(false)
//                    .enableDoubletap(true)
//                    .onDraw((canvas, pageWidth, pageHeight, displayedPage) -> {
//
//                    })
//                    .onDrawAll((canvas, pageWidth, pageHeight, displayedPage) -> {
//
//                    })
//                    .onPageError((page, t) -> {
//                        Toast.makeText(DetailEBookActivity.this, "Có lỗi xảy ra !", Toast.LENGTH_SHORT).show();
//                    })
//                    .onPageChange((page, pageCount) -> {
//
//                    })
//                    .onTap(e -> true)
//                    .onRender((nbPages, pageWidth, pageHeight) -> bd.idPDFView.fitToWidth())
//                    .enableAnnotationRendering(true)
//                    .invalidPageColor(Color.WHITE)
//                    .load();
//        }
//    }

    @Override
    protected int getID() {
        return R.layout.detail_activity;
    }
}
