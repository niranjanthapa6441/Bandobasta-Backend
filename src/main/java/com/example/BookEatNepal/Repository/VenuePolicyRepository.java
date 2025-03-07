package com.example.BookEatNepal.Repository;
import com.example.BookEatNepal.Model.VenuePolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VenuePolicyRepository extends JpaRepository<VenuePolicy,Integer> {

}
