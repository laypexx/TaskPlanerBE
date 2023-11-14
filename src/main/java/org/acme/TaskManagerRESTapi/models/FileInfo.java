package org.acme.TaskManagerRESTapi.models;

import lombok.Data;

@Data
public class FileInfo {
    private String filename;
    private Long fileLength;
    private String contentType;
    private Boolean isReadable;
    private Boolean isFileEmpty;
    private byte[] fileData;
}
