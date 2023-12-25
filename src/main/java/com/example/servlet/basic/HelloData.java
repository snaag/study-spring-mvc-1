package com.example.servlet.basic;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class HelloData {
    private String username;
    private int age;

    // getter, setter 사용하면 아래 코드 사용하지 않아도 됨
    /**
     *     public String getUsername() {
     *         return username;
     *     }
     *
     *     public void setUsername(String username) {
     *         this.username = username;
     *     }
     *
     *     public int getAge() {
     *         return age;
     *     }
     *
     *     public void setAge(int age) {
     *         this.age = age;
     *     }
     */
}
