package com.epam.aidial.core.controller;

import com.epam.aidial.core.Proxy;
import com.epam.aidial.core.ProxyContext;
import com.epam.aidial.core.data.FileMetadataBase;
import com.epam.aidial.core.storage.BlobStorage;
import com.epam.aidial.core.storage.BlobStorageUtil;
import com.epam.aidial.core.util.HttpStatus;
import io.vertx.core.Future;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@AllArgsConstructor
@Slf4j
public class FileMetadataController {
    private final Proxy proxy;
    private final ProxyContext context;

    /**
     * Lists all files and folders that belong to the provided path.
     * Current API implementation requires a relative path, absolute path will be calculated based on authentication context
     *
     * @param path relative path, for example: /inputs
     */
    public Future<?> list(String path) {
        BlobStorage storage = proxy.getStorage();
        return proxy.getVertx().executeBlocking(() -> {
            try {
                String absolutePath = BlobStorageUtil.buildAbsoluteFilePath(context, path);
                List<FileMetadataBase> metadata = storage.listMetadata(absolutePath);
                context.respond(HttpStatus.OK, metadata);
            } catch (Exception ex) {
                log.error("Failed to list files", ex);
                context.respond(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to list files by path %s".formatted(path));
            }

            return null;
        });
    }
}