package team1.mobileapp.com.model;

public class ChatMember {
    String studentNumber;
    String nickName;
    int position;

    public ChatMember(){

    }

    public ChatMember(String studentNumber, int position){
        this.studentNumber = studentNumber;
        this.position = position;
    }

    public ChatMember(String studentNumber, String nickName, int position){
        this.studentNumber = studentNumber;
        this.nickName = nickName;
        this.position = position;
    }

    public String getStudentNumber() {
        return studentNumber;
    }

    public void setStudentNumber(String studentNumber) {
        this.studentNumber = studentNumber;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }
}
