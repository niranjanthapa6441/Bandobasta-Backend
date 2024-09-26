package com.example.BookEatNepal.ServiceImpl;

import com.example.BookEatNepal.DTO.VenueDetails;
import com.example.BookEatNepal.DTO.VenueDTO;
import com.example.BookEatNepal.Enums.VenueStatus;
import com.example.BookEatNepal.Model.Venue;
import com.example.BookEatNepal.Model.VenueImage;
import com.example.BookEatNepal.Repository.VenueImageRepo;
import com.example.BookEatNepal.Repository.VenueRepo;
import com.example.BookEatNepal.Request.VenueRequest;
import com.example.BookEatNepal.Service.VenueService;
import com.example.BookEatNepal.Util.CustomException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.apache.tika.Tika;

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
public class VenueServiceImpl implements VenueService {
    private static final String SUCCESS_MESSAGE = "successful";
    @Autowired
    private VenueRepo venueRepo;
    @Autowired
    private VenueImageRepo venueImageRepo;
    @Autowired
    private EntityManager entityManager;
    @Override
    public String save(VenueRequest request) {
        request.setLicenseImagePath(getLicenseImagePath(request));
        request.setPanImagePath(getPanImagePath(request));
        Venue venue = venueRepo.save(toVenue(request));
        saveVenueImages(request.getVenueImages(), venue);
        return SUCCESS_MESSAGE;
    }

    @Override
    public String delete(int id) {
        Venue venue = venueRepo.findById(id).orElseThrow(() -> new CustomException(CustomException.Type.VENUE_NOT_FOUND));
        venue.setStatus(VenueStatus.DELETED);
        venueRepo.save(venue);
        return SUCCESS_MESSAGE;
    }


    @Override
    public VenueDTO findAll(String venue, String location, int rating, int page, int size) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Venue> query = cb.createQuery(Venue.class);
        Root<Venue> venueRoot = query.from(Venue.class);

        List<Predicate> predicates = new ArrayList<>();

        if (venue != null && !venue.isEmpty()) {
            predicates.add(cb.like(venueRoot.get("venueName"), "%" + venue + "%"));
        }

        if (location != null && !location.isEmpty()) {
            predicates.add(cb.like(venueRoot.get("address"), "%" + location + "%"));
        }

        predicates.add(cb.isTrue(venueRoot.get("verified")));

        predicates.add(cb.equal(venueRoot.get("status"), VenueStatus.AVAILABLE));

        query.where(predicates.toArray(new Predicate[0]));

        List<Venue> venues = entityManager.createQuery(query).getResultList();

        TypedQuery<Venue> typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult((page - 1) * size);
        typedQuery.setMaxResults(size);

        List<Venue> pagedVenues = typedQuery.getResultList();

        int currentPage = page - 1;
        int totalElements = venues.size();
        int totalPages = (int) Math.ceil((double) totalElements / size);

