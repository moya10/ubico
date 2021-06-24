package com.example.lbma.Models;

public class UserModel {
    private int id;
    private String fullname;
    private String gender;
    private String age;
    private String e_mail;
    private int telephone;


    public UserModel(int id, String fullname, String gender, String age, String e_mail, int telephone) {
        this.id = id;
        this.fullname = fullname;
        this.gender = gender;
        this.age = age;
        this.e_mail = e_mail;
        this.telephone = telephone;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getE_mail() {
        return e_mail;
    }

    public void setE_mail(String e_mail) {
        this.e_mail = e_mail;
    }

    public int getTelephone() {
        return telephone;
    }

    public void setTelephone(int telephone) {
        this.telephone = telephone;
    }

    @Override
    public String toString() {
        return "UserModel{" +
                "id=" + id +
                ", fullname='" + fullname + '\'' +
                ", gender='" + gender + '\'' +
                ", age='" + age + '\'' +
                ", e_mail='" + e_mail + '\'' +
                ", telephone=" + telephone +
                '}';
    }
}
