package com.tribytegenius.CareerCompass.repository;

import com.tribytegenius.CareerCompass.model.Role;
import com.tribytegenius.CareerCompass.model.AppRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByRoleName(AppRole appRole);
}
