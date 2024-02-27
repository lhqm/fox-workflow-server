package com.activiti.z_six.dto;
import javax.validation.constraints.NotNull;

public class BaseInPageDto {
    protected @NotNull(
            message = "当前页数不能为空"
    ) Integer pageNo = 1;
    protected @NotNull(
            message = "每页显示的记录数不能为空"
    ) Integer pageSize = 10;

    public BaseInPageDto() {
    }

    public Integer getPageNo() {
        return this.pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

    public Integer getPageSize() {
        return this.pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getDataIndex() {
        return (this.pageNo - 1) * this.pageSize;
    }
}

