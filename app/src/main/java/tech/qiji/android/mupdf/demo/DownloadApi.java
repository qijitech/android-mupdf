package tech.qiji.android.mupdf.demo;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import rx.Observable;

/**
 * Created by ljd on 3/29/16.
 */
public interface DownloadApi {

  @GET("/api/documents/5.pdf") Call<ResponseBody> retrofitDownload();

  @GET("/api/documents/5.pdf") Observable<ResponseBody> download();
}
