package io.illusionbank.sec.antifraud.gateway.io;

import io.vertx.core.buffer.Buffer;
import lombok.Getter;
import org.apache.commons.io.FilenameUtils;

@Getter
public class FileEntry {
    private String name;
    private String fullPath;
    private Buffer content;

    public FileEntry(Buffer content, String fullPath) {
        this.content = content;
        this.fullPath = fullPath;
        this.name = FilenameUtils.getName(fullPath);
    }
}
