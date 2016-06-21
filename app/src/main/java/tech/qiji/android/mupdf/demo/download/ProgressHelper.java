package tech.qiji.android.mupdf.demo.download;

import android.util.Log;
import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;

public class ProgressHelper {
  private static Download download = new Download();
  private static ProgressHandler mProgressHandler;

  public static OkHttpClient.Builder addProgress(OkHttpClient.Builder builder) {
    if (builder == null) {
      builder = new OkHttpClient.Builder();
    }
    final Progress.ProgressListener progressListener = new Progress.ProgressListener() {
      //该方法在子线程中运行
      @Override public void update(long progress, long total, boolean done) {
        Log.d("progress:", String.format("%d%% done\n", (100 * progress) / total));
        if (mProgressHandler == null) {
          return;
        }
        download.setBytesRead(progress);
        download.setContentLength(total);
        download.setDone(done);
        mProgressHandler.sendMessage(download);
      }
    };
    //添加拦截器，自定义ResponseBody，添加下载进度
    builder.networkInterceptors().add(new Interceptor() {
      @Override public okhttp3.Response intercept(Chain chain) throws IOException {
        okhttp3.Response originalResponse = chain.proceed(chain.request());
        return originalResponse.newBuilder()
            .body(new Progress.ProgressResponseBody(originalResponse.body(), progressListener))
            .build();
      }
    });

    return builder;
  }

  public static void setProgressHandler(ProgressHandler progressHandler) {
    mProgressHandler = progressHandler;
  }
}
