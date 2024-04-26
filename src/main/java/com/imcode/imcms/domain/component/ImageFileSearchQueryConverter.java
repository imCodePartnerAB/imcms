package com.imcode.imcms.domain.component;

import com.imcode.imcms.domain.dto.ImageFilePageRequestDTO;
import com.imcode.imcms.domain.dto.PageRequestDTO;
import com.imcode.imcms.domain.dto.SearchImageFileQueryDTO;
import imcode.server.document.index.ImageFileIndex;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ImageFileSearchQueryConverter {

	public SolrQuery convertToSolrQuery(SearchImageFileQueryDTO searchQuery) {

		final SolrQuery solrQuery = new SolrQuery(termToDefaultQuery(searchQuery.getTerm()));

		prepareSolrQueryPaging(searchQuery, solrQuery);

		return solrQuery;
	}

	private String termToDefaultQuery(String term) {
		if (StringUtils.isBlank(term)) return "*:*";

		if (!term.startsWith("\"") && !term.endsWith("\"")) {
			String[] splits = term.split("\\s+");

			StringBuilder termBuilder = new StringBuilder();
			for (String split : splits) {
				termBuilder.append(String.format("*%s* ", split));
			}

			term = termBuilder.toString().trim();
		}

		final String finalTerm = term;
		return getSearchFields().stream()
				.map(field -> String.format("%s:(%s)", field, finalTerm))
				.collect(Collectors.joining(" "));
	}

	private List<String> getSearchFields() {
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

	private void prepareSolrQueryPaging(SearchImageFileQueryDTO searchQuery, SolrQuery solrQuery) {
		PageRequestDTO page = searchQuery.getPage();

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
