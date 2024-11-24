package com.cc.common.utils;

public class UserContext {
    private static final ThreadLocal<Long> tl = new ThreadLocal<>();

    /**
     * Save the current logged in user information to ThreadLocal
     * @param userId userId
     */
    public static void setUser(Long userId) {
        tl.set(userId);
    }

    /**
     * Retrieve the information of the currently logged in user
     * @return userId
     */
    public static Long getUser() {
        return tl.get();
    }

    /**
     * Remove the current logged in user information
     */
    public static void removeUser(){
        tl.remove();
    }
}