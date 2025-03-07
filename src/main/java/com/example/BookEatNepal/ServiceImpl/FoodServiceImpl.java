package com.example.BookEatNepal.ServiceImpl;


import com.example.BookEatNepal.Enums.FoodStatus;
import com.example.BookEatNepal.Model.Food;
import com.example.BookEatNepal.Model.FoodCategory;
import com.example.BookEatNepal.Model.FoodSubCategory;
import com.example.BookEatNepal.Model.Venue;
import com.example.BookEatNepal.Payload.DTO.FoodCategoryDetail;
import com.example.BookEatNepal.Payload.DTO.FoodDTO;
import com.example.BookEatNepal.Payload.DTO.FoodDetail;
import com.example.BookEatNepal.Payload.Request.FoodRequest;
import com.example.BookEatNepal.Payload.Request.VenueFoodCategoryRequest;
import com.example.BookEatNepal.Repository.FoodCategoryRepo;
import com.example.BookEatNepal.Repository.FoodRepo;
import com.example.BookEatNepal.Repository.FoodSubCategoryRepo;
import com.example.BookEatNepal.Repository.VenueRepo;
import com.example.BookEatNepal.Service.FoodService;
import com.example.BookEatNepal.Util.CustomException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Tuple;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FoodServiceImpl implements FoodService {
    private static final String SUCCESS_MESSAGE = "successful";
    @Autowired
    private FoodRepo foodRepo;

    @Autowired
    private VenueRepo venueRepo;

    @Autowired
    private FoodCategoryRepo foodCategoryRepo;

    @Autowired
    private FoodSubCategoryRepo foodSubCategoryRepo;

    @Autowired
    private EntityManager entityManager;

    @Override
    @Transactional
    public String save(List<FoodRequest> requests) {
        for(FoodRequest request: requests){
            Venue venue = findVenueById(request.getVenueId());
            foodRepo.save(converToFood(request,venue));
        }
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
        food.setSubCategory(toFoodSubCategory(request.getFoodSubCategory()));
        return SUCCESS_MESSAGE;
    }

    @Override
    @Transactional
    public String saveVenueFoodCategory(String venueId, List<VenueFoodCategoryRequest> requests) {
        Venue venue = findVenueById(venueId);
        for (VenueFoodCategoryRequest request: requests){
          FoodCategory foodCategory =  saveFoodCategory(request, venue);
          for (String subCategory: request.getSubCategories()){
              saveFoodSubCategory(subCategory, foodCategory, venue);
          }
        }
        return SUCCESS_MESSAGE;
    }

    @Override
    public List<FoodCategoryDetail> findAllFoodCategoryVenue(String venueId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        CriteriaQuery<Tuple> query = cb.createTupleQuery();

        Root<FoodCategory> foodCategoryRoot = query.from(FoodCategory.class);

        Join<FoodCategory, FoodSubCategory> subCategoryJoin = foodCategoryRoot.join("foodSubCategories", JoinType.INNER);

        List<Predicate> predicates = new ArrayList<>();
        if (venueId != null && !venueId.isEmpty()) {
            predicates.add(cb.equal(foodCategoryRoot.get("venue").get("id"), venueId));
        }

        query.multiselect(
                foodCategoryRoot.get("name").alias("categoryName"),
                subCategoryJoin.get("id").alias("subCategoryId"),
                subCategoryJoin.get("name").alias("subCategoryName")
        );

        query.where(cb.and(predicates.toArray(new Predicate[0])));

        List<Tuple> resultList = entityManager.createQuery(query).getResultList();

        Map<String, Map<Integer, String>> groupedData = resultList.stream()
                .collect(Collectors.groupingBy(
                        tuple -> tuple.get("categoryName", String.class),
                        Collectors.toMap(
                                tuple -> tuple.get("subCategoryId", Integer.class),
                                tuple -> tuple.get("subCategoryName", String.class)
                        )
                ));

        return groupedData.entrySet().stream()
                .map(entry -> FoodCategoryDetail.builder()
                        .category(entry.getKey())
                        .subCategory(entry.getValue())
                        .build())
                .collect(Collectors.toList());
    }



    private Venue findVenueById(String venueId) {
        return venueRepo.findById(Integer.valueOf(venueId)).orElseThrow(() -> new CustomException(CustomException.Type.VENUE_NOT_FOUND));
    }

    private Food converToFood(FoodRequest request, Venue venue) {
        Food food = new Food();
        food.setDescription(request.getDescription());
        food.setName(request.getName());
        food.setVenue(venue);
        food.setStatus(FoodStatus.valueOf(request.getStatus()));
        food.setSubCategory(toFoodSubCategory(request.getFoodSubCategory()));
        return food;
    }

    private FoodSubCategory toFoodSubCategory(String subCategoryName) {
        return foodSubCategoryRepo.findByName(subCategoryName).orElseThrow(() -> new CustomException(CustomException.Type.SUB_CATEGORY_NOT_FOUND));
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
                .foodCategory(food.getSubCategory().getFoodCategory().getName())
                .foodSubCategory(food.getSubCategory().getName())
                .build();
    }
    private void saveFoodSubCategory(String subCategory, FoodCategory foodCategory, Venue venue) {
        validateFoodSubCategory(subCategory,venue,foodCategory);
        FoodSubCategory sfoodSubCategory = new FoodSubCategory();
        sfoodSubCategory.setFoodCategory(foodCategory);
        sfoodSubCategory.setName(subCategory);

        foodSubCategoryRepo.save(sfoodSubCategory);
    }

    private void validateFoodSubCategory(String subCategory, Venue venue, FoodCategory category) {
        Optional<FoodSubCategory> foodSubCategory = foodSubCategoryRepo.findByNameAndVenueAndCategory(subCategory, venue, category);
        if (foodSubCategory.isPresent()) {
            throw new CustomException(CustomException.Type.FO0D_SUB_CATEGORY_FOR_VENUE_IS_PRESENT);
        }
    }

    private FoodCategory saveFoodCategory(VenueFoodCategoryRequest request, Venue venue) {
        validateFoodCategory(request.getCategory(),venue);
        FoodCategory foodCategory= new FoodCategory();
        foodCategory.setVenue(venue);
        foodCategory.setName(request.getCategory());

        return foodCategoryRepo.save(foodCategory);
    }

    private void validateFoodCategory(String category, Venue venue) {
        Optional<FoodCategory> foodCategory = foodCategoryRepo.findByNameAndVenue(category,venue);
        if (foodCategory.isPresent()){
            throw  new CustomException(CustomException.Type.FO0D_CATEGORY_FOR_VENUE_IS_PRESENT);
        }
    }
}
