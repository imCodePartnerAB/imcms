
-- Ta fram alla childs till ett menydok

select to_meta_id, meta_headline 
from childs c join meta m 
on c.to_meta_id = m.meta_id 
where c.meta_id = "+meta_id[i]+" 
order by to_meta_id


-- Räkna antal föräldrar för dokumenten

select to_meta_id, count(meta_id) as parents 
from childs 
group by to_meta_id 
order by to_meta_id


-- Ta fram alla childs till ett browserdok

select distinct to_meta_id, meta_headline 
from browser_docs b join meta m 
on b.to_meta_id = m.meta_id 
where b.meta_id = "+meta_id[i]+" 
order by to_meta_id


-- En lista på alla dokument med rubrik och meta_id

select meta_id, meta_headline, doc_type 
from meta 
order by meta_id