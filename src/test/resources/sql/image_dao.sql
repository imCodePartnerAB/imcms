INSERT INTO imcms_languages
  (`id`, `code`, `name`, `native_name`, `enabled`)
VALUES
  (1, 'en', 'English', 'English', true),
  (2, 'sv', 'Swedish', 'Svenska', true);


INSERT INTO imcms_text_doc_images (
  doc_id, doc_version_no, no, language_id,

  content_loop_no, content_no,

  width,  height, border,  v_space,  h_space,

  image_name, target, align, alt_text, low_scr, imgurl, linkurl,

  type,

  format, rotate_angle, crop_x1, crop_y1, crop_x2, crop_y2)
VALUES
  (1001, 0, 1, 1,

   null, null,

   0, 0, 0, 0, 0,

   "", "_blank", "top", "", "", "imCMSpower.gif", "http://www.imcms.net/>",

   0,

   0, 0, -1, -1, -1, -1),


  (1001, 0, 1, 2,

   null, null,

   0, 0, 0, 0, 0,

   "", "_blank", "top", "", "", "imCMSpower.gif", "http://www.imcms.net/>",

   0,

   0, 0, -1, -1, -1, -1),   


  (1001, 0, 2, 1,

   null, null,

   0, 0, 0, 0, 0,

   "", "_blank", "top", "", "", "imCMSpower.gif", "http://www.imcms.net/>",

   0,

   0, 0, -1, -1, -1, -1),


  (1001, 0, 2, 2,

   null, null,

   0, 0, 0, 0, 0,

   "", "_blank", "top", "", "", "imCMSpower.gif", "http://www.imcms.net/>",

   0,

   0, 0, -1, -1, -1, -1),


  (1001, 0, 3, 1,

   null, null,

   0, 0, 0, 0, 0,

   "", "_blank", "top", "", "", "imCMSpower.gif", "http://www.imcms.net/>",

   0,

   0, 0, -1, -1, -1, -1),


  (1001, 0, 3, 2,

   null, null,

   0, 0, 0, 0, 0,

   "", "_blank", "top", "", "", "imCMSpower.gif", "http://www.imcms.net/>",

   0,

   0, 0, -1, -1, -1, -1);

-- with content loop

INSERT INTO imcms_text_doc_images (
  doc_id, doc_version_no, no, language_id,

  content_loop_no, content_no,

  width,  height, border,  v_space,  h_space,

  image_name, target, align, alt_text, low_scr, imgurl, linkurl,

  type,

  format, rotate_angle, crop_x1, crop_y1, crop_x2, crop_y2)
VALUES
  (1001, 0, 1, 1,

   1, 1,

   0, 0, 0, 0, 0,

   "", "_blank", "top", "", "", "imCMSpower.gif", "http://www.imcms.net/>",

   0,

   0, 0, -1, -1, -1, -1),


  (1001, 0, 1, 2,

   1, 1,

   0, 0, 0, 0, 0,

   "", "_blank", "top", "", "", "imCMSpower.gif", "http://www.imcms.net/>",

   0,

   0, 0, -1, -1, -1, -1),


  (1001, 0, 2, 1,

   1, 2,

   0, 0, 0, 0, 0,

   "", "_blank", "top", "", "", "imCMSpower.gif", "http://www.imcms.net/>",

   0,

   0, 0, -1, -1, -1, -1),


  (1001, 0, 2, 2,

   1, 2,

   0, 0, 0, 0, 0,

   "", "_blank", "top", "", "", "imCMSpower.gif", "http://www.imcms.net/>",

   0,

   0, 0, -1, -1, -1, -1),


  (1001, 0, 3, 1,

   1, 3,

   0, 0, 0, 0, 0,

   "", "_blank", "top", "", "", "imCMSpower.gif", "http://www.imcms.net/>",

   0,

   0, 0, -1, -1, -1, -1),


  (1001, 0, 3, 2,

   1, 3,

   0, 0, 0, 0, 0,

   "", "_blank", "top", "", "", "imCMSpower.gif", "http://www.imcms.net/>",

   0,

   0, 0, -1, -1, -1, -1);