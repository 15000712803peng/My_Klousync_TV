package com.kloudsync.techexcel2.info;

import org.json.JSONObject;

/**
 * Created by pingfan on 2017/9/22.
 */

public class Favorite {
    private int ProjectID;
    private int LinkedKWProjectID;
    private int AttachmentID;
    private int IncidentID;
    private int FileID;
    private int AttachmentTypeID;
    private int Status;
    private int flag;
    private int progressbar;
    private int ItemID;
    private String Title;
    private String Description;
    private String FileName;
    private String FileDownloadURL;
    private String CreatedDate;
    private String size;
    private String duration;
    private JSONObject jsonObject;

    public int getItemID() {
        return ItemID;
    }

    public void setItemID(int itemID) {
        ItemID = itemID;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public int getProgressbar() {
        return progressbar;
    }

    public void setProgressbar(int progressbar) {
        this.progressbar = progressbar;
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }

    public void setJsonObject(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public Favorite() {
    }

    public int getProjectID() {
        return ProjectID;
    }

    public void setProjectID(int projectID) {
        ProjectID = projectID;
    }

    public int getLinkedKWProjectID() {
        return LinkedKWProjectID;
    }

    public void setLinkedKWProjectID(int linkedKWProjectID) {
        LinkedKWProjectID = linkedKWProjectID;
    }

    public int getAttachmentID() {
        return AttachmentID;
    }

    public void setAttachmentID(int attachmentID) {
        AttachmentID = attachmentID;
    }

    public int getIncidentID() {
        return IncidentID;
    }

    public void setIncidentID(int incidentID) {
        IncidentID = incidentID;
    }

    public int getFileID() {
        return FileID;
    }

    public void setFileID(int fileID) {
        FileID = fileID;
    }

    public int getAttachmentTypeID() {
        return AttachmentTypeID;
    }

    public void setAttachmentTypeID(int attachmentTypeID) {
        AttachmentTypeID = attachmentTypeID;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getFileName() {
        return FileName;
    }

    public void setFileName(String fileName) {
        FileName = fileName;
    }

    public String getFileDownloadURL() {
        return FileDownloadURL;
    }

    public void setFileDownloadURL(String fileDownloadURL) {
        FileDownloadURL = fileDownloadURL;
    }

    public String getCreatedDate() {
        return CreatedDate;
    }

    public void setCreatedDate(String createdDate) {
        CreatedDate = createdDate;
    }
}
