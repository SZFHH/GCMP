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
     * SUCCEED STATUS
     */
    SUCCEED(2, POD_STATUS_SUCCEED),

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
    UNKNOWN(5, POS_STATUS_UNKNOWN);

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
            if (taskStatusType.getDesc().equals(value)) {
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
