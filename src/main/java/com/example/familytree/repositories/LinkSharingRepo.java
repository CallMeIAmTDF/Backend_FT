package com.example.familytree.repositories;

import com.example.familytree.entities.KeyTokenEntity;
import com.example.familytree.entities.LinkSharingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface LinkSharingRepo extends JpaRepository<LinkSharingEntity, Integer> {
    LinkSharingEntity findFirstByLink (String link);


}
