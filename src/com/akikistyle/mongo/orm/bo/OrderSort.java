package com.akikistyle.mongo.orm.bo;

import java.util.ArrayList;
import java.util.List;

public class OrderSort {
    private List<OrderBy> orders = new ArrayList<OrderBy>();

    public List<OrderBy> getOrders() {
        return orders;
    }

    public void setOrders(List<OrderBy> orders) {
        this.orders = orders;
    }

    public void addOrder(String orderName, SortEnum orderSort) {
        orders.add(new OrderBy(orderName, orderSort));
    }

    public void addIndexOrder(int index, String orderName, SortEnum orderSort) {
        int c = orders.size();
        if (index < 0 || (index != 0 && index > c - 1))
            return;
        orders.add(index, new OrderBy(orderName, orderSort));
    }

    @Override
    public String toString() {
        if (orders != null && orders.size() > 0) {
            String res = "[";
            for (OrderBy order : orders) {
                res += order.toString() + " ";
            }
            res += "]";
            return res;
        }
        return "[]";
    }
}
