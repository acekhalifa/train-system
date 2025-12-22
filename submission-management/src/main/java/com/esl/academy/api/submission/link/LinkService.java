package com.esl.academy.api.submission.link;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LinkService {

    private final LinkRepository linkRepository;

    //Helper method to get Links associated to a submission or Learning resource
    public List<String> getUrls(UUID objectId, LinkType linkType) {
        return linkRepository.findByObjectIdAndLinkType(objectId, linkType)
            .stream()
            .map(Link::getUrl)
            .collect(Collectors.toList());
    }

    public void save(Link link) {
        linkRepository.save(link);
    }
}
