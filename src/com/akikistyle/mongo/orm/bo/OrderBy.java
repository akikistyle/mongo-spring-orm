package com.akikistyle.mongo.orm.bo;

public class OrderBy {
    private String orderName;
    private SortEnum orderSort;

    public OrderBy(String orderName, SortEnum orderSort) {
        this.orderName = orderName;
        this.orderSort = orderSort;
    }

    public void setOrderName(String orderName) {
        this.orderName = orderName;
    }

    public void setOrderSort(SortEnum orderSort) {
        this.orderSort = orderSort;
    }

    public String getOrderName() {
        return orderName;
    }

    public SortEnum getOrderSort() {
        return orderSort;
    }

    @Override
    public String toString() {
        String orderStr = "";
        orderStr += orderName + " " + orderSort;
        return orderStr.length() < 1 ? "" : orderStr;
    }
}
