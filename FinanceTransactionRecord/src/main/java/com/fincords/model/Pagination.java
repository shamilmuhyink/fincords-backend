package com.fincords.model;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Pagination {
    private int page;
    private int size;
    private int offset;
    private int limit;
}
