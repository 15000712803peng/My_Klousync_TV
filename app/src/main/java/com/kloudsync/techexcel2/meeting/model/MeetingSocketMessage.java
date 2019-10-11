package com.kloudsync.techexcel2.meeting.model;

import java.util.List;

/**
 * Created by tonyan on 2019/10/10.
 */

public class MeetingSocketMessage {

    private String invitedUserIds;
    private String userId;
    private String sessionId;
    private int presentStatus;
    private String phoneInfo;
    private String presenterSessionId;
    private int isAuditor;
    private int status;
    private String CurrentDocumentPage;
    private int currentMode;
    private int currentLine;
    private int prepareMode;
    private String playAudioData;
    private int hideCamera;
    private String recordingId;
    private int sizeMode;
    private int audienceCount;
    private int type;
    private String lessonId;
    private int syncMode;
    private List<MeetingUser> usersList;

    public String getInvitedUserIds() {
        return invitedUserIds;
    }

    public void setInvitedUserIds(String invitedUserIds) {
        this.invitedUserIds = invitedUserIds;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public int getPresentStatus() {
        return presentStatus;
    }

    public void setPresentStatus(int presentStatus) {
        this.presentStatus = presentStatus;
    }

    public String getPhoneInfo() {
        return phoneInfo;
    }

    public void setPhoneInfo(String phoneInfo) {
        this.phoneInfo = phoneInfo;
    }

    public String getPresenterSessionId() {
        return presenterSessionId;
    }

    public void setPresenterSessionId(String presenterSessionId) {
        this.presenterSessionId = presenterSessionId;
    }

    public int getIsAuditor() {
        return isAuditor;
    }

    public void setIsAuditor(int isAuditor) {
        this.isAuditor = isAuditor;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getCurrentDocumentPage() {
        return CurrentDocumentPage;
    }

    public void setCurrentDocumentPage(String currentDocumentPage) {
        CurrentDocumentPage = currentDocumentPage;
    }

    public int getCurrentMode() {
        return currentMode;
    }

    public void setCurrentMode(int currentMode) {
        this.currentMode = currentMode;
    }

    public int getCurrentLine() {
        return currentLine;
    }

    public void setCurrentLine(int currentLine) {
        this.currentLine = currentLine;
    }

    public int getPrepareMode() {
        return prepareMode;
    }

    public void setPrepareMode(int prepareMode) {
        this.prepareMode = prepareMode;
    }

    public String getPlayAudioData() {
        return playAudioData;
    }

    public void setPlayAudioData(String playAudioData) {
        this.playAudioData = playAudioData;
    }

    public int getHideCamera() {
        return hideCamera;
    }

    public void setHideCamera(int hideCamera) {
        this.hideCamera = hideCamera;
    }

    public String getRecordingId() {
        return recordingId;
    }

    public void setRecordingId(String recordingId) {
        this.recordingId = recordingId;
    }

    public int getSizeMode() {
        return sizeMode;
    }

    public void setSizeMode(int sizeMode) {
        this.sizeMode = sizeMode;
    }

    public int getAudienceCount() {
        return audienceCount;
    }

    public void setAudienceCount(int audienceCount) {
        this.audienceCount = audienceCount;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getLessonId() {
        return lessonId;
    }

    public void setLessonId(String lessonId) {
        this.lessonId = lessonId;
    }

    public int getSyncMode() {
        return syncMode;
    }

    public void setSyncMode(int syncMode) {
        this.syncMode = syncMode;
    }

    public List<MeetingUser> getUsersList() {
        return usersList;
    }

    public void setUsersList(List<MeetingUser> usersList) {
        this.usersList = usersList;
    }

    @Override
    public String toString() {
        return "MeetingSocketMessage{" +
                "invitedUserIds='" + invitedUserIds + '\'' +
                ", userId='" + userId + '\'' +
                ", sessionId='" + sessionId + '\'' +
                ", presentStatus=" + presentStatus +
                ", phoneInfo='" + phoneInfo + '\'' +
                ", presenterSessionId='" + presenterSessionId + '\'' +
                ", isAuditor=" + isAuditor +
                ", status=" + status +
                ", CurrentDocumentPage='" + CurrentDocumentPage + '\'' +
                ", currentMode=" + currentMode +
                ", currentLine=" + currentLine +
                ", prepareMode=" + prepareMode +
                ", playAudioData='" + playAudioData + '\'' +
                ", hideCamera=" + hideCamera +
                ", recordingId='" + recordingId + '\'' +
                ", sizeMode=" + sizeMode +
                ", audienceCount=" + audienceCount +
                ", type=" + type +
                ", lessonId='" + lessonId + '\'' +
                ", syncMode=" + syncMode +
                ", usersList=" + usersList +
                '}';
    }
}
