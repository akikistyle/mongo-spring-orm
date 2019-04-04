package com.akikistyle.mongo.orm.bo;

import java.util.HashMap;

public class Pager {

    /**
     * @preserve
     */
    private Integer pageSize = 20;

    /**
     * @preserve
     */
    private Integer pageCount;

    /**
     * @preserve
     */
    private Integer recordCount;

    /**
     * @preserve
     */
    private Integer page = 0;

    /**
     * @preserve
     */
    private HashMap<String, String> filter = new HashMap<String, String>();

    /**
     * @preserve
     */
    private String queryString;

    private Integer start = 0;

    public Pager() {
    }

    public Pager(Integer page, Integer pageSize) {
        if (page < 1) {
            this.page = 0;
        } else {
            this.page = page - 1;
        }

        this.pageSize = pageSize;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize < 1 ? 0 : pageSize;
    }

    public Integer getPageCount() {
        return pageCount;
    }

    public void setPageCount(Integer pageCount) {
        this.pageCount = pageCount;
    }

    public Integer getRecordCount() {
        return recordCount;
    }

    public void setRecordCount(Integer recordCount) {
        this.recordCount = recordCount;
        if (pageSize < 1) {
            pageCount = 1;
        } else {
            pageCount = recordCount % pageSize == 0 ? (recordCount / pageSize) : (recordCount / pageSize + 1);
            start = page * pageSize;
        }
    }

    public Integer getPage() {
        return page < 0 ? 0 : page;
    }

    public void setPage(Integer page) {
        this.page = page < 0 ? 0 : page;
    }

    public HashMap<String, String> getFilter() {
        return this.filter;
    }

    public String getQueryString() {
        return this.queryString;
    }

    public Integer getStart() {
        return start;
    }

    public void setStart(Integer start) {
        this.start = start;
    }

}