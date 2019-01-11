package org.caiqizhao.entity;

import java.util.ArrayList;

public class UserFile {
   private String file_name;
   private String user_name;
   private String file_date;
   private String file_size;
   private String file_hash;
   private Integer dir_id;

   public static ArrayList<UserFile> user_file_list = new ArrayList<UserFile>();

    @Override
    public String toString() {
        return "UserFile{" +
                "file_name='" + file_name + '\'' +
                ", user_name='" + user_name + '\'' +
                ", file_date='" + file_date + '\'' +
                ", file_size='" + file_size + '\'' +
                ", file_hash='" + file_hash + '\'' +
                ", dir_id=" + dir_id +
                '}';
    }

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getFile_date() {
        return file_date;
    }

    public void setFile_date(String file_date) {
        this.file_date = file_date;
    }

    public String getFile_size() {
        return file_size;
    }

    public void setFile_size(String file_size) {
        this.file_size = file_size;
    }

    public String getFile_hash() {
        return file_hash;
    }

    public void setFile_hash(String file_hash) {
        this.file_hash = file_hash;
    }

    public Integer getDir_id() {
        return dir_id;
    }

    public void setDir_id(Integer dir_id) {
        this.dir_id = dir_id;
    }
}
