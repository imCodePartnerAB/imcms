update text_docs set group_id = ISNULL((Select min(group_id) from templates_cref where template_id = text_docs.template_id),(select min(group_id) from templates_cref))
