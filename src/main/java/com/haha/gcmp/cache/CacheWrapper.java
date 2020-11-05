package com.haha.gcmp.cache;

import java.io.Serializable;
import java.util.Date;

/**
 * Cache wrapper
 *
 * @author SZFHH
 * @date 2020/10/18
 */
class CacheWrapper<V> implements Serializable {

    public V getData() {
        return data;
    }

    public void setData(V data) {
        this.data = data;
    }

    public Date getExpireAt() {
        return expireAt;
    }

    public void setExpireAt(Date expireAt) {
        this.expireAt = expireAt;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    private V data;

    private Date expireAt;

    private Date createAt;
}
