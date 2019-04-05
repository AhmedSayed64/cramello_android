package net.aldar.cramello.services;

import android.util.Log;
import android.widget.ImageView;

import net.aldar.cramello.apiHandler.BaseApi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static net.aldar.cramello.App.mAppDirFile;

public class DownloadFile {

    private static String TAG = "DOWNLOAD FILE";

    public static void downloadImage(BaseApi serviceApi, final PrefsManger prefsManger, String fileLink,
                                     final String fileName, final ImageView targetImage, final int placeHolder) {

        Call<ResponseBody> call = serviceApi.downloadFile(fileLink);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                Log.e("Response", response.code() + "");

                if (response.isSuccessful()) {
                    Log.e(TAG, "server contacted and has file");

                    boolean writtenToDisk = writeResponseBodyToDisk(prefsManger, response.body(),
                            fileName, targetImage, placeHolder);

                    Log.e(TAG, "file download was a success? " + writtenToDisk);
                } else {
                    Log.e(TAG, "server contact failed");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("onFailure", t.getMessage() + "");
            }
        });
    }

    private static boolean writeResponseBodyToDisk(PrefsManger prefsManger, ResponseBody body, String name,
                                                   ImageView targetImage, int placeHolder) {
        try {
            File myfile = new File(mAppDirFile, name);

            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[4096];

                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(String.valueOf(myfile));

                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }

                    outputStream.write(fileReader, 0, read);

                    fileSizeDownloaded += read;

                    Log.e(TAG, "file download: " + fileSizeDownloaded + " of " + fileSize);
                }

                Utils.loadImage(null, myfile, targetImage, placeHolder);
                prefsManger.setAdImage(name);
                outputStream.flush();

                return true;
            } catch (IOException e) {
                Log.e("Exc : ", e.getMessage() + "");
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return false;
        }
    }
}
