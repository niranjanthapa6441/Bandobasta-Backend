package com.example.BookEatNepal.Registration;

import org.springframework.stereotype.Service;

import java.util.function.Predicate;
@Service
public class EmailValidator implements Predicate<String> {
    @Override
    public boolean test(String s) {
        //Todo regex for email validator
        return true;
    }
}
