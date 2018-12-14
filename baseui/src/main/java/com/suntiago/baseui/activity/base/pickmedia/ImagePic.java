package com.suntiago.baseui.activity.base.pickmedia;

import android.net.Uri;

import java.io.Serializable;

/**
 * Created by zy on 2018/12/14.
 */

public class ImagePic implements Serializable {
    private String originalPath;
    private String compressPath;
    private ImagePic.FromType fromType;
    private boolean cropped;
    private boolean compressed;
    public static ImagePic of(String path, ImagePic.FromType fromType){
        return new ImagePic(path, fromType);
    }
    public static ImagePic of(Uri uri, ImagePic.FromType fromType){
        return new ImagePic(uri, fromType);
    }
    private ImagePic(String path, ImagePic.FromType fromType) {
        this.originalPath = path;
        this.fromType = fromType;
    }
    private ImagePic(Uri uri, ImagePic.FromType fromType) {
        this.originalPath = uri.getPath();
        this.fromType = fromType;
    }

    public String getOriginalPath() {
        return originalPath;
    }

    public void setOriginalPath(String originalPath) {
        this.originalPath = originalPath;
    }

    public String getCompressPath() {
        return compressPath;
    }

    public void setCompressPath(String compressPath) {
        this.compressPath = compressPath;
    }

    public ImagePic.FromType getFromType() {
        return fromType;
    }

    public void setFromType(ImagePic.FromType fromType) {
        this.fromType = fromType;
    }

    public boolean isCropped() {
        return cropped;
    }

    public void setCropped(boolean cropped) {
        this.cropped = cropped;
    }

    public boolean isCompressed() {
        return compressed;
    }

    public void setCompressed(boolean compressed) {
        this.compressed = compressed;
    }

    public enum FromType {
        CAMERA, OTHER
    }
}
