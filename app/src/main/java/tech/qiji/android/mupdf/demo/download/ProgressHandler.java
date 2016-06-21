package tech.qiji.android.mupdf.demo.download;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

public abstract class ProgressHandler {
  protected abstract void sendMessage(Download download);

  protected abstract void handleMessage(Message message);

  protected abstract void onProgress(long progress, long total, boolean done);

  protected static class ResponseHandler extends Handler {
    private ProgressHandler mProgressHandler;

    public ResponseHandler(ProgressHandler mProgressHandler, Looper looper) {
      super(looper);
      this.mProgressHandler = mProgressHandler;
    }

    @Override public void handleMessage(Message msg) {
      mProgressHandler.handleMessage(msg);
    }
  }
}
