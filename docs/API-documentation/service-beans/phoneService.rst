PhoneService
============

In order to init PhoneService bean need to use - ``Imcms.getServices().getManagedBean(PhoneService.class)``


Use API
-------

.. code-block:: jsp

    void updateUserPhones(List<Phone> phones, int userId);

    List<Phone> getUserPhones(int userId);


Fields Phone 
""""""""""""
#. Integer phoneId;
#. String number;
#. User user;
#. PhoneTypeDTO phoneType;

Fields PhoneTypeDTO
"""""""""""""""""""
#. Integer id;
#. String name;

``PhoneTypes``
#. OTHER(0, "Other"),
#. HOME(1, "Home"),
#. WORK(2, "Work"),
#. MOBILE(3, "Mobile"),
#. FAX(4, "Fax")
