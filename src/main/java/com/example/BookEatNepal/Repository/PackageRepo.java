package com.example.BookEatNepal.Repository;

import com.example.BookEatNepal.Model.Package;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PackageRepo extends JpaRepository<Package, Integer> {
}
