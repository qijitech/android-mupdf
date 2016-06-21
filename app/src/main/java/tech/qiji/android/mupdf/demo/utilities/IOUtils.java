package tech.qiji.android.mupdf.demo.utilities;

import java.io.Closeable;
import java.io.IOException;

public final class IOUtils {

  private IOUtils() {

  }

  public static void closeQuietly(Closeable closeable) {
    try {
      if (closeable != null) {
        closeable.close();
      }
    } catch (IOException e) {
    }
  }
}
