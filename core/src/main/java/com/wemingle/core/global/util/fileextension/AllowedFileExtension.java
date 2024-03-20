package com.wemingle.core.global.util.fileextension;

import java.util.List;

public interface AllowedFileExtension {
    List<String> allowedFileExtensions = List.of(
            "jpg",
            "jpeg",
            "png",
            "bmp",
            "tiff",
            "tif",
            "heif",
            "webp",
            "svg",
            "xcf",
            "raw"
    );
}
