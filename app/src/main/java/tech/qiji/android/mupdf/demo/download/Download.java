package tech.qiji.android.mupdf.demo.download;

public class Download {

  private long bytesRead;
  private long contentLength;
  private boolean done;

  public long getBytesRead() {
    return bytesRead;
  }

  public void setBytesRead(long bytesRead) {
    this.bytesRead = bytesRead;
  }

  public long getContentLength() {
    return contentLength;
  }

  public void setContentLength(long contentLength) {
    this.contentLength = contentLength;
  }

  public boolean isDone() {
    return done;
  }

  public void setDone(boolean done) {
    this.done = done;
  }
}
