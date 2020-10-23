package com.haha.gcmp.cache;


import java.io.Serializable;
import java.util.Date;

/**
 * Cache wrapper.
 *
 * @author johnniang
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

    /**
     * Cache data
     */
    private V data;

    /**
     * Expired time.
     */
    private Date expireAt;

    /**
     * Create time.
     */
    private Date createAt;
}
