package com.wemingle.core.global.util.fileextension;

import com.wemingle.core.global.exception.UnsupportedFileExtensionException;

import static com.wemingle.core.global.exceptionmessage.ExceptionMessage.UNSUPPORTED_EXTENSION;

public class FileExtensionFilter implements AllowedFileExtension{
    public void isAvailableFileExtension(String fileExtension){
        if (isNotSupportedFileExtension(fileExtension)){
            throw new UnsupportedFileExtensionException(UNSUPPORTED_EXTENSION.getExceptionMessage());
        }
    }

    private boolean isNotSupportedFileExtension(String fileExtension) {
        String fileExtensionLowerCase = fileExtension.toLowerCase();
        return allowedFileExtensions.stream().noneMatch(allowedFileExtension -> allowedFileExtension.equals(fileExtensionLowerCase));
    }
}
