package com.example.BookEatNepal.Repository;

import com.example.BookEatNepal.Model.CustomPackage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomPackageRepo extends JpaRepository<CustomPackage, Integer> {
}
