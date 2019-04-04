package com.akikistyle.mongo.orm.bo;

import org.springframework.data.domain.Sort.Direction;

public enum SortEnum {
    ASC,
    DESC;

    private SortEnum() {
    }

    public Direction toValue() {
        if (this.equals(DESC)) {
            return Direction.DESC;
        }
        return Direction.ASC;
    }
}
