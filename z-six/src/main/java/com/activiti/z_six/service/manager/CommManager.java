package com.activiti.z_six.service.manager;

import com.activiti.z_six.entity.orgmanagement.CompanyEntity;
import com.activiti.z_six.entity.orgmanagement.DepartmentEntity;
import com.activiti.z_six.entity.orgmanagement.UserEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommManager {
    public String getUserNo(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return username;
    }
    /**
     * 递归查找当前部门的子部门
     * @param root 单个对象
     * @param all 所有的集合
     * @return 排序后的子类
     */
    public List<DepartmentEntity> getDeptChildren(DepartmentEntity root, List<DepartmentEntity> all){
        List<DepartmentEntity> childrenList = all.stream()
                .filter(t -> t.getParentid() == root.getId())
                .map(g -> {
                    //找子菜单
                    g.setChildren(getDeptChildren(g,all));
                    return g;
                })
                //菜单排序
                .collect(Collectors.toList());

        return childrenList;
    }
    /**
     * 递归查找当前公司的子公司
     * @param root 单个对象
     * @param all 所有的集合
     * @return 排序后的子类
     */
    public List<CompanyEntity> getCompanyChildren(CompanyEntity root, List<CompanyEntity> all){
        List<CompanyEntity> childrenList = all.stream()
                .filter(t -> t.getParentid() == root.getId())
                .map(g -> {
                    //找子菜单
                    g.setChildren(getCompanyChildren(g,all));
                    return g;
                })
                //菜单排序
                .collect(Collectors.toList());

        return childrenList;
    }

    /**
     * 查询人员树
     * @param root
     * @param all
     * @return
     */
    public List<UserEntity> getUserChildren(UserEntity root, List<UserEntity> all){
        List<UserEntity> childrenList = all.stream()
                .filter(t -> t.getDepartid().toString().equals(root.getUsername()) )
                .map(g -> {
                    //找子菜单
                    g.setChildren(getUserChildren(g,all));
                    return g;
                })
                //菜单排序
                .collect(Collectors.toList());

        return childrenList;
    }
}
