ImageHistoryService
===================


    void save(ImageJPA image);

    void save(ImageDTO image, LanguageJPA language, Version version);

    List<ImageHistoryDTO> getAll(ImageDTO image);