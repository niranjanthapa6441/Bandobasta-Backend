package com.example.BookEatNepal.Service;

import com.amazonaws.AmazonClientException;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
public interface AWSService {

    String uploadFile(
            final String bucketName,
            final String keyName,
            final Long contentLength,
            final String contentType,
            final InputStream value
    ) throws AmazonClientException;

    ByteArrayOutputStream downloadFile(
            final String bucketName,
            final String keyName
    ) throws IOException, AmazonClientException;

    List<String> listFiles(final String bucketName) throws AmazonClientException;

    void deleteFile(
            final String bucketName,
            final String keyName
    ) throws AmazonClientException;
}
