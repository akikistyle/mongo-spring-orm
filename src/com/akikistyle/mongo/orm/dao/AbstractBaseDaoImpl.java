package com.akikistyle.mongo.orm.dao;


import com.akikistyle.mongo.orm.bo.Operators;
import com.akikistyle.mongo.orm.bo.OrderBy;
import com.akikistyle.mongo.orm.bo.OrderSort;
import com.akikistyle.mongo.orm.bo.Pager;
import com.akikistyle.mongo.orm.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractBaseDaoImpl implements BaseDao {

    @Override
    public abstract Class getObjectClass();

    @Autowired
    protected MongoTemplate mongoTemplate;

    @Override
    public <T> void insert(T obj) {
        mongoTemplate.insert(obj);
    }

    @Override
    public <T> void save(T obj) {
        mongoTemplate.save(obj);
    }

    @Override
    public <T> List<T> list(Map<String, Object> param, OrderSort orderSort, Pager pager) {
        Query query = new Query();

        if (orderSort != null) {
            List<Order> orderList = new ArrayList<Order>();
            for (OrderBy ob : orderSort.getOrders()) {
                Order order = new Order(ob.getOrderSort().toValue(), ob.getOrderName());
                orderList.add(order);
            }
            Sort sort = new Sort(orderList);
            query.with(sort);
        }

        if (pager != null) {
            query.skip(pager.getStart()).limit(pager.getPageSize());
        }

        Criteria criteria = combineParam(param);

        query.addCriteria(criteria);

        return (List<T>) mongoTemplate.find(query, getObjectClass());
    }

    @Override
    public long count(Map<String, Object> param) {
        Query query = new Query();
        Criteria criteria = combineParam(param);

        query.addCriteria(criteria);

        return mongoTemplate.count(query, getObjectClass());
    }

    @Override
    public <T> T getOneById(String id) {
        Query query = new Query();

        Criteria criteria = Criteria.where("_id").is(id);

        query.addCriteria(criteria);

        return (T) mongoTemplate.findOne(query, getObjectClass());
    }

    @Override
    public <T> T getOne(Map<String, Object> param) {
        Query query = new Query();
        Criteria criteria = combineParam(param);

        query.addCriteria(criteria);

        return (T) mongoTemplate.findOne(query, getObjectClass());
    }

    @Override
    public <T> void deleteById(String id) {
        Query query = new Query();

        Criteria criteria = Criteria.where("_id").is(id);

        query.addCriteria(criteria);

        mongoTemplate.remove(query, getObjectClass());
    }

    @Override
    public <T> void updateFirst(Map<String, Object> param, Map<String, Object> result) {
        Query query = new Query();
        Criteria criteria = combineParam(param);
        query.addCriteria(criteria);
        mongoTemplate.updateFirst(query, combineUpdateSet(result), getObjectClass());
    }

    @Override
    public <T> void updateMulti(Map<String, Object> param, Map<String, Object> result) {
        Query query = new Query();
        Criteria criteria = combineParam(param);
        query.addCriteria(criteria);
        mongoTemplate.updateMulti(query, combineUpdateSet(result), getObjectClass());
    }

    protected Criteria combineParam(Map<String, Object> paramMap) {
        Integer index = 0;
        Criteria criteria = new Criteria();
        if (paramMap != null) {
            for (Map.Entry<String, Object> entry : paramMap.entrySet()) {
                if (entry.getKey().indexOf(Operators.OR) != -1) {// 或
                    String fieldName = entry.getKey().substring(0, entry.getKey().indexOf(Operators.OR));
                    List<Criteria> criterias = new ArrayList<Criteria>();
                    List<Object> orList = (List<Object>) entry.getValue();
                    for (Object orValue : orList) {
                        criterias.add(Criteria.where(fieldName).is(orValue));
                    }
                    Criteria[] tmpArr = new Criteria[criterias.size()];
                    criteria = criteria.orOperator(criterias.toArray(tmpArr));
                } else if (entry.getKey().indexOf(Operators.IN) != -1) {// 包括
                    String fieldName = entry.getKey().substring(0, entry.getKey().indexOf(Operators.IN));
                    List<Object> rangeList = (List<Object>) entry.getValue();
                    criteria = index == 0 ? criteria.where(fieldName).in(rangeList) : criteria.and(fieldName).in(rangeList);
                } else if (entry.getKey().indexOf(Operators.OR_SEPARATOR) != -1) {// 或分隔符
                    String[] querys = entry.getKey().split("\\|\\|");
                    List<Criteria> criterias = new ArrayList<Criteria>();
                    for (String query : querys) {
                        Map<String, Object> subQueryMap = new HashMap<String, Object>();
                        subQueryMap.put(query, entry.getValue());
                        criterias.add(combineParam(subQueryMap));
                    }
                    Criteria[] tmpArr = new Criteria[criterias.size()];
                    criteria = criteria.orOperator(criterias.toArray(tmpArr));
                } else if (entry.getKey().indexOf(Operators.GTE) != -1) {// 大于等于
                    String fieldName = entry.getKey().substring(0, entry.getKey().indexOf(Operators.GTE));
                    if (paramMap.containsKey(StringUtil.concat(fieldName, Operators.LTE))) {
                        // 当存在同字段小于等于时组合
                        criteria = index == 0 ? criteria.where(fieldName).gte(entry.getValue())
                                .lte(paramMap.get(StringUtil.concat(fieldName, Operators.LTE))) : criteria.and(fieldName)
                                .gte(entry.getValue()).lte(paramMap.get(StringUtil.concat(fieldName, Operators.LTE)));
                    } else {
                        criteria = index == 0 ? criteria.where(fieldName).gte(entry.getValue()) : criteria.and(fieldName).gte(entry.getValue());
                    }
                } else if (entry.getKey().indexOf(Operators.LTE) != -1) {// 小于等于
                    String fieldName = entry.getKey().substring(0, entry.getKey().indexOf(Operators.LTE));
                    if (paramMap.containsKey(StringUtil.concat(fieldName, Operators.GTE))) {
                        continue;
                    }
                    criteria = index == 0 ? criteria.where(fieldName).lte(entry.getValue()) : criteria.and(fieldName).lte(entry.getValue());
                } else if (entry.getKey().indexOf(Operators.GT) != -1) {// 大于
                    String fieldName = entry.getKey().substring(0, entry.getKey().indexOf(Operators.GT));
                    if (paramMap.containsKey(StringUtil.concat(fieldName, Operators.LT))) {
                        // 当存在同字段小于等于时组合
                        criteria = index == 0 ? criteria.where(fieldName).gt(entry.getValue())
                                .lt(paramMap.get(StringUtil.concat(fieldName, Operators.LTE))) : criteria.and(fieldName)
                                .gt(entry.getValue()).lt(paramMap.get(StringUtil.concat(fieldName, Operators.LTE)));
                    } else {
                        criteria = index == 0 ? criteria.where(fieldName).gt(entry.getValue()) : criteria.and(fieldName).gt(entry.getValue());
                    }
                } else if (entry.getKey().indexOf(Operators.LT) != -1) {// 小于
                    String fieldName = entry.getKey().substring(0, entry.getKey().indexOf(Operators.LT));
                    if (paramMap.containsKey(StringUtil.concat(fieldName, Operators.GT))) {
                        continue;
                    }
                    criteria = index == 0 ? criteria.where(fieldName).lt(entry.getValue()) : criteria.and(fieldName).lt(entry.getValue());
                } else if (entry.getKey().indexOf(Operators.BET) != -1) {// 范围
                    String fieldName = entry.getKey().substring(0, entry.getKey().indexOf(Operators.BET));
                    List<Object> rangeList = (List<Object>) entry.getValue();
                    criteria = index == 0 ? criteria.where(fieldName).gte(rangeList.get(0)).lte(rangeList.get(1)) : criteria.and(fieldName)
                            .gte(rangeList.get(0)).lte(rangeList.get(1));
                } else if (entry.getKey().indexOf(Operators.BLK) != -1) {// 模糊匹配
                    String fieldName = entry.getKey().substring(0, entry.getKey().indexOf(Operators.BLK));
                    criteria = index == 0 ? criteria.where(fieldName).regex(entry.getValue().toString()) : criteria.and(fieldName).regex(
                            entry.getValue().toString());
                } else if (entry.getKey().indexOf(Operators.RLK) != -1) {// 右模糊匹配
                    String fieldName = entry.getKey().substring(0, entry.getKey().indexOf(Operators.RLK));
                    criteria = index == 0 ? criteria.where(fieldName).regex(StringUtil.concat("^", entry.getValue().toString())) : criteria.and(
                            fieldName).regex(StringUtil.concat("^", entry.getValue().toString()));
                } else if (entry.getKey().indexOf(Operators.LLK) != -1) {// 左模糊匹配
                    String fieldName = entry.getKey().substring(0, entry.getKey().indexOf(Operators.LLK));
                    criteria = index == 0 ? criteria.where(fieldName).regex(StringUtil.concat(entry.getValue().toString(), "$")) : criteria.and(
                            fieldName).regex(StringUtil.concat(entry.getValue().toString(), "^"));
                } else if (entry.getKey().indexOf(Operators.EXSISTS) != -1) {// 存在
                    String fieldName = entry.getKey().substring(0, entry.getKey().indexOf(Operators.EXSISTS));
                    criteria = index == 0 ? criteria.where(fieldName).exists((Boolean) entry.getValue()) : criteria.and(fieldName).exists(
                            (Boolean) entry.getValue());
                } else if (entry.getKey().indexOf(Operators.IGNORE) != -1) {// 跳过
                    continue;
                } else if (entry.getKey().indexOf(Operators.NE) != -1) {
                    String fieldName = entry.getKey().substring(0, entry.getKey().indexOf(Operators.NE));
                    criteria = index == 0 ? criteria.where(fieldName).ne(entry.getValue()) : criteria.and(fieldName).ne(entry.getValue());
                } else {
                    criteria = index == 0 ? criteria.where(entry.getKey()).is(entry.getValue()) : criteria.and(entry.getKey()).is(entry.getValue());
                }
                index++;
            }
        }
        return criteria;
    }

    protected Update combineUpdateSet(Map<String, Object> resultMap) {
        Update update = new Update();
        for (Map.Entry<String, Object> entry : resultMap.entrySet()) {
            update.set(entry.getKey(), entry.getValue());
        }
        return update;
    }
}
