package com.fincords.service;

import com.fincords.dto.respone.ApiResponse;

public interface BaseInterface {
    public ApiResponse<?> create(Object request);

    public ApiResponse<?> read(Object request);

    public ApiResponse<?> readAll(Object request);

    public ApiResponse<?> update(Object request);

    public ApiResponse<?> delete(Object request);
}
