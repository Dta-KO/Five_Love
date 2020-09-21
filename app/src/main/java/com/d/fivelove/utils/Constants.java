package com.d.fivelove.utils;

import java.util.HashMap;

/**
 * Created by Nguyen Kim Khanh on 9/5/2020.
 */
public class Constants {
    public static final String REMOTE_MSG_TYPE = "type";
    public static final String REMOTE_MSG_INVITATION = "invitation";
    public static final String REMOTE_MSG_MEETING_TYPE = "meetingType";
    public static final String REMOTE_MSG_MEETING_AUDIO_TYPE = "audio";
    public static final String REMOTE_MSG_MEETING_VIDEO_TYPE = "video";
    public static final String REMOTE_MSG_AVATAR_USER = "avatarUser";

    public static final String REMOTE_MSG_INVITER_ID = "inviterId";
    public static final String REMOTE_MSG_INVITATION_RESPONSE = "invitationResponse";
    public static final String REMOTE_MSG_INVITATION_ACCEPTED = "accepted";
    public static final String REMOTE_MSG_INVITATION_REFUSED = "refused";
    public static final String REMOTE_MSG_INVITATION_CANCELED = "canceled";

    public static final String REMOTE_MSG_INVITER_TOKEN = "inviterToken";


    public static final String REMOTE_MSG_DATA = "data";
    public static final String REMOTE_MSG_REGISTRATION_IDS = "registration_ids";
    public static final String ROOM_ID = "roomId";
    public static final String CURRENT_POSITION_VIEW_PAGER = "positionViewPager";
    public static final String INVITATION_ADD_FRIEND = "invitationAddFriend";
    public static final String PARTNER_TOKEN = "partnerToken";
    private static final String REMOTE_MSG_AUTHORIZATION = "Authorization";
    private static final String REMOTE_MSG_CONTENT_TYPE = "Content-Type";

    public static HashMap<String, String> getRemoteMessageHeaders() {
        HashMap<String, String> header = new HashMap<>();
        header.put(REMOTE_MSG_AUTHORIZATION, "key=AAAAcgwfhvg:APA91bFwvUECJpEjP4oWK9ZtUbJ0I1CSAZjw1cp6nZu74BD29_Ce25ibYwJlrf6dqzvfuevtVSme8SXjJBqo5MbEyItawxyDtEJR9BE7_a3TrTmJ3ZVqMi0LQ8t3bfmhzz1nWC09Cz1_");
        header.put(REMOTE_MSG_CONTENT_TYPE, "application/json");
        return header;
    }


}
