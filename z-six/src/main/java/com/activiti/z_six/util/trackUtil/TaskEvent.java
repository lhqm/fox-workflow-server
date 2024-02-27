package com.activiti.z_six.util.trackUtil;

public enum TaskEvent {
    /**
     * 提交之前
     */
    BeforeSubmit(0),
    /**
     * 提交成功时
     */
    SubmitSucess(1),
    /**
     * 提交失败时
     */
    SubmitError(2),
    /**
     * 驳回之前
     */
    BeforeReturn(3),
    /**
     * 驳回成功时
     */
    AfterReturn(4),
    /**
     * 拒绝之前
     */
    BeforeRefuse(5),
    /**
     * 拒绝之后
     */
    AfterRefuse(6),
    /**
     * 手动结束之前
     */
    BeforeendTask(7),
    /**
     * 手动结束之后
     */
    AfterendTask(8);
    public static final int SIZE = java.lang.Integer.SIZE;

    private int intValue;
    private static java.util.HashMap<Integer, TaskEvent> mappings;
    private static java.util.HashMap<Integer, TaskEvent> getMappings()  {
        if (mappings == null)
        {
            synchronized (TaskEvent.class)
            {
                if (mappings == null)
                {
                    mappings = new java.util.HashMap<Integer, TaskEvent>();
                }
            }
        }
        return mappings;
    }

    private TaskEvent(int value)
    {
        intValue = value;
        getMappings().put(value, this);
    }

    public final int getValue()  {
        return intValue;
    }

    public static TaskEvent forValue(int value)
    {
        return getMappings().get(value);
    }
}
