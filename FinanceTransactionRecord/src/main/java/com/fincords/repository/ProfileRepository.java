package com.fincords.repository;

import com.fincords.model.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProfileRepository extends JpaRepository<Profile, Long> {

//    @Query(value = "SELECT p FROM profile p WHERE p.account_id = :id1 AND p.created_by = :id2 AND p.is_active = true", nativeQuery = true)
//    Optional<Profile> findByAccountIdAndMaster(@Param("id1") Long acId, @Param("id2") Long masterId);

    @Query("SELECT p FROM Profile p WHERE p.account.id = :accountId AND p.createdBy.id = :masterId AND p.isActive = true")
    Optional<Profile> findByAccountIdAndMaster(@Param("accountId") Long accountId, @Param("masterId") Long masterId);
}
