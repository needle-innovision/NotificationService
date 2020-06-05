package com.fms.notification.vo;

public class RedirectResponse<T> extends Response {

    private String redirectPage;

    public RedirectResponse(String message, String redirectPath) {
        super(message, null);
        this.redirectPage = redirectPath;
    }

    public RedirectResponse(String message, T data, String redirectPath) {
        super(message, data);
        this.redirectPage = redirectPath;
    }

    public String getRedirectPage() {
        return redirectPage;
    }

    public void setRedirectPage(String redirectPage) {
        this.redirectPage = redirectPage;
    }
}
