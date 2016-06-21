package tech.qiji.android.mupdf.demo.download;

import android.os.Looper;
import android.os.Message;

public abstract class UploadProgressHandler extends ProgressHandler {

  private static final int UPLOAD_PROGRESS = 0;
  protected ResponseHandler mHandler = new ResponseHandler(this, Looper.getMainLooper());

  @Override protected void sendMessage(Download download) {
    mHandler.obtainMessage(UPLOAD_PROGRESS, download).sendToTarget();
  }

  @Override protected void handleMessage(Message message) {
    switch (message.what) {
      case UPLOAD_PROGRESS:
        Download download = (Download) message.obj;
        onProgress(download.getBytesRead(), download.getContentLength(), download.isDone());
    }
  }
}
