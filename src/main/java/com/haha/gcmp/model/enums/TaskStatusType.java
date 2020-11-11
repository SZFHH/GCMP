package com.haha.gcmp.model.enums;

import org.springframework.lang.Nullable;

import static com.haha.gcmp.model.support.GcmpConst.*;

/**
 * k8s pod status
 *
 * @author SZFHH
 * @date 2020/11/4
 */
public enum TaskStatusType {
    /**
     * PENDING STATUS
     */
    PENDING(0, POD_STATUS_PENDING),

    /**
     * RUNNING STATUS
     */
    RUNNING(1, POD_STATUS_RUNNING),

    /**
     * SUCCEEDED STATUS
     */
    SUCCEEDED(2, POD_STATUS_SUCCEEDED),

    /**
     * FAILED STATUS
     */
    FAILED(3, POD_STATUS_FAILED),

    /**
     * DELETED STATUS
     */
    DELETED(4, POD_STATUS_DELETED),

    /**
     * UNKNOWN STATUS
     */
    UNKNOWN(5, POD_STATUS_UNKNOWN),

    /**
     * RETRY STATUS
     */
    RETRY(6, POD_STATUS_RETRY);

    private final Integer value;

    private final String desc;

    TaskStatusType(Integer value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public Integer getValue() {
        return value;
    }

    public String getDesc() {
        return desc;
    }

    public static TaskStatusType valueFrom(@Nullable String value) {
        TaskStatusType taskStatusType = null;
        for (TaskStatusType status : values()) {
            if (status.getDesc().equals(value)) {
                taskStatusType = status;
                break;
            }
        }
        if (taskStatusType == null) {
            taskStatusType = UNKNOWN;
        }
        return taskStatusType;
    }
}
