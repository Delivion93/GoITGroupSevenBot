package com.goitgroupsevenbot.repository;

import com.goitgroupsevenbot.entity.User;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class UserList {
    public static ConcurrentHashMap<Long, User> userList = new ConcurrentHashMap<>();
}
