package com.activiti.z_six.oauth;

import com.activiti.z_six.entity.UserInfo;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class OauthUserQuickLoginAuthenticationToken extends AbstractAuthenticationToken {

    private final UserInfo user;

    public OauthUserQuickLoginAuthenticationToken(UserInfo user) {
        super(null);
        this.user = user;
        setAuthenticated(false);
    }

    public OauthUserQuickLoginAuthenticationToken(UserInfo user, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.user = user;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return user;
    }
}