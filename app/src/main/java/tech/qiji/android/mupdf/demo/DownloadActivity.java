package tech.qiji.android.mupdf.demo;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.tbruyelle.rxpermissions.RxPermissions;
import java.io.File;
import java.io.IOException;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import okio.BufferedSink;
import okio.Okio;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import tech.qiji.android.mupdf.MuPDFActivity;
import tech.qiji.android.mupdf.demo.download.DownloadProgressHandler;
import tech.qiji.android.mupdf.demo.download.ProgressHelper;
import tech.qiji.android.mupdf.demo.utilities.IOUtils;

public class DownloadActivity extends Activity {

  private CompositeSubscription mCompositeSubscription
      = new CompositeSubscription();

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_download);
    ButterKnife.bind(this);
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    ButterKnife.unbind(this);
    if (mCompositeSubscription != null && !mCompositeSubscription.isUnsubscribed()) {
      mCompositeSubscription.unsubscribe();
      mCompositeSubscription = null;
    }
  }

  @OnClick(R.id.start_download_btn) public void onClickButton() {
    // Must be done during an initialization phase like onCreate
    RxPermissions.getInstance(this)
        .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        .subscribe(new Action1<Boolean>() {
          @Override public void call(Boolean granted) {
            if (granted) {
              retrofitDownload();
            }
          }
        });
  }

  private void retrofitDownload() {
    //监听下载进度
    final ProgressDialog dialog = new ProgressDialog(this);
    dialog.setProgressNumberFormat("%1d KB/%2d KB");
    dialog.setTitle("下载");
    dialog.setMessage("正在下载，请稍后...");
    dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
    dialog.setCancelable(false);
    dialog.show();

    Retrofit.Builder retrofitBuilder =
        new Retrofit.Builder().addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .baseUrl("http://athena.huaxing.com");

    OkHttpClient.Builder builder = ProgressHelper.addProgress(null);
    builder.addInterceptor(new Interceptor() {
      @Override public okhttp3.Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        Headers.Builder builder = new Headers.Builder();
        builder.add("X-Jwt",
            "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VyX2lkIjoxNjY0LCJzeXN0ZW0iOiJhbmRyb2lkIn0.ZLAZdriW2nRhU6QDJacwLwqynK18ptE6Ztsc5Pzi1Ec");
        Request compressedRequest = originalRequest.newBuilder().headers(builder.build()).build();

        return chain.proceed(compressedRequest);
      }
    });
    DownloadApi retrofit =
        retrofitBuilder.client(builder.build()).build().create(DownloadApi.class);

    ProgressHelper.setProgressHandler(new DownloadProgressHandler() {
      @Override protected void onProgress(long bytesRead, long contentLength, boolean done) {
        dialog.setMax((int) (contentLength / 1024));
        dialog.setProgress((int) (bytesRead / 1024));
        if (done) {
          dialog.dismiss();
        }
      }
    });
    mCompositeSubscription.add(retrofit.download()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Action1<ResponseBody>() {
          @Override public void call(ResponseBody responseBody) {
            BufferedSink sink = null;
            try {
              File file = new File(Environment.getExternalStorageDirectory(), "111123456.pdf");
              sink = Okio.buffer(Okio.sink(file));
              sink.writeAll(responseBody.source());

              //  start a viewing activity
              Uri uri = Uri.parse(file.getAbsolutePath());
              Intent intent;
              intent = new Intent(DownloadActivity.this, MuPDFActivity.class);
              intent.setAction(Intent.ACTION_VIEW);
              intent.setData(uri);
              startActivity(intent);
              finish();
            } catch (IOException e) {
              e.printStackTrace();
            } finally {
              IOUtils.closeQuietly(sink);
            }
          }
        }));
  }


}
