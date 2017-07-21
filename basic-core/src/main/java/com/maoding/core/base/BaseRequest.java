package com.maoding.core.base;

import java.io.Serializable;

/**
 * Created by Chengliang.zhang on 2017/7/18.
 * 操作请求基类，后续可
 */
public class BaseRequest implements Serializable {
    /** 操作者标识 */
    AuthToken token;

    /** 模拟token类型 */
    public class AuthToken {
        private String account;
        private String password;
        private String token;

        public String getAccount() {
            return account;
        }

        public void setAccount(String account) {
            this.account = account;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }
    }

    public AuthToken getToken() {
        return token;
    }

    public void setToken(AuthToken token) {
        this.token = token;
    }
}
