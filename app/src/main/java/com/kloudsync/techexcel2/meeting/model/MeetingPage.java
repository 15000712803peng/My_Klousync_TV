package com.kloudsync.techexcel2.meeting.model;

/**
 * Created by tonyan on 2019/10/11.
 */

public class MeetingPage {
    private String pageUrl;
    private String savedLocalPath;
    private String showingPath;


    public String getPageUrl() {
        return pageUrl;
    }

    public void setPageUrl(String pageUrl) {
        this.pageUrl = pageUrl;
    }

    public String getSavedLocalPath() {
        return savedLocalPath;
    }

    public void setSavedLocalPath(String savedLocalPath) {
        this.savedLocalPath = savedLocalPath;
    }

    public String getShowingPath() {
        return showingPath;
    }

    public void setShowingPath(String showingPath) {
        this.showingPath = showingPath;
    }

    @Override
    public String toString() {
        return "MeetingPage{" +
                "pageUrl='" + pageUrl + '\'' +
                ", savedLocalPath='" + savedLocalPath + '\'' +
                ", showingPath='" + showingPath + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MeetingPage that = (MeetingPage) o;

        return pageUrl != null ? pageUrl.equals(that.pageUrl) : that.pageUrl == null;
    }

    @Override
    public int hashCode() {
        return pageUrl != null ? pageUrl.hashCode() : 0;
    }
}
