package entities;

import java.util.Date;

public class Message {
    private Date date;
    private String message;
    private Contact sender;


     public Message(String message, Date date){
         this.message=message;
         this.date=date;
     }
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Contact getSender() {
        return sender;
    }

    public void setSender(Contact sender) {
        this.sender = sender;
    }
}
