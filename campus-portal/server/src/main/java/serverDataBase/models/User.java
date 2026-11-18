package serverDataBase.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import logic.enums.UserType;

import java.time.LocalDateTime;
import java.util.*;
import java.util.List;

@Entity
@Table(name="portal_user")
public class User{
    @Id
    @GeneratedValue
    @Column(name="id",nullable = false)
    private int id;

    @OneToOne(mappedBy = "user")
    private Student student;

    @OneToOne(mappedBy = "user")
    private Master master;

    @Column(name="username",nullable = false,unique = true)
    String username;

    @Column(name="userFullName",nullable = false)
    String userFullName;

    @Column(name="userPassword")
    String userPassword="";

    @Column(name="userType",nullable = false)
    @Enumerated(EnumType.ORDINAL)
    UserType userType;

    @Column(name="userLastLoginTime",nullable = false)
    LocalDateTime userLastLoginTime;


    @Column(name="userImage")   //default
    @Lob
    byte[] userImage;

    @Column(name="userEmail")
    String userEmail="";

    @Column(name="canLogin")
    boolean canLogin=true;

    @Column(name="userPhoneNumber")
    String userPhoneNumber="";

    @Column(name="userNationalNumber")
    String userNationalNumber="";


    @ManyToMany
    @JoinTable(
            name = "User_Chat",
            joinColumns = { @JoinColumn(name = "id") },
            inverseJoinColumns = { @JoinColumn(name = "chatId") }
    )
    List<Chat> userChats = new ArrayList<>();


    @OneToMany
    @JoinColumn(name = "responsibleUserId")
    private List<UniRequest> InUniRequests= new ArrayList<>();

    @OneToMany
    @JoinColumn(name = "requesterUserId")
    private List<UniRequest> OutUniRequests= new ArrayList<>();


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Master getMaster() {
        return master;
    }

    public void setMaster(Master master) {
        this.master = master;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public LocalDateTime getUserLastLoginTime() {
        return userLastLoginTime;
    }

    public void setUserLastLoginTime(LocalDateTime userLastLoginTime) {
        this.userLastLoginTime = userLastLoginTime;
    }

    public byte[] getUserImage() {
        return userImage;
    }

    public void setUserImage(byte[] userImage) {
        this.userImage = userImage;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public boolean isCanLogin() {
        return canLogin;
    }

    public void setCanLogin(boolean canLogin) {
        this.canLogin = canLogin;
    }

    public String getUserPhoneNumber() {
        return userPhoneNumber;
    }

    public void setUserPhoneNumber(String userPhoneNumber) {
        this.userPhoneNumber = userPhoneNumber;
    }

    public String getUserNationalNumber() {
        return userNationalNumber;
    }

    public void setUserNationalNumber(String userNationalNumber) {
        this.userNationalNumber = userNationalNumber;
    }

    public List<Chat> getUserChats() {
        return (List<Chat>) userChats;
    }

    public void setUserChats(List<Chat> userChats) {
        this.userChats = userChats;
    }

    public List<UniRequest> getInUniRequests() {
        return InUniRequests;
    }

    public void setInUniRequests(List<UniRequest> inUniRequests) {
        InUniRequests = inUniRequests;
    }

    public List<UniRequest> getOutUniRequests() {
        return OutUniRequests;
    }

    public void setOutUniRequests(List<UniRequest> outUniRequests) {
        OutUniRequests = outUniRequests;
    }
}
