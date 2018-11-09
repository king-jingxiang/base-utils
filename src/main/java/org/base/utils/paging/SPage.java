package org.base.utils.paging;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 分页信息.Paging 基 1
 */
public class SPage<T> implements Serializable {
    private List<T> content;
    private int size;
    private int page;
    private long total;

    public SPage(List<T> content) {
        this.content = content;
    }

    public SPage(List<T> content, int page, int size, long total) {
        this.content = content;
        this.size = size;
        this.page = page;
        this.total = total;
    }

    public SPage(List<T> content, int page, int size, int total) {
        this.content = content;
        this.size = size;
        this.page = page;
        this.total = total;
    }

    public List<T> getContent() {
        return content;
    }

    public int getSize() {
        return size;
    }

    public int getPage() {
        return page;
    }

    public long getTotal() {
        return total;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }

    public static  <R>  SPage<R> noResult(final int page,final int size){
        return new SPage<R>(new ArrayList<>(),page,size,1000);
    }
}
