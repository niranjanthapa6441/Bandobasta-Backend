package com.example.BookEatNepal.ServiceImpl;

import com.example.BookEatNepal.Enums.HallShift;
import com.example.BookEatNepal.Enums.HallStatus;
import com.example.BookEatNepal.Model.*;
import com.example.BookEatNepal.Payload.DTO.HallAvailabilityDTO;
import com.example.BookEatNepal.Payload.DTO.HallAvailabilityDetail;
import com.example.BookEatNepal.Payload.DTO.HallDTO;
import com.example.BookEatNepal.Payload.DTO.HallDetail;
import com.example.BookEatNepal.Repository.HallAvailabilityRepo;
import com.example.BookEatNepal.Repository.HallImageRepo;
import com.example.BookEatNepal.Repository.HallRepo;
import com.example.BookEatNepal.Repository.VenueRepo;
import com.example.BookEatNepal.Payload.Request.HallAvailabilityRequest;
import com.example.BookEatNepal.Payload.Request.HallRequest;
import com.example.BookEatNepal.Service.AWSService;
import com.example.BookEatNepal.Service.HallService;
import com.example.BookEatNepal.Util.Formatter;
import com.example.BookEatNepal.Util.CustomException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import jakarta.transaction.Transactional;
import org.apache.commons.io.FilenameUtils;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class HallServiceImpl implements HallService {
    private static final String SUCCESS_MESSAGE = "successful";
    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;
    @Autowired
    private HallRepo hallRepo;

    @Autowired
    private VenueRepo venueRepo;

    @Autowired
    private HallImageRepo hallImageRepo;

    @Autowired
    private AWSService awsService;

    @Autowired
    private HallAvailabilityRepo hallAvailabilityRepo;

    @Autowired
    private EntityManager entityManager;

    @Override
    @Transactional
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
    public HallDTO findAll(String venueId,int numberOfGuests, int page, int size, String checkAvailableDate) {
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

        if (checkAvailableDate != null && !checkAvailableDate.isEmpty()) {
            Subquery<Long> hallAvailabilitySubquery = query.subquery(Long.class);
            Root<HallAvailability> hallAvailabilityRoot = hallAvailabilitySubquery.from(HallAvailability.class);
            Join<HallAvailability, Hall> hallJoin = hallAvailabilityRoot.join("hall");

            hallAvailabilitySubquery.select(hallJoin.get("id")) // Select hall IDs that match the criteria
                    .where(
                            cb.equal(hallAvailabilityRoot.get("date"), Formatter.convertStrToDate(checkAvailableDate, "yyyy-MM-dd")), // Match the date
                            cb.equal(hallAvailabilityRoot.get("status"), HallStatus.AVAILABLE) // Ensure the hall is available
                    );

            predicates.add(cb.in(hallRoot.get("id")).value(hallAvailabilitySubquery)); // Check if the hall's ID is in the subquery result
        }

        if (numberOfGuests != 0){
            predicates.add(cb.greaterThanOrEqualTo(hallRoot.get("capacity"), numberOfGuests));
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
    @Transactional
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

    //Changes Done here
    @Override
    public HallAvailabilityDTO checkAvailability(String venueId, int hallId, String date, String shift, int numberOfGuests, int page, int size) {
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

        if (hallId!= 0) {
            predicates.add(cb.equal(hallAvailabilityJoin.get("id"), hallId));
        }

        if (date != null) {
            predicates.add(cb.equal(hallAvailabilityRoot.get("date"), Formatter.convertStrToDate(date,"yyyy-MM-dd")));
        }

        if (shift != null) {
            predicates.add(cb.equal(hallAvailabilityRoot.get("shift"), shift));
        }


        if (numberOfGuests > 0) {
            predicates.add(cb.greaterThanOrEqualTo(hallAvailabilityJoin.get("capacity"), numberOfGuests));
        }
        predicates.add(cb.equal(hallAvailabilityRoot.get("status"), HallStatus.AVAILABLE));

        query.where(predicates.toArray(new Predicate[0]));

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

    public String updateHallAvailability(String shift,String status,LocalDate date)
    {
        HallAvailability hallAvailability = hallAvailabilityRepo.findAvailableHallByDateAndShift(date,HallShift.valueOf(shift))
                .orElseThrow(()-> new CustomException(CustomException.Type.HALL_AVAILABILITY_NOT_FOUND));
        hallAvailability.setShift(HallShift.valueOf(shift));
        hallAvailability.setStatus(HallStatus.valueOf(status));
        hallAvailabilityRepo.save(hallAvailability);
        return SUCCESS_MESSAGE;
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
                .venueName(hallAvailability.getHall().getVenue().getVenueName())
                .hallName(hallAvailability.getHall().getName())
                .hallId(String.valueOf(hallAvailability.getHall().getId()))
                .description(String.valueOf(hallAvailability.getHall().getDescription()))
                .capacity(hallAvailability.getHall().getCapacity())
                .status(String.valueOf(hallAvailability.getStatus()))
                .date(Formatter.convertDateToStr(hallAvailability.getDate(),"yyyy-MM-dd"))
                .shift(hallAvailability.getShift())
                .build();
    }


    private HallAvailability convertToHallAvailability(HallAvailabilityRequest request, Hall hall){
        HallAvailability hallAvailability= new HallAvailability();
        hallAvailability.setHall(hall);
        hallAvailability.setStatus(HallStatus.valueOf(request.getStatus()));
        hallAvailability.setDate(Formatter.convertStrToDate(request.getDate(),"yyyy-MM-dd"));
        hallAvailability.setStartTime(request.getStartTime());
        hallAvailability.setEndTime(request.getEndTime());
        hallAvailability.setShift(HallShift.valueOf(request.getShift()));
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
                hallImage.setImageUrl(uploadImageToS3(image, venueName, hall.getName(), fileName));

                hallImageRepo.save(hallImage);
            } catch (CustomException e) {
                System.err.println("Error processing image: " + e.getMessage());
            } catch (Exception e) {
                System.err.println("Unexpected error: " + e.getMessage());
            }
        }
    }

    private String uploadImageToS3(MultipartFile image, String venueName, String hallName, String fileName) throws IOException {
        String s3Key = "images/venues/" + venueName.replaceAll("\\s", "") + "/" + hallName.replaceAll("\\s", "") + "/" + fileName;
        String contentType = image.getContentType();
        long fileSize = image.getSize();
        InputStream inputStream = image.getInputStream();
        return awsService.uploadFile(bucketName,s3Key,fileSize,contentType,inputStream);
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
                .description(hall.getDescription())
                .status(String.valueOf(hall.getStatus()))
                .hallImagePaths(getHallImagePaths(hall.getId()))
                .build();
    }
}