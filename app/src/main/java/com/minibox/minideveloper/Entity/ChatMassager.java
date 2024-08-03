package com.minibox.minideveloper.Entity;

import java.io.Serializable;

public class ChatMassager implements Serializable {

   public static final String CHAT_BY_ME = "me";
   public static final String CHAT_BY_BOT = "bot";

   private  String Massager;
   private  String BySend;
   private  String Head;

    public String getMassager() {return Massager;}

    public String getBySend() {return BySend;}

    public String getHead() {return Head;}

    public void setMassager(String massager) {Massager = massager;}

    public void setBySend(String bySend) {BySend = bySend;}

    public void setHead(String head) {Head = head;}

    public ChatMassager(String massager, String bySend, String head){
        this.Massager = massager;
        this.BySend = bySend;
        this.Head = head;
    }

}
