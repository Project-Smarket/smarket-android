package org.techtown.smarket_android.User;

import java.io.Serializable;

public class Person implements Serializable {
    private String email;
    private String password;
    private String name;

    public Person(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }

    public String toString(){
        return String.format("이메일 : %s\n" + "비밀번호 : %s\n" + "이름 : %s", email, password, name);

    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }
}
