package com.example.BookEatNepal.ServiceImpl;

import com.example.BookEatNepal.DTO.FoodDetail;
import com.example.BookEatNepal.DTO.MenuDTO;
import com.example.BookEatNepal.DTO.MenuDetail;
import com.example.BookEatNepal.Enums.FoodCategory;
import com.example.BookEatNepal.Enums.MenuStatus;
import com.example.BookEatNepal.Enums.MenuType;
import com.example.BookEatNepal.Model.Food;
import com.example.BookEatNepal.Model.FoodMenu;
import com.example.BookEatNepal.Model.Menu;
import com.example.BookEatNepal.Model.Venue;
import com.example.BookEatNepal.Repository.FoodMenuRepo;
import com.example.BookEatNepal.Repository.FoodRepo;
import com.example.BookEatNepal.Repository.MenuRepo;
import com.example.BookEatNepal.Repository.VenueRepo;
import com.example.BookEatNepal.Request.MenuRequest;
import com.example.BookEatNepal.Service.MenuService;
import com.example.BookEatNepal.Util.CustomException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MenuServiceImpl implements MenuService {
    private static final String SUCCESS_MESSAGE = "successful";

    @Autowired
    private VenueRepo venueRepo;

    @Autowired
    private FoodRepo foodRepo;

    @Autowired
    private MenuRepo menuRepo;

    @Autowired
    private FoodMenuRepo foodMenuRepo;
    @Autowired
    EntityManager entityManager;


    @Override
    public String save(MenuRequest request) {
        Venue venue = findVenueById(request.getVenueId());
        Menu menu = menuRepo.save(convertToMenu(request, venue));
        linkFoodToMenu(request.getFoodIds(), menu);
        return SUCCESS_MESSAGE;
    }

    @Override
    public String delete(int id) {
        Menu menu = menuRepo.findById(id).orElseThrow(() -> new CustomException(CustomException.Type.MENU_NOT_FOUND));
        menu.setStatus(MenuStatus.DELETED);
        menuRepo.save(menu);
        return SUCCESS_MESSAGE;
    }

    @Override
    public MenuDTO findMenuByVenue(String venueId, String menuType, int page, int size) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Menu> query = cb.createQuery(Menu.class);
        Root<Menu> menuRoot = query.from(Menu.class);
        Join<Menu, Venue> menuVenueJoin = menuRoot.join("venue");

        query.select(menuRoot);
        List<Predicate> predicates = new ArrayList<>();

        if (venueId != null && !venueId.isEmpty()) {
            try {
                int id = Integer.parseInt(venueId);
                predicates.add(cb.equal(menuVenueJoin.get("id"), id));
            } catch (NumberFormatException e) {
                throw new CustomException(CustomException.Type.INVALID_VENUE_ID);
            }
        }

        if (menuType != null && !menuType.isEmpty()) {
            try {
                MenuType enumMenuType = MenuType.valueOf(menuType.toUpperCase());
                predicates.add(cb.equal(menuRoot.get("menuType"), enumMenuType));
            } catch (IllegalArgumentException e) {
                throw new CustomException(CustomException.Type.INVALID_MENU_TYPE);
            }
        }

        query.where(predicates.toArray(new Predicate[0]));

        List<Menu> menus = entityManager.createQuery(query).getResultList();

        TypedQuery<Menu> typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult((page - 1) * size);
        typedQuery.setMaxResults(size);

        List<Menu> pagedMenus = typedQuery.getResultList();

        int totalElements = menus.size();
        int totalPages = (int) Math.ceil((double) totalElements / size);

        return convertToMenuDTO(pagedMenus, page, totalElements, totalPages);
    }

    @Override
    public MenuDetail findById(int id) {
        Menu menu = getMenuById(id);
        return toMenuDetail(menu);
    }

    @Override
    public String update(MenuRequest request, int id) {
        Menu menu = menuRepo.findById(id).orElseThrow(() -> new CustomException(CustomException.Type.MENU_NOT_FOUND));
        menu.setMenuType(MenuType.valueOf(request.getMenuType()));
        menu.setPrice(request.getPrice());
        menu.setDescription(request.getDescription());
        menu.setVenue(findVenueById(request.getVenueId()));
        menu.setStatus(MenuStatus.valueOf(request.getStatus()));
        return SUCCESS_MESSAGE;
    }

    private MenuDTO convertToMenuDTO(List<Menu> menus, int currentPage, int totalElements, int totalPages) {
        List<MenuDetail> foodDetails = convertToMenuDetails(menus);
        return MenuDTO.builder()
                .menuDetails(foodDetails)
                .currentPage(currentPage)
                .totalPages(totalPages)
                .totalElements(totalElements)
                .build();
    }

    private List<MenuDetail> convertToMenuDetails(List<Menu> menus) {
        List<MenuDetail> menuDetails = new ArrayList<>();
        for (Menu menu : menus
        ) {
            menuDetails.add(toMenuDetail(menu));
        }
        return menuDetails;
    }

    private void linkFoodToMenu(List<String> foodIds, Menu menu) {
        for (String foodId : foodIds) {
            Food food = findFoodById(Integer.parseInt(foodId));
            foodMenuRepo.save(convertToFoodMenu(food, menu));
        }
    }

    private FoodMenu convertToFoodMenu(Food food, Menu menu) {
        FoodMenu foodMenu = new FoodMenu();
        foodMenu.setFood(food);
        foodMenu.setMenu(menu);
        return foodMenu;
    }

    private Food findFoodById(int id) {
        return foodRepo.findById(id).orElseThrow(() -> new CustomException(CustomException.Type.FOOD_NOT_FOUND));
    }

    private Menu convertToMenu(MenuRequest request, Venue venue) {
        Menu menu = new Menu();
        menu.setVenue(venue);
        menu.setPrice(request.getPrice());
        menu.setDescription(request.getDescription());
        menu.setMenuType(MenuType.valueOf(request.getMenuType()));
        menu.setStatus(MenuStatus.valueOf(request.getStatus()));
        return menu;
    }

    private Venue findVenueById(String venueId) {
        return venueRepo.findById(Integer.valueOf(venueId)).orElseThrow(() -> new CustomException(CustomException.Type.VENUE_NOT_FOUND));
    }

    private MenuDetail toMenuDetail(Menu menu) {
        List<FoodMenu> foodMenus = foodMenuRepo.findByMenu(menu);
        return MenuDetail.builder().
                id(String.valueOf(menu.getId())).
                menuType(String.valueOf(menu.getMenuType())).
                price(menu.getPrice()).
                description(menu.getDescription()).
                venueId(String.valueOf(menu.getVenue().getId())).
                status(String.valueOf(menu.getStatus())).
                foodDetails(convertToFoodDetail(foodMenus))
                .build();
    }

    private List<FoodDetail> convertToFoodDetail(List<FoodMenu> foodMenus) {
        List<FoodDetail> foodDetails = new ArrayList<>();
        for (FoodMenu foodMenu : foodMenus
        ) {
            foodDetails.add(FoodDetail.builder().
                    foodCategory(String.valueOf(foodMenu.getFood().getCategory())).
                    name(foodMenu.getFood().getName()).
                    description(foodMenu.getFood().getDescription()).
                    imageUrl(foodMenu.getFood().getImageUrl()).
                    id(String.valueOf(foodMenu.getFood().getId())).
                    status(String.valueOf(foodMenu.getFood().getStatus())).
                    venueId(String.valueOf(foodMenu.getFood().getVenue().getId())).
                    build());
        }
        return foodDetails;
    }

    private Menu getMenuById(int id) {
        return menuRepo.findById(id).orElseThrow(() -> new CustomException(CustomException.Type.MENU_NOT_FOUND));
    }
}