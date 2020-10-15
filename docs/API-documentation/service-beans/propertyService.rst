PropertyService
===============


   Property getByDocIdAndName(int docId, String name);

    Integer getDocIdByAlias(String alias);

    List<String> findAllAliases();

    List<Property> findByDocId(int docId);

    Boolean existsByAlias(String alias);