        return toVenueDTO(pagedVenues, currentPage, totalElements, totalPages);
    }



    @Override
    public Venue findById(int id) {
        return venueRepo.findById(id).orElseThrow(() -> new CustomException(CustomException.Type.VENUE_NOT_FOUND));
    }

    @Override
    public String update(VenueRequest request, int id) {
        Venue venue = venueRepo.findById(id)
                .orElseThrow(() -> new CustomException(CustomException.Type.VENUE_NOT_FOUND));
        venue.setVenueName(request.getName());
        venue.setEmail(request.getEmail());
        venue.setPrimaryPhoneNumber(request.getPrimaryPhoneNumber());
        venue.setSecondaryPhoneNumber(request.getSecondaryPhoneNumber());
        venue.setDescription(request.getDescription());
        venue.setCountryCode(request.getCountryCode());
        venue.setRegistrationNumber(request.getRegistrationNumber());
        venue.setLicenseNumber(request.getLicenseNumber());
        venue.setPanImagePath(getPanImagePath(request));
        venue.setLicenseImagePath(getLicenseImagePath(request));
        venue.setAddress(toAddress(request));
        venueRepo.save(venue);
        return null;
    }

    private Venue toVenue(VenueRequest request) {
        Venue venue = new Venue();
        venue.setVenueName(request.getName());
        venue.setEmail(request.getEmail());
        venue.setPrimaryPhoneNumber(request.getPrimaryPhoneNumber());
        venue.setSecondaryPhoneNumber(request.getSecondaryPhoneNumber());
        venue.setDescription(request.getDescription());
        venue.setCountryCode(request.getCountryCode());
        venue.setRegistrationNumber(request.getRegistrationNumber());
        venue.setLicenseNumber(request.getLicenseNumber());
        venue.setVerified(false);
        venue.setPanImagePath(request.getPanImagePath());
        venue.setLicenseImagePath(request.getLicenseImagePath());
        venue.setStatus(VenueStatus.PENDING_APPROVAL);
        venue.setPermanentAccountNumber(request.getPermanentAccountNumber());
        venue.setAddress(toAddress(request));
        return venue;
    }

    private String toAddress(VenueRequest request) {
        return request.getStreetNumber() + "-" +
                request.getStreetName() + "," +
                request.getCity() + "," +
                request.getDistrict() +
                request.getState() +
                request.getCountry();
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

    private String getImagePath(MultipartFile multipartFile, String venueName, String fileName) {
        String uploadDirectory = "./images/venues/" + venueName.replaceAll("\\s", "");
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

    private void saveVenueImages(List<MultipartFile> venueImages, Venue venue) {
        for (MultipartFile image : venueImages) {
            try {
                validate(image);
                String fileName = StringUtils.cleanPath(Objects.requireNonNull(image.getOriginalFilename()));

                if (fileName.contains(".php%00.")) {
                    throw new CustomException(CustomException.Type.INVALID_FILE_EXTENSION);
                }

                VenueImage venueImage = new VenueImage();
                venueImage.setVenue(venue);
                venueImage.setImageUrl(getImagePath(image, venue.getVenueName(), fileName));

                venueImageRepo.save(venueImage);
            } catch (CustomException e) {
                System.err.println("Error processing image: " + e.getMessage());
            } catch (Exception e) {
                System.err.println("Unexpected error: " + e.getMessage());
            }
        }
    }

    private String getLicenseImagePath(VenueRequest request) {
        validate(request.getLicenseImage());
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(request.getLicenseImage().getOriginalFilename()));
        if (fileName.contains(".php%00.")) {
            throw new CustomException(CustomException.Type.INVALID_FILE_EXTENSION);
        }
        return getImagePath(request.getLicenseImage(), request.getName(), fileName);
    }

    private String getPanImagePath(VenueRequest request) {
        validate(request.getPanImage());
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(request.getPanImage().getOriginalFilename()));
        if (fileName.contains(".php%00.")) {
            throw new CustomException(CustomException.Type.INVALID_FILE_EXTENSION);
        }
        return getImagePath(request.getPanImage(), request.getName(), fileName);
    }
    private VenueDTO toVenueDTO(List<Venue> venues, int currentPage, int totalElements, int totalPages) {
        List<VenueDetails> venueDetails= toVenueDetails(venues);
        return VenueDTO.builder()
                .venues(venueDetails)
                .currentPage(currentPage)
                .totalPages(totalPages)
                .totalElements(totalElements)
                .build();
    }
    private List<VenueDetails> toVenueDetails(List<Venue> venues) {
        List<VenueDetails> venueDTOs= new ArrayList<>();
        for (Venue venue:venues
        ) {
            venueDTOs.add(VenueDetails.builder()
                    .name(venue.getVenueName())
                    .address(venue.getAddress())
                    .description(venue.getDescription())
                    .status(String.valueOf(venue.getStatus()))
                    .venueImagePaths(getVenueImagePath(venue.getImages()))
                    .build())
            ;
        }
        return venueDTOs;
    }

    private List<String> getVenueImagePath(List<VenueImage> images) {
        List<String> venueImagePaths = new ArrayList<>();
        for (VenueImage image: images){
            venueImagePaths.add(image.getImageUrl());
        }
        return venueImagePaths;
    }
}
