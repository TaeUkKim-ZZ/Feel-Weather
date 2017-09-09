package com.feel.weather;

public class Info {

    int id;
    String name;
    String age;
    String gender;
    String introduce;
    String userfeel;
    String macaddress;

    public Info() {

    }

    public Info(int id, String name, String age, String gender, String introduce, String userfeel, String macaddress) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.introduce = introduce;
        this.userfeel = userfeel;
        this.macaddress = macaddress;
    }

    public Info(String name, String age, String gender, String introduce, String userfeel, String macaddress) {
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.introduce = introduce;
        this.userfeel = userfeel;
        this.macaddress = macaddress;
    }

    public int getID() {
        return this.id;
    }

    public void setID(int id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return this.age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getGender() {
        return this.gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getIntroduce() {
        return this.introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public String getFeel() {
        return this.userfeel;
    }

    public void setFeel(String userfeel) {
        this.userfeel = userfeel;
    }

    public String getMacaddress() { return this.macaddress; }

    public void setMacaddress(String macaddress) { this.macaddress = macaddress; }
}
