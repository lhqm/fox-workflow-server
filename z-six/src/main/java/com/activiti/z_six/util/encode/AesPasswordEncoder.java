package com.activiti.z_six.util.encode;

import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 自定义的密码编码器
 */
public class AesPasswordEncoder implements PasswordEncoder {
    /**
     * 加密
     * @param rawPassword
     * @return
     */
    @Override
    public String encode(CharSequence rawPassword) {
        if (rawPassword == null){

        }
        else {
            String encode = null;
            try {
                encode = AesUtil.encryptAES(rawPassword.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return encode;
        }
        return null;
    }

    /**
     * 校验密码
     * @param rawPassword 输入的密码
     * @param encodedPassword 数据库中加密的密码
     * @return 、
     */
    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        boolean flag = false;
        try {
            if (encodedPassword.equals(AesUtil.encryptAES(rawPassword.toString()))) flag = true;
            else flag = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    @Override
    public boolean upgradeEncoding(String encodedPassword) {
        return false;
    }
}
