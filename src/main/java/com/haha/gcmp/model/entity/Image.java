package com.haha.gcmp.model.entity;

/**
 * @author SZFHH
 * @date 2020/10/24
 */
public class Image {
    int id;
    String tag;
    String desc;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getOwner() {
        return owner;
    }

    public void setOwner(int owner) {
        this.owner = owner;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    int owner;
    String alias;

    public Image(String tag, String desc, int owner, String alias) {
        this.tag = tag;
        this.desc = desc;
        this.owner = owner;
        this.alias = alias;
    }

    public Image() {
    }
}
