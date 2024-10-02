package com.example.BookEatNepal.Service;

import com.example.BookEatNepal.DTO.PackageDTO;
import com.example.BookEatNepal.DTO.PackageDetail;
import com.example.BookEatNepal.Request.PackageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@Service
public interface PackageService {
        String save(PackageRequest request);
        String delete(int id);
        PackageDTO findPackageByVenue(String venueId,String packageType, String eventType, int page, int size);
        PackageDetail findById(int id);
        String update(PackageRequest request, int id );
}
