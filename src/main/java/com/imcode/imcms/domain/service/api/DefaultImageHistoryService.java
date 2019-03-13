package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.dto.ImageDTO;
import com.imcode.imcms.domain.dto.ImageHistoryDTO;
import com.imcode.imcms.domain.service.ImageHistoryService;
import com.imcode.imcms.domain.service.LanguageService;
import com.imcode.imcms.domain.service.UserService;
import com.imcode.imcms.model.Language;
import com.imcode.imcms.persistence.entity.ImageHistoryJPA;
import com.imcode.imcms.persistence.entity.ImageJPA;
import com.imcode.imcms.persistence.entity.LanguageJPA;
import com.imcode.imcms.persistence.entity.LoopEntryRefJPA;
import com.imcode.imcms.persistence.entity.Version;
import com.imcode.imcms.persistence.repository.ImageHistoryRepository;
import com.imcode.imcms.util.function.TernaryFunction;
import imcode.server.Imcms;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service("imageHistoryService")
@Transactional
public class DefaultImageHistoryService implements ImageHistoryService {

    private final ImageHistoryRepository imageHistoryRepository;
    private final LanguageService languageService;
    private final UserService userService;
    private final TernaryFunction<ImageDTO, Version, Language, ImageJPA> imageDTOToImageJPA;
    private final Function<ImageHistoryJPA, ImageHistoryDTO> imageHistoryJPAToImageHistoryDTO;


    public DefaultImageHistoryService(ImageHistoryRepository imageHistoryRepository, LanguageService languageService, UserService userService, TernaryFunction<ImageDTO, Version, Language, ImageJPA> imageDTOToImageJPA, Function<ImageHistoryJPA, ImageHistoryDTO> imageHistoryJPAToImageHistoryDTO) {
        this.imageHistoryRepository = imageHistoryRepository;
        this.languageService = languageService;
        this.userService = userService;
        this.imageDTOToImageJPA = imageDTOToImageJPA;
        this.imageHistoryJPAToImageHistoryDTO = imageHistoryJPAToImageHistoryDTO;
    }

    @Override
    public void save(ImageJPA image) {
        final ImageHistoryJPA mappedHistory = new ImageHistoryJPA(
                image,
                userService.getUser(Imcms.getUser().getId()),
                LocalDateTime.now()
        );

        imageHistoryRepository.save(mappedHistory);
    }

    @Override
    public void save(ImageDTO image, LanguageJPA language, Version version) {
        final ImageHistoryJPA mappedHistory = new ImageHistoryJPA(
                imageDTOToImageJPA.apply(image, version, language),
                userService.getUser(Imcms.getUser().getId()),
                LocalDateTime.now()
        );

        imageHistoryRepository.save(mappedHistory);
    }

    @Override
    public List<ImageHistoryDTO> getAll(ImageDTO image) {
        final Integer docId = image.getDocId();
        final Integer index = image.getIndex();
        final LanguageJPA language = new LanguageJPA(languageService.findByCode(image.getLangCode()));

        final Optional<LoopEntryRefJPA> loopEntry = Optional.ofNullable(image.getLoopEntryRef())
                .map(LoopEntryRefJPA::new);


        final List<ImageHistoryJPA> imageHistory = (loopEntry.isPresent())
                ? imageHistoryRepository.findImageHistoryInLoop(docId, language, loopEntry.get(), index)
                : imageHistoryRepository.findImageHistoryNotInLoop(docId, language, index);

        return imageHistory.stream()
                .sorted(Comparator.comparing(ImageHistoryJPA::getModifiedAt).reversed())
                .map(imageHistoryJPAToImageHistoryDTO)
                .collect(Collectors.toList());
    }
}
