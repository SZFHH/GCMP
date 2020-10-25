package com.haha.gcmp.model.params;

/**
 * @author SZFHH
 * @date 2020/10/24
 */
public class ImageToAddParam {
    String tag;
    String desc;
    String alias;
    String dockerFile;

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
