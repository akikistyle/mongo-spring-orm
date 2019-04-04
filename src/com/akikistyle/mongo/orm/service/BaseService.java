package com.akikistyle.mongo.orm.service;


import com.akikistyle.mongo.orm.bo.OrderSort;
import com.akikistyle.mongo.orm.bo.Pager;
import com.akikistyle.mongo.orm.dao.BaseDao;

import java.util.List;
import java.util.Map;

public interface BaseService {

    BaseDao getDao();

    <T> void insert(T obj);

    <T> void save(T obj);

    <T> List<T> list(Map<String, Object> obj, OrderSort order, Pager pager);

    <T> T getById(String id);

    <T> int count(Map<String, Object> obj);

    <T> void deleteById(String id);

    <T> void updateOne(Map<String, Object> param, Map<String, Object> result);

    <T> void updateMulti(Map<String, Object> param, Map<String, Object> result);
}
