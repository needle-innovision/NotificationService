package com.go.notification.models;


public class UserVO {

    private long userId;
    private String userName;
    private String email;
    private int orgType;
    private long orgUniqueId;
    private int notificationReadStatus;

    public int getNotificationReadStatus() {
        return notificationReadStatus;
    }

    public void setNotificationReadStatus(int notificationReadStatus) {
        this.notificationReadStatus = notificationReadStatus;
    }



    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int hashCode() {
        int id = 0;
        try {
            id = new Long(userId + email.hashCode()).hashCode();
        } catch (Exception e) {
            System.out.println(e + " : ==================occured while getting hs code : ");

        }
        return id;
    }

    public boolean equals(Object obj) {

        if (!(obj instanceof UserVO))
            return false;

        UserVO map = (UserVO) obj;

        if (this.userId + this.email.hashCode() == map.userId + map.email.hashCode()) {
            return true;
        } else {
            return false;
        }
    }

    public int getOrgType() {
        return orgType;
    }

    public void setOrgType(int orgType) {
        this.orgType = orgType;
    }

    public long getOrgUniqueId() {
        return orgUniqueId;
    }

    public void setOrgUniqueId(long orgUniqueId) {
        this.orgUniqueId = orgUniqueId;
    }



}
