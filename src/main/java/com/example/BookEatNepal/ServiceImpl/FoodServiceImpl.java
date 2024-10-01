package com.example.BookEatNepal.ServiceImpl;


import com.example.BookEatNepal.DTO.FoodDTO;
import com.example.BookEatNepal.DTO.FoodDetail;
import com.example.BookEatNepal.Enums.FoodCategory;
import com.example.BookEatNepal.Enums.FoodStatus;
import com.example.BookEatNepal.Model.Food;
import com.example.BookEatNepal.Model.Venue;
import com.example.BookEatNepal.Repository.FoodRepo;
import com.example.BookEatNepal.Repository.VenueRepo;
import com.example.BookEatNepal.Request.FoodRequest;
import com.example.BookEatNepal.Service.FoodService;
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
public class FoodServiceImpl implements FoodService {
    private static final String SUCCESS_MESSAGE = "successful";
    @Autowired
    private FoodRepo foodRepo;
    @Autowired
    private VenueRepo venueRepo;

    @Autowired
    private EntityManager entityManager;

    @Override
    public String save(FoodRequest request, MultipartFile image) {
        Venue venue = findVenueById(request.getVenueId());
        request.setImageUrl(generateImagePath(image, venue.getVenueName(), request));
        foodRepo.save(converToFood(request, venue));
        return SUCCESS_MESSAGE;
    }
    @Override
    public String delete(int id) {
        Food food = foodRepo.findById(id).orElseThrow(() -> new CustomException(CustomException.Type.FOOD_NOT_FOUND));
        food.setStatus(FoodStatus.DELETED);
        foodRepo.save(food);
        return SUCCESS_MESSAGE;
    }

    @Override
    public FoodDTO findAll(String venueId, int page, int size) {
        int id = Integer.parseInt(venueId);
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Food> query = cb.createQuery(Food.class);
        Root<Food> foodRoot = query.from(Food.class);
        Join<Food, Venue> foodVenueJoin = foodRoot.join("venue");

        query.select(foodRoot);
        List<Predicate> predicates = new ArrayList<>();

        if (id != 0 ) {
            predicates.add(cb.equal(foodVenueJoin.get("id"), id));
        }
        query.where(predicates.toArray(new Predicate[0]));

        List<Food> foods = entityManager.createQuery(query).getResultList();

        TypedQuery<Food> typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult((page - 1) * size);
        typedQuery.setMaxResults(size);

        List<Food> pagedFoods = typedQuery.getResultList();

        int currentPage = page - 1;
        int totalElements = foods.size();
        int totalPages = (int) Math.ceil((double) totalElements / size);
        return convertToFoodDTO(pagedFoods, currentPage, totalElements, totalPages);
    }

    @Override
    public FoodDetail findById(int id) {
        Food food = foodRepo.findById(id).orElseThrow(() -> new CustomException(CustomException.Type.FOOD_NOT_FOUND));
        return convertToFoodDetail(food);
    }

    @Override
    public String update(FoodRequest request, int id, MultipartFile image) {
        Venue venue= findVenueById(request.getVenueId());
        Food food = foodRepo.findById(id).orElseThrow(() -> new CustomException(CustomException.Type.FOOD_NOT_FOUND));
        food.setVenue(venue);
        food.setStatus(FoodStatus.valueOf(request.getStatus()));
        food.setName(request.getName());
        food.setDescription(request.getDescription());
        food.setImageUrl(generateImagePath(image,venue.getVenueName() ,request));
        food.setCategory(FoodCategory.valueOf(request.getFoodCategory()));
        return SUCCESS_MESSAGE;
    }

    private Venue findVenueById(String venueId) {
        return venueRepo.findById(Integer.valueOf(venueId)).orElseThrow(() -> new CustomException(CustomException.Type.VENUE_NOT_FOUND));
    }

    private String generateImagePath(MultipartFile image, String venueName, FoodRequest request) {
        validateImage(image);
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(image.getOriginalFilename()));
        if (fileName.contains(".php%00.")) {
            throw new CustomException(CustomException.Type.INVALID_FILE_EXTENSION);
        }
        return generateImagePath(image, venueName, fileName);
    }

    private String generateImagePath(MultipartFile multipartFile, String venueName, String fileName) {
        String uploadDirectory = "./images/venues/" + venueName.replaceAll("\\s", "") + "/foods";
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

    private void validateImage(MultipartFile multipartFile) {
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

    private Food converToFood(FoodRequest request, Venue venue) {
        Food food = new Food();
        food.setDescription(request.getDescription());
        food.setName(request.getName());
        food.setVenue(venue);
        food.setStatus(FoodStatus.valueOf(request.getStatus()));
        food.setImageUrl(request.getImageUrl());
        food.setCategory(FoodCategory.valueOf(request.getFoodCategory()));
        return food;
    }

    private FoodDTO convertToFoodDTO(List<Food> foods, int currentPage, int totalElements, int totalPages) {
        List<FoodDetail> foodDetails = convertToFoodDetails(foods);
        return FoodDTO.builder()
                .foodDetails(foodDetails)
                .currentPage(currentPage)
                .totalPages(totalPages)
                .totalElements(totalElements)
                .build();
    }
    private List<FoodDetail> convertToFoodDetails(List<Food> foods) {
        List<FoodDetail> foodDetails = new ArrayList<>();
        for (Food food : foods
        ) {
            foodDetails.add(convertToFoodDetail(food));
        }
        return foodDetails;
    }
    private FoodDetail convertToFoodDetail(Food food) {
        return FoodDetail.builder()
                .name(food.getName())
                .venueId(String.valueOf(food.getVenue().getId()))
                .description(food.getDescription())
                .id(String.valueOf(food.getId()))
                .status(String.valueOf(food.getStatus()))
                .imageUrl(food.getImageUrl())
                .foodCategory(String.valueOf(food.getCategory()))
                .build();
    }
}
