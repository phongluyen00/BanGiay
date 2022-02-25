package com.example.retrofitrxjava.dialog;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.retrofitrxjava.R;
import com.example.retrofitrxjava.databinding.DialogPdfViewerBinding;
import com.github.barteksc.pdfviewer.listener.OnErrorListener;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class DialogPDFViewer extends BDialogFragment<DialogPdfViewerBinding> {

    private String title;
    private String urlPDF;

    public DialogPDFViewer(String title, String urlPDF) {
        this.title = title;
        this.urlPDF = urlPDF;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_pdf_viewer;
    }

    @Override
    protected void initLayout() {
        binding.title.setText(title);
        binding.progressCircular.setVisibility(View.VISIBLE);
        binding.title.setOnClickListener(v -> dismiss());
        new loadPdfURLViewer().execute(urlPDF);
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
            binding.idPDFView.
                    fromStream(inputStream) .onLoad(this).onError(this).load();
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


}
