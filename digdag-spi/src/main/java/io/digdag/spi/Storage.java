package io.digdag.spi;

import java.io.InputStream;
import java.io.IOException;
import java.util.List;
import com.google.common.base.Optional;

public interface Storage
{
    StorageObject open(String key)
        throws StorageFileNotFoundException;

    interface UploadStreamProvider
    {
        InputStream open() throws IOException;
    }

    String put(String key, long contentLength,
            UploadStreamProvider payload)
        throws IOException;

    default String put(String key, long contentLength,
            final InputStream in)
        throws IOException
    {
        return put(key, contentLength, new UploadStreamProvider() {
            private boolean done = false;

            @Override
            public InputStream open() throws IOException
            {
                if (done) {
                    throw new IllegalStateException("Already opened");
                }
                done = true;
                return in;
            }
        });
    }

    interface FileListing
    {
        void accept(List<StorageObjectSummary> chunk);
    }

    void list(String keyPrefix, FileListing callback);

    default Optional<DirectDownloadHandle> getDirectDownloadHandle(String key)
    {
        return Optional.absent();
    }

    default Optional<DirectUploadHandle> getDirectUploadHandle(String key)
    {
        return Optional.absent();
    }
}
