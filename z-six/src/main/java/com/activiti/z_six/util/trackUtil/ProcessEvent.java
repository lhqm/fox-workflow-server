package com.activiti.z_six.util.trackUtil;

public enum ProcessEvent {
    /**
     * 启动成功之后
     */
    AfterStart(0),
    /**
     * 流程结束之前
     */
    BeforeEnd(1),
    /**
     * 流程结束之后
     */
    AftereEnd(2);
    public static final int SIZE = java.lang.Integer.SIZE;

    private int intValue;
    private static java.util.HashMap<Integer, ProcessEvent> mappings;
    private static java.util.HashMap<Integer, ProcessEvent> getMappings()  {
        if (mappings == null)
        {
            synchronized (ProcessEvent.class)
            {
                if (mappings == null)
                {
                    mappings = new java.util.HashMap<Integer, ProcessEvent>();
                }
            }
        }
        return mappings;
    }

    private ProcessEvent(int value)
    {
        intValue = value;
        getMappings().put(value, this);
    }

    public int getValue()  {
        return intValue;
    }

    public static ProcessEvent forValue(int value)
    {
        return getMappings().get(value);
    }
}
