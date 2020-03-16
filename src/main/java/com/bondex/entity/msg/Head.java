/**
  * Copyright 2018 bejson.com 
  */
package com.bondex.entity.msg;

/**
 * Auto-generated: 2018-06-26 10:49:28
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class Head {

    private String SeqNo; //消息标号
    private String SenderID; //BOE
    private String SenderName; //BOE系统
    private String ReciverID; //TrackSystem
    private String ReciverName; //追踪系统
    private String DocTypeID; //BOE 系统
    private String DocTypeName; //BOE追踪任务
    private String CreateTime; //创建时间
    private String FinishTime;
    private String DataInterfaceID;
    private String DataInterfaceName;
    private String Action;
    private String Version;
    public void setSeqNo(String SeqNo) {
         this.SeqNo = SeqNo;
     }
     public String getSeqNo() {
         return SeqNo;
     }

    public void setSenderID(String SenderID) {
         this.SenderID = SenderID;
     }
     public String getSenderID() {
         return SenderID;
     }

    public void setSenderName(String SenderName) {
         this.SenderName = SenderName;
     }
     public String getSenderName() {
         return SenderName;
     }

    public void setReciverID(String ReciverID) {
         this.ReciverID = ReciverID;
     }
     public String getReciverID() {
         return ReciverID;
     }

    public void setReciverName(String ReciverName) {
         this.ReciverName = ReciverName;
     }
     public String getReciverName() {
         return ReciverName;
     }

    public void setDocTypeID(String DocTypeID) {
         this.DocTypeID = DocTypeID;
     }
     public String getDocTypeID() {
         return DocTypeID;
     }

    public void setDocTypeName(String DocTypeName) {
         this.DocTypeName = DocTypeName;
     }
     public String getDocTypeName() {
         return DocTypeName;
     }

    public void setCreateTime(String CreateTime) {
         this.CreateTime = CreateTime;
     }
     public String getCreateTime() {
         return CreateTime;
     }

    public void setFinishTime(String FinishTime) {
         this.FinishTime = FinishTime;
     }
     public String getFinishTime() {
         return FinishTime;
     }

    public void setDataInterfaceID(String DataInterfaceID) {
         this.DataInterfaceID = DataInterfaceID;
     }
     public String getDataInterfaceID() {
         return DataInterfaceID;
     }

    public void setDataInterfaceName(String DataInterfaceName) {
         this.DataInterfaceName = DataInterfaceName;
     }
     public String getDataInterfaceName() {
         return DataInterfaceName;
     }

    public void setAction(String Action) {
         this.Action = Action;
     }
     public String getAction() {
         return Action;
     }

    public void setVersion(String Version) {
         this.Version = Version;
     }
     public String getVersion() {
         return Version;
     }

}