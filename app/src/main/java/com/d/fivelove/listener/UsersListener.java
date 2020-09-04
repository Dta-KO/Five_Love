package com.d.fivelove.listener;

import com.d.fivelove.model.User;

/**
 * Created by Nguyen Kim Khanh on 9/3/2020.
 */
public interface UsersListener {
    void initiateVideoMeeting(User user);

    void initiateAudioMeeting(User user);
}
