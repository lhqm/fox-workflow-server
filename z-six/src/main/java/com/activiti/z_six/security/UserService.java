package com.activiti.z_six.security;

import com.activiti.z_six.entity.UserInfo;
import com.activiti.z_six.mapper.UserInfoMapper;
import com.activiti.z_six.util.encode.AesPasswordEncoder;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.context.annotation.Bean;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService implements UserDetailsService {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    UserInfoMapper mapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //强绑定，解决微服务的集成问题
        List<GrantedAuthority> grantedAuthorities= Lists.newArrayList();
        GrantedAuthority grantedAuthority=new SimpleGrantedAuthority("ROLE_ACTIVITI_USER");
        grantedAuthorities.add(grantedAuthority);
        UserInfo userInfoBean = mapper.getUserInfo(username);
        if (userInfoBean == null) {
            throw new UsernameNotFoundException("数据库中无此用户！");
        }
        return new User(userInfoBean.getUsername(),userInfoBean.getPassword(),grantedAuthorities);
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new AesPasswordEncoder();
    }
}
