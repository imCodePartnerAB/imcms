UserPropertyService
===================


.. warning:: This init instance over Imcms.getServices().getUserPropertyService() working from 12 version


Init or get instance UserPropertyService over global Imcms.getServices ``Imcms.getServices().getUserPropertyService();``

.. code-block:: jsp

List<UserProperty> = Imcms.getServices().getUserPropertyService().getAll();

List<UserProperty> = Imcms.getServices().getUserPropertyService().getByUserId(Integer userId);

UserProperty = Imcms.getServices().getUserPropertyService().getByUserIdAndKeyName(Integer userId, String keyName);

void = Imcms.getServices().getUserPropertyService().create(List<UserPropertyDTO> userProperties);

UserProperty = Imcms.getServices().getUserPropertyService().update(UserProperty userProperty);

void = Imcms.getServices().getUserPropertyService().deleteById(Integer id);










