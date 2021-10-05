package com.rusned.laba1.util;

import java.io.File;
import java.io.FilenameFilter;

public class AppFileNameFilter implements FilenameFilter {

    private final String extension;

    public AppFileNameFilter(String extension){
        this.extension = extension;
    }

    @Override
    public boolean accept(File dir, String name) {
        return name.toLowerCase().endsWith(extension);
    }
}
