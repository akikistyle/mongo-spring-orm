package com.akikistyle.mongo.orm.service;


import com.akikistyle.mongo.orm.bo.OrderSort;
import com.akikistyle.mongo.orm.bo.Pager;
import com.akikistyle.mongo.orm.dao.BaseDao;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public abstract class AbstractBaseServiceImpl implements BaseService {

    @Override
    public abstract BaseDao getDao();

    @Override
    public <T> void insert(T obj) {
        getDao().insert(obj);
    }

    @Override
    public <T> void save(T obj) {
        getDao().save(obj);
    }

    @Override
    public <T> T getById(String id) {
        return (T) getDao().getOneById(id);
    }

    @Override
    public <T> List<T> list(Map<String, Object> obj, OrderSort order, Pager pager) {
        if (pager != null) {
            int count = new BigDecimal(getDao().count(obj)).intValue();
            pager.setRecordCount(count);
        }
        return (List<T>) getDao().list(obj, order, pager);
    }

    @Override
    public <T> int count(Map<String, Object> obj) {
        return new BigDecimal(getDao().count(obj)).intValue();
    }

    @Override
    public <T> void deleteById(String id) {
        getDao().deleteById(id);
    }

    @Override
    public <T> void updateOne(Map<String, Object> param, Map<String, Object> result) {
        getDao().updateFirst(param, result);
    }

    @Override
    public <T> void updateMulti(Map<String, Object> param, Map<String, Object> result) {
        getDao().updateMulti(param, result);
    }
}
