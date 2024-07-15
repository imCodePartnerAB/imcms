package com.imcode.imcms.domain.component;

import com.imcode.imcms.domain.dto.ImageFilePageRequestDTO;
import com.imcode.imcms.domain.dto.PageRequestDTO;
import com.imcode.imcms.domain.dto.SearchImageFileQueryDTO;
import imcode.server.document.index.ImageFileIndex;
import imcode.util.DateConstants;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ImageFileSearchQueryConverter {

	public SolrQuery convertToSolrQuery(SearchImageFileQueryDTO searchQuery) {

		final SolrQuery solrQuery = new SolrQuery(termToDefaultQuery(searchQuery.getTerm(), searchQuery.getFilterBy()));

		prepareSolrQueryPaging(searchQuery.getPage(), solrQuery);

		return solrQuery;
	}

	private String termToDefaultQuery(final String term, SearchImageFileQueryDTO.FilterBy filterType) {
		if (StringUtils.isBlank(term)) return "*:*";

        switch (filterType) {
            case BEFORE_LICENSE_PERIOD_START:
            case BEFORE_LICENSE_PERIOD_END:
                return String.format("%s:[* TO %s]", filterType.getIndexFieldName(), processDateTerm(term));
            case AFTER_LICENSE_PERIOD_START:
            case AFTER_LICENSE_PERIOD_END:
                return String.format("%s:[%s TO *]", filterType.getIndexFieldName(), processDateTerm(term));
            case ALL:
                return getDefaultSearchFields().stream()
                        .map(field -> String.format("%s:(%s)", field, processTerm(term)))
                        .collect(Collectors.joining(" "));
            default:
                return String.format("%s:(%s)", filterType.getIndexFieldName(), processTerm(term));
        }
	}

	private String processTerm(String term){
		if (!term.startsWith("\"") && !term.endsWith("\"")) {
			String[] splits = term.split("\\s+");

			StringBuilder termBuilder = new StringBuilder();
			for (String split : splits) {
				termBuilder.append(String.format("*%s* ", split));
			}

			term = termBuilder.toString().trim();
		}

		return term;
	}

	private String processDateTerm(String term){
		try {
			final Date dateTerm = DateConstants.DATE_FORMAT.get().parse(term);
			return DateConstants.SOLR_DATE_FORMAT.get().format(dateTerm);
		} catch (ParseException e) {
			return "*";
		}
	}

	private List<String> getDefaultSearchFields() {
		return List.of(ImageFileIndex.FIELD__ID,
				ImageFileIndex.FIELD__NAME,
				ImageFileIndex.FIELD__PATH,
				ImageFileIndex.FIELD__UPLOADED,
				ImageFileIndex.FIELD__PHOTOGRAPHER,
				ImageFileIndex.FIELD__UPLOADED_BY,
				ImageFileIndex.FIELD__COPYRIGHT,
				ImageFileIndex.LICENSE_PERIOD_START,
				ImageFileIndex.LICENSE_PERIOD_END,
				ImageFileIndex.ALT_TEXT,
				ImageFileIndex.DESCRIPTION_TEXT);
	}

	public void prepareSolrQueryPaging(PageRequestDTO page, SolrQuery solrQuery) {
		if (page == null) {
			page = new ImageFilePageRequestDTO();
		}

		solrQuery.setStart(page.getSkip());
		solrQuery.setRows(page.getSize());

		Sort sort = page.getSort();
		if (sort == Sort.unsorted()) {
			sort = Sort.by(Sort.Order.desc(ImageFileIndex.FIELD__UPLOADED));
		}
		final Sort.Order order = sort.iterator().next();

		solrQuery.addSort(order.getProperty(), SolrQuery.ORDER.valueOf(order.getDirection().name().toLowerCase()));
	}
}
