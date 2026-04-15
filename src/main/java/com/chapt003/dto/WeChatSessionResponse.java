package com.chapt003.dto;

public class WeChatSessionResponse {
    private String openid;
    private String sessionKey;
    private String unionid;
    private Integer errcode;
    private String errmsg;

    public WeChatSessionResponse() {
    }

    public WeChatSessionResponse(String openid, String sessionKey, String unionid) {
        this.openid = openid;
        this.sessionKey = sessionKey;
        this.unionid = unionid;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getSessionKey() {
        return sessionKey;
    }

    public void setSessionKey(String sessionKey) {
        this.sessionKey = sessionKey;
    }

    public String getUnionid() {
        return unionid;
    }

    public void setUnionid(String unionid) {
        this.unionid = unionid;
    }

    public Integer getErrcode() {
        return errcode;
    }

    public void setErrcode(Integer errcode) {
        this.errcode = errcode;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private WeChatSessionResponse response = new WeChatSessionResponse();

        public Builder openid(String openid) {
            response.setOpenid(openid);
            return this;
        }

        public Builder sessionKey(String sessionKey) {
            response.setSessionKey(sessionKey);
            return this;
        }

        public Builder unionid(String unionid) {
            response.setUnionid(unionid);
            return this;
        }

        public Builder errcode(Integer errcode) {
            response.setErrcode(errcode);
            return this;
        }

        public Builder errmsg(String errmsg) {
            response.setErrmsg(errmsg);
            return this;
        }

        public WeChatSessionResponse build() {
            return response;
        }
    }
}