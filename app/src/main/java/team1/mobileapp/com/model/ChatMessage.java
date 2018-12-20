package team1.mobileapp.com.model;

public class ChatMessage {

    String content; //메세지 내용
    String sender; // 보낸 사람 key
    String time; //보낸 시간


    public ChatMessage() {

    }

    public ChatMessage(String content, String sender, String time ){
        this.content = content;
        this.sender = sender;
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
