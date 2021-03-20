package com.mooc.libnetwork;

/**
 * 用于取出T的具体类型, 进行强制类型转换
 * @param <T>
 */
public abstract class JsonCallback<T> {
    public void onSuccess(ApiResponse<T> response) {

    }

    public void onError(ApiResponse<T> response) {

    }

    public void onCacheSuccess(ApiResponse<T> response) {

    }
}
