package org.caiqizhao.entity;

/**
 * Created by caiqizhao on 2018/12/23.
 */


/**
 * 用户实体类
 */
public class User {


    private String user_name;

    private String user_password;

    private String is_user;

    private String user_size;

    private String file_size;

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static User user = null;

    public String getIs_user() {
        return is_user;
    }

    public void setIs_user(String is_user) {
        this.is_user = is_user;
    }


    @Override
    public String toString() {
        return "User{" +
                "user_name='" + user_name + '\'' +
                ", user_password='" + user_password + '\'' +
                ", is_user='" + is_user + '\'' +
                ", user_size='" + user_size + '\'' +
                ", file_size='" + file_size + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    public String getUser_size() {
        return user_size;
    }

    public void setUser_size(String user_size) {
        this.user_size = user_size;
    }

    public String getFile_size() {
        return file_size;
    }

    public void setFile_size(String file_size) {
        this.file_size = file_size;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_password() {
        return user_password;
    }

    public void setUser_password(String user_password) {
        this.user_password = user_password;
    }
}
