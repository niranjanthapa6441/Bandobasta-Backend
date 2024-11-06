package com.example.BookEatNepal.ServiceImpl;

import com.example.BookEatNepal.Enums.BookingStatus;
import com.example.BookEatNepal.Enums.EventType;
import com.example.BookEatNepal.Enums.HallStatus;
import com.example.BookEatNepal.Model.*;
import com.example.BookEatNepal.Payload.DTO.*;
import com.example.BookEatNepal.Repository.*;
import com.example.BookEatNepal.Payload.Request.BookingRequest;
import com.example.BookEatNepal.Service.HallBookingService;
import com.example.BookEatNepal.Util.CustomException;
import com.example.BookEatNepal.Util.Formatter;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class HallBookingServiceImpl implements HallBookingService {
    private static final String SUCCESS_MESSAGE = "successful";
    @Autowired
    private HallAvailabilityRepo hallAvailabilityRepo;

    @Autowired
    private MenuRepo menuRepo;

    @Autowired
    private HallBookingRepo hallBookingRepo;

    @Autowired
    private FoodRepo foodRepo;

    @Autowired
    private AppUserRepo appUserRepo;

    @Autowired
    private HallImageRepo hallImageRepo;

    @Autowired
    private HallRepo hallRepo;

    @Autowired
    private MenuItemRepo foodMenuRepo;

    @Autowired
    private BookingMenuItemRepo bookingMenuItemRepo;

    @Autowired
    private EntityManager entityManager;

    @Override
    public String save(BookingRequest request) {
        HallAvailability hallAvailability = getHallAvailability(request.getId());

        Menu menu = getMenu(request.getMenuId());

        AppUser user = getUser(request.getUserId());

        if (request.getNumberOfGuests() == 0){
            throw  new CustomException(CustomException.Type.NUMBER_OF_GUESTS_SHOULD_NOT_BE_EMPTY);
        }
        if (hallAvailability.getStatus().equals(HallStatus.AVAILABLE)){
            HallBooking hallBooking=hallBookingRepo.save(toBooking(request, menu, hallAvailability, user));

            updateHallAvailabilityStatus(request.getId(), HallStatus.PENDING);

            toBookingMenuItem(request.getFoodIds(),hallBooking);

            return SUCCESS_MESSAGE;
        }
        else throw  new CustomException(CustomException.Type.BOOKING_HAS_ALREADY_BEEN_MADE);
    }


    @Override
    public String delete(int id) {
        HallBooking hallBooking = getHallBooking(id);
        hallBooking.setStatus(BookingStatus.DELETED);

        hallBookingRepo.save(hallBooking);
        return SUCCESS_MESSAGE;
    }

    @Override
    public HallBookingDTO findBookingByUser(String userId, String startDate, String endDate, String bookingStatus, String sortBy, int page, int size) {
        int id = Integer.parseInt(userId);
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<HallBooking> query = cb.createQuery(HallBooking.class);
        Root<HallBooking> bookingRoot = query.from(HallBooking.class);
        Join<HallBooking, AppUser> hallBookingAppUserJoin = bookingRoot.join("user");

        query.select(bookingRoot);
        List<Predicate> predicates = new ArrayList<>();

        if (id != 0 ) {
            predicates.add(cb.equal(hallBookingAppUserJoin.get("id"), id));
        }
        query.where(predicates.toArray(new Predicate[0]));

        List<HallBooking> bookings = entityManager.createQuery(query).getResultList();

        TypedQuery<HallBooking> typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult((page - 1) * size);
        typedQuery.setMaxResults(size);

        List<HallBooking> pagedBookings = typedQuery.getResultList();

        int currentPage = page - 1;
        int totalElements = bookings.size();
        int totalPages = (int) Math.ceil((double) totalElements / size);
        return toBookingDTO(pagedBookings, currentPage, totalElements, totalPages);
    }

    @Override
    public HallBookingDTO findBookingByVenue(String venueId, String bookingDate, String hallId, int page, int size) {
        int id = Integer.parseInt(venueId);

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<HallBooking> query = cb.createQuery(HallBooking.class);

        Root<HallBooking> bookingRoot = query.from(HallBooking.class);
        Join<HallBooking, HallAvailability> hallBookingHallAvailabilityJoin = bookingRoot.join("hallAvailability");
        Join<HallAvailability, Hall> hallAvailabilityHallJoin = hallBookingHallAvailabilityJoin.join("hall");
        Join<Hall, Venue> hallVenueJoin = hallAvailabilityHallJoin.join("venue");

        query.select(bookingRoot);
        List<Predicate> predicates = new ArrayList<>();

        if (id != 0 ) {
            predicates.add(cb.equal(hallVenueJoin.get("id"), id));
        }
        query.where(predicates.toArray(new Predicate[0]));

        List<HallBooking> bookings = entityManager.createQuery(query).getResultList();

        TypedQuery<HallBooking> typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult((page - 1) * size);
        typedQuery.setMaxResults(size);

        List<HallBooking> pagedBookings = typedQuery.getResultList();

        int currentPage = page - 1;
        int totalElements = bookings.size();
        int totalPages = (int) Math.ceil((double) totalElements / size);
        return toBookingDTO(pagedBookings, currentPage, totalElements, totalPages);
    }

    @Override
    public String update(BookingRequest request, int id) {
        return SUCCESS_MESSAGE;
    }

    @Override
    public String confirmBooking(int id) {
        HallBooking hallBooking = getHallBooking(id);
        hallBooking.setStatus(BookingStatus.BOOKED);
        hallBookingRepo.save(hallBooking);

        updateHallAvailabilityStatus(String.valueOf(hallBooking.getHallAvailability().getId()), HallStatus.BOOKED);

        return SUCCESS_MESSAGE;
    }

    @Override
    public HallBookingDetail findById(int id) {
        HallBooking hallBooking = getHallBooking(id);
        return convertToBookingDetail(hallBooking);
    }

    private HallBookingDetail convertToBookingDetail(HallBooking hallBooking) {
        return HallBookingDetail.builder()
                .id(hallBooking.getId())
                .venueName(hallBooking.getHallAvailability().getHall().getVenue().getVenueName())
                .hallDetail(toHallDetail(getHall(hallBooking.getHallAvailability().getHall().getId())))
                .menuDetail(toMenuDetail(hallBooking.getMenu()))
                .userId(String.valueOf(hallBooking.getUser().getId()))
                .requestedDate(Formatter.convertDateToStr(hallBooking.getRequestedDate(), "yyyy-MM-dd"))
                .confirmedDate(Formatter.convertDateToStr(hallBooking.getConfirmedDate(), "yyyy-MM-dd"))
                .requestedTime(hallBooking.getRequestedTime())
                .confirmedTime(hallBooking.getConfirmedTime())
                .bookedForDate(Formatter.convertDateToStr(hallBooking.getBookedForDate(), "yyyy-MM-dd"))
                .price(hallBooking.getPrice())
                .status(String.valueOf(hallBooking.getStatus()))
                .eventType(String.valueOf(hallBooking.getEventType()))
                .build();
    }

    private Hall getHall(int id) {
        return hallRepo.findById(id)
                .orElseThrow(() -> new CustomException(CustomException.Type.HALL_NOT_FOUND));
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

    private List<String> getHallImagePaths(int id) {
        List<String> hallImagePaths = new ArrayList<>();
        List<HallImage> hallImages = hallImageRepo.findByHallId(id);
        for (HallImage image : hallImages
        ) {
            hallImagePaths.add(image.getImageUrl());
        }
        return hallImagePaths;
    }

    private void updateHallAvailabilityStatus(String id, HallStatus status) {
        HallAvailability hallAvailability = getHallAvailability(id);
        hallAvailability.setStatus(status);
        hallAvailabilityRepo.save(hallAvailability);
    }

    private AppUser getUser(String id) {
        return appUserRepo.findById(Integer.valueOf(id))
                .orElseThrow(() -> new CustomException(CustomException.Type.USER_NOT_FOUND));
    }

    private Menu getMenu(String id) {
        return menuRepo.findById(Integer.valueOf(id))
                .orElseThrow(() -> new CustomException(CustomException.Type.MENU_NOT_FOUND));
    }

    private HallAvailability getHallAvailability(String id) {
        return hallAvailabilityRepo
                .findById(Integer.valueOf(id))
                .orElseThrow(() -> new CustomException(CustomException.Type.HALL_AVAILABILITY_NOT_FOUND));
    }

    private HallBooking toBooking(BookingRequest request, Menu menu, HallAvailability hallAvailability, AppUser user) {
        HallBooking hallBooking = new HallBooking();
        hallBooking.setHallAvailability(hallAvailability);
        hallBooking.setMenu(menu);
        hallBooking.setNumberOfGuests(request.getNumberOfGuests());
        hallBooking.setRequestedDate(LocalDate.now());
        hallBooking.setRequestedTime(LocalTime.now());
        hallBooking.setStatus(BookingStatus.PENDING);
        hallBooking.setBookedForDate(hallAvailability.getDate());
        hallBooking.setEventType(EventType.valueOf(request.getEventType()));
        hallBooking.setUser(user);
        return hallBooking;
    }

    private HallBooking getHallBooking(int id) {
        return hallBookingRepo.findById(id)
                .orElseThrow(() -> new CustomException(CustomException.Type.BOOKING_NOT_FOUND));
    }

    private MenuDetail toMenuDetail(Menu menu) {
        List<MenuItem> foodMenus = foodMenuRepo.findByMenu(menu);
        return MenuDetail.builder().
                id(String.valueOf(menu.getId())).
                menuType(String.valueOf(menu.getMenuType())).
                price(menu.getPrice()).
                description(menu.getDescription()).
                venueId(String.valueOf(menu.getVenue().getId())).
                status(String.valueOf(menu.getStatus())).
                foodDetails(toFoodDetail(foodMenus))
                .build();
    }

    private List<FoodDetail> toFoodDetail(List<MenuItem> foodMenus) {
        List<FoodDetail> foodDetails = new ArrayList<>();
        for (MenuItem foodMenu : foodMenus
        ) {
            foodDetails.add(FoodDetail.builder().
                    foodSubCategory(foodMenu.getFood().getSubCategory().getName()).
                    foodCategory(foodMenu.getFood().getSubCategory().getFoodCategory().getName()).
                    name(foodMenu.getFood().getName()).
                    description(foodMenu.getFood().getDescription()).
                    id(String.valueOf(foodMenu.getFood().getId())).
                    status(String.valueOf(foodMenu.getFood().getStatus())).
                    venueId(String.valueOf(foodMenu.getFood().getVenue().getId())).
                    build());
        }
        return foodDetails;
    }
    private HallBookingDTO toBookingDTO(List<HallBooking> bookings, int currentPage, int totalElements, int totalPages) {
        List<HallBookingDetail> bookingDetails = getBookingDetails(bookings);
        return  HallBookingDTO.builder()
                .bookings(bookingDetails)
                .currentPage(currentPage)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .build();
    }

    private List<HallBookingDetail> getBookingDetails(List<HallBooking> bookings) {
        List<HallBookingDetail> bookingDetails = new ArrayList<>();
        for (HallBooking hallBooking: bookings
        ) {
            bookingDetails.add(convertToBookingDetail(hallBooking));
        }
        return bookingDetails;
    }
    private void toBookingMenuItem(List<String> foodIds,HallBooking hallBooking) {
        for (String id : foodIds){
            BookingMenuItem bookingMenuItem= new BookingMenuItem();
            bookingMenuItem.setBooking(hallBooking);
            bookingMenuItem.setName(toFood(id));

            bookingMenuItemRepo.save(bookingMenuItem);
        }
    }

    private String toFood(String id) {
        Food food= foodRepo.findById(Integer.parseInt(id)).orElseThrow(() -> new CustomException(CustomException.Type.FOOD_NOT_FOUND));
        return  food.getName();
    }
}
