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

        HALL_NOT_FOUND("Owner not found", 404),
        VENUE_NOT_FOUND("Venue not found", 404),
        FOOD_NOT_FOUND("Food not found", 404 ),
        MENU_NOT_FOUND("Menu not found", 404 ),
        INVALID_MENU_TYPE("Invalid Menu Type", 400),
        INVALID_FOOD_CATEGORY("Invalid Food Category", 400 ),
        INVALID_VENUE_ID("Invalid Venue id", 400 ),
        PACKAGE_NOT_FOUND("Package not Found", 400 ),
        INVALID_EVENT_TYPE("Invalid event type", 400 );
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
