package com.haha.gcmp.model.params;

/**
 * Image param
 *
 * @author SZFHH
 * @date 2020/10/24
 */
public class ImageParam {

    /**
     * docker镜像id
     */
    int id;

    /**
     * docker镜像tag
     */
    String tag;

    /**
     * 镜像描述
     */
    String desc;

    /**
     * 镜像简化名字
     */
    String alias;

    /**
     * dockerFile文件
     */
    String dockerFile;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isCommon() {
        return "".equals(dockerFile);
    }

    public String getDockerFile() {
        return dockerFile;
    }

    public void setDockerFile(String dockerFile) {
        this.dockerFile = dockerFile;
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


    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

}
