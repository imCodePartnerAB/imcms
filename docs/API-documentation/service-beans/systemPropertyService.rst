SystemPropertyService
=====================

   List<SystemProperty> findAll();

    SystemProperty findByName(String name);

    SystemProperty update(SystemProperty systemProperty);

    void deleteById(Integer id);

    SystemProperty findById(Integer id);