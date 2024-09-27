package com.example.BookEatNepal.ServiceImpl;

import com.example.BookEatNepal.DTO.HallDetails;
import com.example.BookEatNepal.DTO.VenueDetails;
import com.example.BookEatNepal.DTO.VenueDTO;
import com.example.BookEatNepal.Enums.VenueStatus;
import com.example.BookEatNepal.Model.*;
import com.example.BookEatNepal.Repository.*;
import com.example.BookEatNepal.Request.VenueRequest;
import com.example.BookEatNepal.Service.HallService;
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
    private AppUserRepo appUserRepo;

    @Autowired
    private HallRepo hallRepo;
    @Autowired
    private HallImageRepo hallImageRepo;

    @Autowired
    private EntityManager entityManager;

    @Override
    public String save(VenueRequest request, List<MultipartFile> venueImages, MultipartFile licenseImage, MultipartFile panImage) {
        request.setLicenseImagePath(getImagePath(licenseImage, request));
        request.setPanImagePath(getImagePath(panImage, request));
        AppUser owner = getOwner(request.getOwnerId());
        Venue venue = venueRepo.save(toVenue(request, owner));
        saveVenueImages(venueImages, venue);
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
    public VenueDTO findAll(String venue, String location, double rating, int page, int size) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Venue> query = cb.createQuery(Venue.class);
        Root<Venue> venueRoot = query.from(Venue.class);

        List<Predicate> predicates = new ArrayList<>();

        if (venue != null && !venue.isEmpty()) {
            predicates.add(cb.like(cb.lower(venueRoot.get("venueName")), "%" + venue.toLowerCase() + "%"));
        }

        if (location != null && !location.isEmpty()) {
            predicates.add(cb.like(cb.lower(venueRoot.get("address")), "%" + location.toLowerCase() + "%"));
        }

        predicates.add(cb.isTrue(venueRoot.get("isVerified")));

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
    public String update(VenueRequest request, int id, MultipartFile licenseImage, MultipartFile panImage) {
        Venue venue = venueRepo.findById(id)
                .orElseThrow(() -> new CustomException(CustomException.Type.VENUE_NOT_FOUND));
        venue.setAppUser(getOwner(request.getOwnerId()));
        venue.setVenueName(request.getName());
        venue.setEmail(request.getEmail());
        venue.setPrimaryPhoneNumber(request.getPrimaryPhoneNumber());
        venue.setSecondaryPhoneNumber(request.getSecondaryPhoneNumber());
        venue.setDescription(request.getDescription());
        venue.setCountryCode(request.getCountryCode());
        venue.setRegistrationNumber(request.getRegistrationNumber());
        venue.setLicenseNumber(request.getLicenseNumber());
        venue.setPanImagePath(getImagePath(panImage, request));
        venue.setLicenseImagePath(getImagePath(licenseImage, request));
        venue.setAddress(toAddress(request));
        venueRepo.save(venue);
        return SUCCESS_MESSAGE;
    }

    private Venue toVenue(VenueRequest request, AppUser owner) {
        Venue venue = new Venue();
        venue.setVenueName(request.getName());
        venue.setEmail(request.getEmail());
        venue.setAppUser(owner);
        venue.setPrimaryPhoneNumber(request.getPrimaryPhoneNumber());
        venue.setSecondaryPhoneNumber(request.getSecondaryPhoneNumber());
        venue.setDescription(request.getDescription());
        venue.setCountryCode(request.getCountryCode());
        venue.setRegistrationNumber(request.getRegistrationNumber());
        venue.setLicenseNumber(request.getLicenseNumber());
        venue.setVerified(true);
        venue.setPanImagePath(request.getPanImagePath());
        venue.setLicenseImagePath(request.getLicenseImagePath());
        venue.setStatus(VenueStatus.AVAILABLE);
        venue.setPermanentAccountNumber(request.getPermanentAccountNumber());
        venue.setAddress(toAddress(request));
        return venue;
    }

    private String toAddress(VenueRequest request) {
        return request.getStreetNumber() + "-" +
                request.getStreetName() + "," +
                request.getCity() + "," +
                request.getDistrict() + "," +
                request.getState() + "," +
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

    private String getImagePath(MultipartFile image, VenueRequest request) {
        validate(image);
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(image.getOriginalFilename()));
        if (fileName.contains(".php%00.")) {
            throw new CustomException(CustomException.Type.INVALID_FILE_EXTENSION);
        }
        return getImagePath(image, request.getName(), fileName);
    }

    private VenueDTO toVenueDTO(List<Venue> venues, int currentPage, int totalElements, int totalPages) {
        List<VenueDetails> venueDetails = toVenueDetails(venues);
        return VenueDTO.builder()
                .venues(venueDetails)
                .currentPage(currentPage)
                .totalPages(totalPages)
                .totalElements(totalElements)
                .build();
    }

    private List<VenueDetails> toVenueDetails(List<Venue> venues) {
        List<VenueDetails> venueDetails = new ArrayList<>();
        for (Venue venue : venues
        ) {
            venueDetails.add(VenueDetails.builder()
                    .name(venue.getVenueName())
                    .address(venue.getAddress())
                    .description(venue.getDescription().toString())
                    .status(String.valueOf(venue.getStatus()))
                    .venueImagePaths(getVenueImagePath(venue.getId()))
                    .hallDetails(getHallDetails(venue.getId()))
                    .build())
            ;
        }
        return venueDetails;
    }

    private List<HallDetails> getHallDetails(int id) {
        List<Hall> halls= hallRepo.findByVenueId(id);
        return toHallDetails(halls);
    }

    private List<HallDetails> toHallDetails(List<Hall> halls) {
            List<HallDetails> hallDetails = new ArrayList<>();
            for (Hall hall : halls
            ) {
                hallDetails.add(toHallDetail(hall));
            }
            return hallDetails;
        }
    private HallDetails toHallDetail(Hall hall) {
        return HallDetails.builder()
                .name(hall.getName())
                .capacity(hall.getCapacity())
                .floorNumber(hall.getFloorNumber())
                .description(hall.getDescription().toString())
                .status(String.valueOf(hall.getStatus()))
                .hallImagePaths(getHallImagePaths(hall.getId()))
                .build();
    }

    private List<String> getVenueImagePath(int id) {
        List<String> venueImagePaths = new ArrayList<>();
        List<VenueImage> venueImages = venueImageRepo.findByVenueId(id);
        for (VenueImage image : venueImages
        ) {
            venueImagePaths.add(image.getImageUrl());
        }
        return venueImagePaths;
    }

    private AppUser getOwner(String ownerId) {
        return appUserRepo.findById(Integer.valueOf(ownerId)).orElseThrow(() -> new CustomException(CustomException.Type.USER_NOT_FOUND));
    }
    private List<String> getHallImagePaths(int id) {
        List<String> hallImagePaths = new ArrayList<>();
        List<HallImage> hallImages = hallImageRepo.findByHallId(id);
        for (HallImage image : hallImages
        ) {
            hallImagePaths.add(image.getImageUrl());
        }
        return hallImagePaths;
    }
}
