package com.example.BookEatNepal.Util;

public class CustomException extends RuntimeException {

    private final Type type;

    public CustomException(Type type) {
        this.type = type;
    }

    public Type getType() {
        return this.type;
    }

    @Override
    public String getMessage() {
        return this.type.getMessage();
    }

    public enum Type {
        CATEGORY_ALREADY_EXIST("Category already exists", 500),
        DATE_INVALID("Invalid Date Format", 501),
        TIME_INVALID("Invalid Time Format", 501),
        PHONE_NUMBER_ALREADY_EXISTS("Phone Number already exists", 500),
        EMAIL_ALREADY_EXISTS("Email already exists", 500),
        USERNAME_ALREADY_EXISTS("Username already exists", 500),
        INVALID_FILE_EXTENSION("Image extension should be .png, .jpeg, or .jpg", 400),
        INVALID_MIME_TYPE("Invalid MIME type", 400),
        INVALID_FILE_SIZE("Upload file size shouldn't be more than 3MB", 400),
        USER_NOT_FOUND("Owner not found", 404),
        AMENITY_NOT_FOUND("Owner not found", 404),

        HALL_NOT_FOUND("Hall not found", 404),
        VENUE_NOT_FOUND("Venue not found", 404),
        FOOD_NOT_FOUND("Food not found", 404 ),
        MENU_NOT_FOUND("Menu not found", 404 ),
        INVALID_MENU_TYPE("Invalid Menu Type", 400),
        INVALID_FOOD_CATEGORY("Invalid Food Category", 400 ),
        INVALID_VENUE_ID("Invalid Venue id", 400 ),
        PACKAGE_NOT_FOUND("Package not Found", 400 ),
        INVALID_EVENT_TYPE("Invalid event type", 400 ),
        HALL_AVAILABILITY_NOT_FOUND("Hall availability not found",400 ),
        BOOKING_NOT_FOUND("Booking not found", 400 ),
        PACKAGE_AVAILABILITY_NOT_FOUND("Package availability not found",400 ),
        BOOKING_HAS_ALREADY_BEEN_MADE("Time Unavailable for the booking",500 ),
        SUB_CATEGORY_NOT_FOUND("Sub category not found",400 ),
        NUMBER_OF_GUESTS_SHOULD_NOT_BE_EMPTY("Number of Guests should not be zero",400 ),
        INVALID_OTP("Invalid OTP",400 ),
        OTP_HAS_EXPIRED("OTP has expired",400 ),
        OTP_ALREADY_USED("OTP already used",400 ),
        OTP_HAS_NOT_BEEN_VERIFIED("OTP has not been verified",400 ),
        FO0D_SUB_CATEGORY_FOR_VENUE_IS_PRESENT(" Food  sub-category for venue is present",500 ),
        FO0D_CATEGORY_FOR_VENUE_IS_PRESENT("Food category for venue is present", 500),
        TICKET_NOT_FOUND("Ticket not found",500 ),
        INVALID_REQUEST("Ticket Details are required", 400 ),
        ORDER_NOT_FOUND("Order not found", 500),
        INSUFFICIENT_TICKET_QUANTITY("Insufficient Number of tickets",400 ),
        EVENT_NOT_FOUND("Event not found",500 ),
        ORDER_TICKET_NOT_FOUND("Ordered Ticket not found",500 ),
        TICKET_HAS_ALREADY_BEEN_CHECKED_IN("Ticket has already been checked in",400 ),
        INVALID_TICKET_ORDER_ID("Ticket not found",500 ),
        POLICY_NOT_FOUND("Policy not Found",404);

        private String message;
        private int code;

        Type(String message, int code) {
            this.message = message;
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public int getCode() {
            return code;
        }

        public Type updateMessage(String message, int code) {
            this.message = message;
            this.code = code;
            return this;
        }
    }
}
