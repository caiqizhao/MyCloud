package org.caiqizhao.entity;

public class FileHash {
    private String file_hash;
    private Integer file_count;


    @Override
    public String toString() {
        return "FileHash{" +
                "file_hash='" + file_hash + '\'' +
                ", file_count=" + file_count +
                '}';
    }

    public String getFile_hash() {
        return file_hash;
    }

    public void setFile_hash(String file_hash) {
        this.file_hash = file_hash;
    }

    public Integer getFile_count() {
        return file_count;
    }

    public void setFile_count(Integer file_count) {
        this.file_count = file_count;
    }
}
