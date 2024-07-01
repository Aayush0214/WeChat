package com.project.wechat.listeners;

import com.project.wechat.models.User;

public interface ConversationListener {
    void onConversationClicked(User user);
}
