INSERT INTO imcms_languages
  (`id`, `code`, `name`, `native_name`, `enabled`)
VALUES
  (1, 'en', 'English', 'English', true),
  (2, 'sv', 'Swedish', 'Svenska', true);


INSERT INTO imcms_text_doc_images (
  doc_id, doc_version_no, width,  height, border,  v_space,  h_space,

  no,     image_name,     target, align, alt_text, low_scr, imgurl, linkurl,

  type, language_id,

  format, rotate_angle, crop_x1, crop_y1, crop_x2, crop_y2)
VALUES
  (1001, 0, 0, 0, 0, 0, 0,

   1, "", "_blank", "top", "", "", "imCMSpower.gif", "http://www.imcms.net/>",

   0, 1,

   0, 0, -1, -1, -1, -1),



  (1001, 0, 0, 0, 0, 0, 0,

   1, "", "_blank", "top", "", "", "imCMSpower.gif", "http://www.imcms.net/>",

   0, 2,

   0, 0, -1, -1, -1, -1),   


  (1001, 0, 0, 0, 0, 0, 0,

  2, "", "_blank", "top", "", "", "imCMSpower.gif", "http://www.imcms.net/>",

  0, 1,

  0, 0, -1, -1, -1, -1),


  (1001, 0, 0, 0, 0, 0, 0,

  2, "", "_blank", "top", "", "", "imCMSpower.gif", "http://www.imcms.net/>",

  0, 2,

  0, 0, -1, -1, -1, -1),


  (1001, 0, 0, 0, 0, 0, 0,

  3, "", "_blank", "top", "", "", "imCMSpower.gif", "http://www.imcms.net/>",

  0, 1,

  0, 0, -1, -1, -1, -1),


  (1001, 0, 0, 0, 0, 0, 0,

  3, "", "_blank", "top", "", "", "imCMSpower.gif", "http://www.imcms.net/>",

  0, 2,

  0, 0, -1, -1, -1, -1);