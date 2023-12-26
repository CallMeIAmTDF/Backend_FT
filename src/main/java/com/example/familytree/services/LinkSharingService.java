package com.example.familytree.services;

import com.example.familytree.entities.FamilyTreeEntity;
import com.example.familytree.entities.LinkSharingEntity;
import com.example.familytree.models.response.InfoUser;
import com.example.familytree.models.response.ListTreeResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface LinkSharingService {
    String createLink(int familyTreeId, int personId, int userId);

    boolean isTimeOutRequired(LinkSharingEntity linkSharing, long ms);
}
