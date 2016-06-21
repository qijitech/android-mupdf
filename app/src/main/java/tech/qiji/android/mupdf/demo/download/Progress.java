/*
 * Copyright (C) 2015 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tech.qiji.android.mupdf.demo.download;

import java.io.IOException;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.ForwardingSink;
import okio.ForwardingSource;
import okio.Okio;
import okio.Sink;
import okio.Source;

public interface Progress {

  class ProgressResponseBody extends ResponseBody {
    private final ResponseBody responseBody;
    private final ProgressListener progressListener;
    private BufferedSource bufferedSource;

    public ProgressResponseBody(ResponseBody responseBody, ProgressListener progressListener) {
      this.responseBody = responseBody;
      this.progressListener = progressListener;
    }

    @Override public MediaType contentType() {
      return responseBody.contentType();
    }

    @Override public long contentLength() {
      return responseBody.contentLength();
    }

    @Override public BufferedSource source() {
      if (bufferedSource == null) {
        bufferedSource = Okio.buffer(source(responseBody.source()));
      }
      return bufferedSource;
    }

    private Source source(Source source) {
      return new ForwardingSource(source) {
        long totalBytesRead = 0L;

        @Override public long read(Buffer sink, long byteCount) throws IOException {
          long bytesRead = super.read(sink, byteCount);
          // read() returns the number of bytes read, or -1 if this source is exhausted.
          totalBytesRead += bytesRead != -1 ? bytesRead : 0;
          progressListener.update(totalBytesRead, responseBody.contentLength(), bytesRead == -1);
          return bytesRead;
        }
      };
    }
  }

  class ProgressRequestBody extends RequestBody {
    private final RequestBody requestBody;
    private final ProgressListener progressListener;
    private BufferedSink bufferedSink;

    public ProgressRequestBody(RequestBody requestBody, ProgressListener progressListener) {
      this.requestBody = requestBody;
      this.progressListener = progressListener;
    }

    @Override public MediaType contentType() {
      return requestBody.contentType();
    }

    @Override public long contentLength() throws IOException {
      return requestBody.contentLength();
    }

    @Override public void writeTo(BufferedSink sink) throws IOException {
      if (bufferedSink == null) {
        bufferedSink = Okio.buffer(sink(sink));
      }
      requestBody.writeTo(bufferedSink);
      bufferedSink.flush();
    }

    private Sink sink(Sink sink) {
      return new ForwardingSink(sink) {
        long bytesWritten = 0L;
        long contentLength = 0L;

        @Override public void write(Buffer source, long byteCount) throws IOException {
          super.write(source, byteCount);
          if (contentLength == 0) {
            contentLength = contentLength();
          }
          bytesWritten += byteCount;
          progressListener.update(bytesWritten, contentLength, bytesWritten == contentLength);
        }
      };
    }
  }

  interface ProgressListener {
    void update(long bytesRead, long contentLength, boolean done);
  }
}