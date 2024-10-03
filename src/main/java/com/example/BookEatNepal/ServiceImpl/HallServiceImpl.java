package com.example.BookEatNepal.ServiceImpl;

import com.example.BookEatNepal.DTO.*;
import com.example.BookEatNepal.Enums.HallStatus;
import com.example.BookEatNepal.Model.*;
import com.example.BookEatNepal.Repository.HallAvailabilityRepo;
import com.example.BookEatNepal.Repository.HallImageRepo;
import com.example.BookEatNepal.Repository.HallRepo;
import com.example.BookEatNepal.Repository.VenueRepo;
import com.example.BookEatNepal.Request.HallAvailabilityRequest;
import com.example.BookEatNepal.Request.HallRequest;
import com.example.BookEatNepal.Service.HallService;
import com.example.BookEatNepal.Util.Formatter;
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
import java.sql.Time;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class HallServiceImpl implements HallService {
    private static final String SUCCESS_MESSAGE = "successful";
    @Autowired
    private HallRepo hallRepo;

    @Autowired
    private VenueRepo venueRepo;

    @Autowired
    private HallImageRepo hallImageRepo;

    @Autowired
    private HallAvailabilityRepo hallAvailabilityRepo;

    @Autowired
    private EntityManager entityManager;

    @Override
    public String save(HallRequest request, List<MultipartFile> hallImages) {
        Venue venue = getVenue(request.getVenueId());
        Hall hall = hallRepo.save(toHall(request, venue));
        saveHallImages(hallImages, hall, venue.getVenueName());
        return SUCCESS_MESSAGE;
    }

    @Override
    public String delete(int id) {
        Hall hall = hallRepo.findById(id).orElseThrow(() -> new CustomException(CustomException.Type.HALL_NOT_FOUND));
        hall.setStatus(HallStatus.DELETED);
        hallRepo.save(hall);
        return SUCCESS_MESSAGE;
    }

    @Override
    public HallDTO findAll(String venueId, int page, int size) {
        int id = Integer.parseInt(venueId);
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Hall> query = cb.createQuery(Hall.class);
        Root<Hall> hallRoot = query.from(Hall.class);
        Join<Hall, Venue> hallVenueJoin = hallRoot.join("venue");

        query.select(hallRoot);
        List<Predicate> predicates = new ArrayList<>();

        if (id != 0) {
            predicates.add(cb.equal(hallVenueJoin.get("id"), id));
        }
        query.where(predicates.toArray(new Predicate[0]));

        List<Hall> halls = entityManager.createQuery(query).getResultList();

        TypedQuery<Hall> typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult((page - 1) * size);
        typedQuery.setMaxResults(size);

        List<Hall> pagedHalls = typedQuery.getResultList();

        int currentPage = page - 1;
        int totalElements = halls.size();
        int totalPages = (int) Math.ceil((double) totalElements / size);
        return toHallDTO(pagedHalls, currentPage, totalElements, totalPages);
    }

    @Override
    public HallDetail findById(int id) {
        Hall hall = hallRepo.findById(id).orElseThrow(() -> new CustomException(CustomException.Type.HALL_NOT_FOUND));
        return toHallDetail(hall);
    }

    @Override
    public String update(HallRequest request, int id, List<MultipartFile> hallImages) {
        Hall hall = hallRepo.findById(id)
                .orElseThrow(() -> new CustomException(CustomException.Type.HALL_NOT_FOUND));
        hall.setVenue(getVenue(request.getVenueId()));
        hall.setDescription(request.getDescription());
        hall.setName(request.getName());
        hall.setCapacity(request.getCapacity());
        hall.setStatus(HallStatus.valueOf(request.getStatus()));
        hall.setPrice(request.getPrice());
        hall.setFloorNumber(request.getFloorNumber());
        hallRepo.save(hall);
        return SUCCESS_MESSAGE;
    }

    @Override
    public String saveHallAvailability(List<HallAvailabilityRequest> requests) {
        for (HallAvailabilityRequest request: requests
             ) {
            Hall hall = hallRepo.findById(Integer.parseInt(request.getHallId()))
                    .orElseThrow(() -> new CustomException(CustomException.Type.HALL_NOT_FOUND));
            hallAvailabilityRepo.save(convertToHallAvailability(request,hall));
        }
        return SUCCESS_MESSAGE;
    }

    @Override
    public HallAvailabilityDTO checkAvailability(String venueId, LocalDate date, String startTime, String endTime, int numberOfGuests, int page, int size) {
        int id = Integer.parseInt(venueId);
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<HallAvailability> query = cb.createQuery(HallAvailability.class);
        Root<HallAvailability> hallAvailabilityRoot = query.from(HallAvailability.class);
        Join<HallAvailability, Hall> hallAvailabilityJoin = hallAvailabilityRoot.join("hall");
        Join<Hall, Venue> hallVenueJoin = hallAvailabilityJoin.join("venue");

        query.select(hallAvailabilityRoot);
        List<Predicate> predicates = new ArrayList<>();

        if (id != 0) {
            predicates.add(cb.equal(hallVenueJoin.get("id"), id));
        }

        if (date != null) {
            predicates.add(cb.equal(hallAvailabilityRoot.get("date"), date));
        }

        if (startTime != null && endTime != null) {
            Predicate noOverlap = cb.or(
                    cb.lessThanOrEqualTo(hallAvailabilityRoot.get("endTime"), Formatter.getTimeFromString(startTime)),
                    cb.greaterThanOrEqualTo(hallAvailabilityRoot.get("startTime"), Formatter.getTimeFromString(endTime))
            );
            predicates.add(noOverlap);
        }

        if (numberOfGuests > 0) {
            predicates.add(cb.greaterThanOrEqualTo(hallAvailabilityJoin.get("capacity"), numberOfGuests));
        }

        predicates.add(cb.equal(hallAvailabilityRoot.get("status"), HallStatus.AVAILABLE));

        List<HallAvailability> hallAvailabilities = entityManager.createQuery(query).getResultList();

        TypedQuery<HallAvailability> typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult((page - 1) * size);
        typedQuery.setMaxResults(size);

        List<HallAvailability> pagedHallAvailabilities = typedQuery.getResultList();

        int currentPage = page - 1;
        int totalElements = hallAvailabilities.size();
        int totalPages = (int) Math.ceil((double) totalElements / size);
        return toHallAvailabilityDTO(pagedHallAvailabilities, currentPage, totalElements, totalPages);
    }

    private HallAvailabilityDTO toHallAvailabilityDTO(List<HallAvailability> pagedHallAvailabilities, int currentPage, int totalElements, int totalPages) {
        List<HallAvailabilityDetail> hallAvailabilityDetails = toHallAvailabilityDetails(pagedHallAvailabilities);
        return HallAvailabilityDTO.builder()
                .hallAvailabilityDetails(hallAvailabilityDetails)
                .currentPage(currentPage)
                .totalPages(totalPages)
                .totalElements(totalElements)
                .build();
    }

    private List<HallAvailabilityDetail> toHallAvailabilityDetails(List<HallAvailability> hallAvailabilities) {
        List<HallAvailabilityDetail> hallAvailabilityDetails = new ArrayList<>();
        for (HallAvailability hallAvailability: hallAvailabilities
             ) {
            hallAvailabilityDetails.add(toHallAvailabilityDetail(hallAvailability));
        }
        return hallAvailabilityDetails;
    }

    private HallAvailabilityDetail toHallAvailabilityDetail(HallAvailability hallAvailability) {
        return HallAvailabilityDetail.builder()
                .id(String.valueOf(hallAvailability.getId()))
                .hallName(hallAvailability.getHall().getName())
                .hallId(String.valueOf(hallAvailability.getHall().getId()))
                .description(String.valueOf(hallAvailability.getHall().getDescription()))
                .capacity(hallAvailability.getHall().getCapacity())
                .status(String.valueOf(hallAvailability.getStatus()))
                .date(Formatter.convertDateToStr(hallAvailability.getDate(),"yyyy-MM-dd"))
                .endTime(Formatter.getStringFromTime(hallAvailability.getEndTime()))
                .startTime(Formatter.getStringFromTime(hallAvailability.getStartTime()))
                .build();
    }


    private HallAvailability convertToHallAvailability(HallAvailabilityRequest request, Hall hall){
        HallAvailability hallAvailability= new HallAvailability();
        hallAvailability.setHall(hall);
        hallAvailability.setStatus(HallStatus.valueOf(request.getStatus()));
        hallAvailability.setDate(Formatter.convertStrToDate(request.getDate(),"yyyy-MM-dd"));
        hallAvailability.setStartTime(Formatter.getTimeFromString(request.getStartTime()));
        hallAvailability.setEndTime(Formatter.getTimeFromString(request.getEndTime()));
        return hallAvailability;
    }

    private void saveHallImages(List<MultipartFile> hallImages, Hall hall, String venueName) {
        for (MultipartFile image : hallImages) {
            try {
                validate(image);
                String fileName = StringUtils.cleanPath(Objects.requireNonNull(image.getOriginalFilename()));

                if (fileName.contains(".php%00.")) {
                    throw new CustomException(CustomException.Type.INVALID_FILE_EXTENSION);
                }

                HallImage hallImage = new HallImage();
                hallImage.setHall(hall);
                hallImage.setImageUrl(getImagePath(image, venueName, hall.getName(), fileName));

                hallImageRepo.save(hallImage);
            } catch (CustomException e) {
                System.err.println("Error processing image: " + e.getMessage());
            } catch (Exception e) {
                System.err.println("Unexpected error: " + e.getMessage());
            }
        }
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

    private String getImagePath(MultipartFile multipartFile, String venueName, String hallName, String fileName) {
        String uploadDirectory = "./images/venues/" + venueName.replaceAll("\\s", "") + "/" + hallName.replaceAll("\\s", "");
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

    private Hall toHall(HallRequest request, Venue venue) {
        Hall hall = new Hall();
        hall.setDescription(request.getDescription());
        hall.setCapacity(request.getCapacity());
        hall.setVenue(venue);
        hall.setFloorNumber(request.getFloorNumber());
        hall.setName(request.getName());
        hall.setPrice(request.getPrice());
        hall.setStatus(HallStatus.AVAILABLE);
        return hall;
    }

    private Venue getVenue(String venueId) {
        return venueRepo.findById(Integer.valueOf(venueId)).orElseThrow(() -> new CustomException(CustomException.Type.VENUE_NOT_FOUND));
    }

    private HallDTO toHallDTO(List<Hall> halls, int currentPage, int totalElements, int totalPages) {
        List<HallDetail> hallDetails = toHallDetails(halls);
        return HallDTO.builder()
                .hallDetails(hallDetails)
                .currentPage(currentPage)
                .totalPages(totalPages)
                .totalElements(totalElements)
                .build();
    }

    private List<HallDetail> toHallDetails(List<Hall> halls) {
        List<HallDetail> hallDetails = new ArrayList<>();
        for (Hall hall : halls
        ) {
            hallDetails.add(toHallDetail(hall));
        }
        return hallDetails;
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

    private HallDetail toHallDetail(Hall hall) {
        return HallDetail.builder()
                .name(hall.getName())
                .id(hall.getId())
                .venueId(String.valueOf(hall.getVenue().getId()))
                .capacity(hall.getCapacity())
                .floorNumber(hall.getFloorNumber())
                .description(hall.getDescription().toString())
                .status(String.valueOf(hall.getStatus()))
                .hallImagePaths(getHallImagePaths(hall.getId()))
                .build();
    }
}
