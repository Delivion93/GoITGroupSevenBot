package com.goitgroupsevenbot.repository;

import com.goitgroupsevenbot.entity.domain.User;

import java.util.concurrent.ConcurrentHashMap;

public class UserRepository {
    private ConcurrentHashMap<Long, User> userList = new ConcurrentHashMap<>();

    public boolean addUser(Long chatId, User user) {
        userList.put(chatId, user);
        return true;
    }

    public boolean updateUser(Long chatId, User user) {
        userList.put(chatId, user);
        return true;
    }

    public User getById(Long chatId) {
        return userList.get(chatId);
    }

    public ConcurrentHashMap<Long, User> getAll() {
        return userList;
    }
}
