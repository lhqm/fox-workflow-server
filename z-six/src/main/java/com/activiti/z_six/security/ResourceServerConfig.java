package com.activiti.z_six.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;

@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {
    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .anyRequest().authenticated()
                //设置允许访问路径
                .and().requestMatchers().antMatchers("/form/**"
                        ,"/task/**"
                        ,"/processManage/**"
                        ,"/as/**"
                        ,"/orgm/**"
                        ,"/common/**"
                        ,"/system/**"
                        ,"/login/**"
                        ,"/formData/**"
                        ,"/deployment/**"
                        ,"/menu/**"
                        ,"flowCond/**"
                        ,"tenant/api/**"
                        ,"/dicts/**");
    }

}
