package com.akikistyle.mongo.orm.dao;


import com.akikistyle.mongo.orm.bo.OrderSort;
import com.akikistyle.mongo.orm.bo.Pager;

import java.util.List;
import java.util.Map;

public interface BaseDao {

    Class getObjectClass();

    <T> void insert(T obj);

    <T> void save(T obj);

    <T> List<T> list(Map<String, Object> param, OrderSort order, Pager pager);

    long count(Map<String, Object> param);

    <T> T getOneById(String id);

    <T> T getOne(Map<String, Object> param);

    <T> void deleteById(String id);

    <T> void updateFirst(Map<String, Object> param, Map<String, Object> result);

    <T> void updateMulti(Map<String, Object> param, Map<String, Object> result);
}
