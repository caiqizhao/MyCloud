package org.caiqizhao.entity;

public class FileCloud {
    private String file_name;
    private int file_tupian;
    private String file_data;
    public  UserFile userFile = null;
    public  Directory directory = null;

    @Override
    public String toString() {
        return "FileCloud{" +
                "file_name='" + file_name + '\'' +
                ", file_tupian=" + file_tupian +
                ", file_data='" + file_data + '\'' +
                '}';
    }

    public FileCloud(String file_name, int file_tupian, String file_data) {
        this.file_name = file_name;
        this.file_tupian = file_tupian;
        this.file_data = file_data;
    }

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public int getFile_tupian() {
        return file_tupian;
    }

    public void setFile_tupian(int file_tupian) {
        this.file_tupian = file_tupian;
    }

    public String getFile_data() {
        return file_data;
    }

    public void setFile_data(String file_data) {
        this.file_data = file_data;
    }
}
