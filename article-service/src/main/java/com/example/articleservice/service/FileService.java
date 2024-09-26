package com.example.articleservice.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

//为了更好的扩展，实现多态，定义此接口，后续接收实现类对象
public interface FileService {

    public String Upload(MultipartFile filedata);

    public void deleteImage(String imageUrl);

    public List<String> getAutocompleteSuggestions(String searchText);

    public List<String> SplitIntoWords(String text);

}
