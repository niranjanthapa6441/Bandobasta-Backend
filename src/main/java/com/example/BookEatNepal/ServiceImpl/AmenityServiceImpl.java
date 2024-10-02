package com.example.BookEatNepal.ServiceImpl;

import com.example.BookEatNepal.DTO.AmenityDTO;
import com.example.BookEatNepal.DTO.AmenityDetail;
import com.example.BookEatNepal.Enums.AmenityStatus;
import com.example.BookEatNepal.Model.Amenity;
import com.example.BookEatNepal.Model.Venue;
import com.example.BookEatNepal.Repository.AmenityRepo;
import com.example.BookEatNepal.Repository.VenueRepo;
import com.example.BookEatNepal.Request.AmenityRequest;
import com.example.BookEatNepal.Service.AmenityService;
import com.example.BookEatNepal.Util.CustomException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.apache.commons.io.FilenameUtils;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class AmenityServiceImpl implements AmenityService {
    private static final String SUCCESS_MESSAGE = "successful";
    @Autowired
    private AmenityRepo amenityRepo;
    @Autowired
    private VenueRepo venueRepo;

    @Autowired
    private EntityManager entityManager;

    @Override
    public String save(AmenityRequest request, MultipartFile image) {
        Venue venue = getVenue(request.getVenueId());
        request.setImageUrl(getImagePath(image, venue.getVenueName(), request));
        amenityRepo.save(toAmenity(request, venue));
        return SUCCESS_MESSAGE;
    }

    @Override
    public String delete(int id) {
        Amenity amenity = amenityRepo.findById(id).orElseThrow(() -> new CustomException(CustomException.Type.AMENITY_NOT_FOUND));
        amenity.setStatus(AmenityStatus.DELETED);
        amenityRepo.save(amenity);
        return SUCCESS_MESSAGE;
    }

    @Override
    public AmenityDTO findAll(String venueId, int page, int size) {
        int id = Integer.parseInt(venueId);
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Amenity> query = cb.createQuery(Amenity.class);
        Root<Amenity> amenityRoot = query.from(Amenity.class);
        Join<Amenity, Venue> amenityVenueJoin = amenityRoot.join("venue");

        query.select(amenityRoot);
        List<Predicate> predicates = new ArrayList<>();

        if (id != 0) {
            predicates.add(cb.equal(amenityVenueJoin.get("id"), id));
        }
        query.where(predicates.toArray(new Predicate[0]));

        List<Amenity> amenities = entityManager.createQuery(query).getResultList();

        TypedQuery<Amenity> typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult((page - 1) * size);
        typedQuery.setMaxResults(size);

        List<Amenity> pagedAmenities = typedQuery.getResultList();

        int currentPage = page - 1;
        int totalElements = amenities.size();
        int totalPages = (int) Math.ceil((double) totalElements / size);
        return toHallDTO(pagedAmenities, currentPage, totalElements, totalPages);
    }

    @Override
    public AmenityDetail findById(int id) {
        Amenity amenity = amenityRepo.findById(id).orElseThrow(() -> new CustomException(CustomException.Type.AMENITY_NOT_FOUND));
        return toAmenityDetail(amenity);
    }

    @Override
    public String update(AmenityRequest request, int id, MultipartFile image) {
        Venue venue = getVenue(request.getVenueId());
        Amenity amenity = amenityRepo.findById(id).orElseThrow(() -> new CustomException(CustomException.Type.AMENITY_NOT_FOUND));
        amenity.setVenue(venue);
        amenity.setStatus(AmenityStatus.valueOf(request.getStatus()));
        amenity.setPrice(request.getPrice());
        amenity.setName(request.getName());
        amenity.setDescription(request.getDescription());
        amenity.setImageUrl(getImagePath(image, venue.getVenueName(), request));
        return SUCCESS_MESSAGE;
    }

    private String getImagePath(MultipartFile image, String venueName, AmenityRequest request) {
        validate(image);
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(image.getOriginalFilename()));
        if (fileName.contains(".php%00.")) {
            throw new CustomException(CustomException.Type.INVALID_FILE_EXTENSION);
        }
        return getImagePath(image, venueName, fileName);
    }

    private String getImagePath(MultipartFile multipartFile, String venueName, String fileName) {
        String uploadDirectory = "./images/venues/" + venueName.replaceAll("\\s", "") + "/amenities";
        Path path = Paths.get(uploadDirectory);
        Path filePath = path.resolve(fileName);
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        try (InputStream inputStream = multipartFile.getInputStream()) {
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return filePath.toString().replace("./", "/").trim();
    }

    private void validate(MultipartFile multipartFile) {
        if (multipartFile.getSize() > 3000000)
            throw new CustomException(CustomException.Type.INVALID_FILE_SIZE);
        String extension = FilenameUtils.getExtension(multipartFile.getOriginalFilename());
        if (!checkFileExtension(extension))
            throw new CustomException(CustomException.Type.INVALID_FILE_EXTENSION);
        checkMimeType(multipartFile);
        if (!checkMimeType(multipartFile))
            throw new CustomException(CustomException.Type.INVALID_MIME_TYPE);
    }

    private boolean checkFileExtension(String extension) {
        return (extension != null && (extension.equals("png") || extension.equals("jpeg") || extension.equals("jpg")));
    }

    private boolean checkMimeType(MultipartFile multipartFile) {
        Tika tika = new Tika();
        String mimeType;
        try {
            mimeType = tika.detect(multipartFile.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return (mimeType.equals("image/png") || mimeType.equals("image/jpg") || mimeType.equals("image/jpeg"));

    }

    private Amenity toAmenity(AmenityRequest request, Venue venue) {
        Amenity amenity = new Amenity();
        amenity.setDescription(request.getDescription());
        amenity.setPrice(request.getPrice());
        amenity.setName(request.getName());
        amenity.setVenue(venue);
        amenity.setStatus(AmenityStatus.valueOf(request.getStatus()));
        amenity.setImageUrl(request.getImageUrl());
        return amenity;
    }

    private Venue getVenue(String venueId) {
        return venueRepo.findById(Integer.valueOf(venueId)).orElseThrow(() -> new CustomException(CustomException.Type.VENUE_NOT_FOUND));
    }

    private List<AmenityDetail> toAmenityDetails(List<Amenity> amenities) {
        List<AmenityDetail> amenityDetails = new ArrayList<>();
        for (Amenity amenity : amenities
        ) {
            amenityDetails.add(toAmenityDetail(amenity));
        }
        return amenityDetails;
    }

    private AmenityDetail toAmenityDetail(Amenity amenity) {
        return AmenityDetail.builder()
                .name(amenity.getName())
                .venueId(String.valueOf(amenity.getVenue().getId()))
                .description(amenity.getDescription())
                .id(String.valueOf(amenity.getId()))
                .status(String.valueOf(amenity.getStatus()))
                .imageUrl(amenity.getImageUrl())
                .price(amenity.getPrice())
                .build();
    }

    private AmenityDTO toHallDTO(List<Amenity> amenities, int currentPage, int totalElements, int totalPages) {
        List<AmenityDetail> amenityDetails = toAmenityDetails(amenities);
        return AmenityDTO.builder()
                .amenityDetails(amenityDetails)
                .currentPage(currentPage)
                .totalPages(totalPages)
                .totalElements(totalElements)
                .build();
    }
}
