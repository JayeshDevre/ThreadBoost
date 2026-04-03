package com.cod.asyncmicroservice.domain;

public class FileData {
    private String data;
    
    public FileData() {}
    public FileData(String data) { this.data = data; }
    
    public String getData() { return data; }
    public void setData(String data) { this.data = data; }
}
