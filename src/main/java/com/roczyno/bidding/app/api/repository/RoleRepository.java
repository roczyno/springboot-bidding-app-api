package com.roczyno.bidding.app.api.repository;



import com.roczyno.bidding.app.api.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role,Integer> {
    Optional<Role> findByName(String role);
}
