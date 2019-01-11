package org.caiqizhao.entity;

import java.util.ArrayList;
import java.util.List;

public class Directory {
    private Integer dir_id;
    private String dir_name;
    private Integer dir_parent;
    private String user_name;

    public static List<Directory> user_dir_list = new ArrayList<Directory>();


    @Override
    public String toString() {
        return "Directory{" +
                "dir_id=" + dir_id +
                ", dir_name='" + dir_name + '\'' +
                ", dir_parent=" + dir_parent +
                ", user_name='" + user_name + '\'' +
                '}';
    }

    public Integer getDir_id() {
        return dir_id;
    }

    public void setDir_id(Integer dir_id) {
        this.dir_id = dir_id;
    }

    public String getDir_name() {
        return dir_name;
    }

    public void setDir_name(String dir_name) {
        this.dir_name = dir_name;
    }

    public Integer getDir_parent() {
        return dir_parent;
    }

    public void setDir_parent(Integer dir_parent) {
        this.dir_parent = dir_parent;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }
}
