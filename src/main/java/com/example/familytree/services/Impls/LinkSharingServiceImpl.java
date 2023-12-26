package com.example.familytree.services.Impls;

import com.example.familytree.entities.*;
import com.example.familytree.repositories.LinkSharingRepo;
import com.example.familytree.services.LinkSharingService;
import com.example.familytree.shareds.Constants;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LinkSharingServiceImpl implements LinkSharingService {
    private final LinkSharingRepo linkSharingRepo;

    @Override
    public String createLink(int familyTreeId, int personId, int userId) {
        String code = RandomStringUtils.randomAlphanumeric(64);
        LinkSharingEntity newLink = LinkSharingEntity.create(
                0,
                code,
                personId,
                userId,
                familyTreeId,
                Constants.getCurrentDay(),
                null
        );
        linkSharingRepo.save(newLink);
        return Constants.URL_LINK_SHARING + code;
    }

    @Override
    public boolean isTimeOutRequired(LinkSharingEntity linkSharing, long ms) {
        long currentTimeInMillis = System.currentTimeMillis();
        long otpRequestedTimeInMillis = linkSharing.getExp().getTime();

        return otpRequestedTimeInMillis + ms <= currentTimeInMillis;
    }
}
