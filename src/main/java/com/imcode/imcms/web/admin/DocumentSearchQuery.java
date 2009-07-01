package com.imcode.imcms.web.admin;

import imcode.server.document.LifeCyclePhase;
import imcode.server.document.index.AnalyzerImpl;
import imcode.server.document.index.DocumentIndex;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.NumberTools;
import org.apache.lucene.document.DateTools.Resolution;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.RangeQuery;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.BooleanClause.Occur;

public class DocumentSearchQuery implements Serializable {
	private static final long serialVersionUID = -1983566269041593813L;
	private static final Log log = LogFactory.getLog(DocumentSearchQuery.class);
	
	public enum Relationship {
		PARENTS(DocumentIndex.FIELD__HAS_PARENTS, true), 
		NO_PARENTS(DocumentIndex.FIELD__HAS_PARENTS, false), 
		CHILDREN(DocumentIndex.FIELD__HAS_CHILDREN, true), 
		NO_CHILDREN(DocumentIndex.FIELD__HAS_CHILDREN, false);
		
		private final String field;
		private final String value;
		
		private Relationship(String field, boolean value) {
			this.field = field;
			this.value = Boolean.toString(value);
		}
		
		public String getField() {
			return field;
		}
		
		public String getValue() {
			return value;
		}
	}
	
	private BooleanQuery mainQuery = new BooleanQuery();
	private AnalyzerImpl analyzer = new AnalyzerImpl();
	
	
	public DocumentSearchQuery() {
	}
	
	public void creators(int[] creatorIds) {
		addIds(DocumentIndex.FIELD__CREATOR_ID, creatorIds);
	}
	
	public void publishers(int[] publisherIds) {
		addIds(DocumentIndex.FIELD__PUBLISHER_ID, publisherIds);
	}
	
	public void categories(int[] categoryIds) {
		addIds(DocumentIndex.FIELD__CATEGORY_ID, categoryIds);
	}
	
	public void lifecyclePhases(Collection<LifeCyclePhase> phases) {
		if (phases.isEmpty()) {
			return;
		}
		
		String[] phaseTexts = new String[phases.size()];
		
		int index = 0;
		
		for (Iterator<LifeCyclePhase> it = phases.iterator(); it.hasNext();) {
			phaseTexts[index++] = it.next().toString();
		}
		
		addOrTerms(DocumentIndex.FIELD__PHASE, phaseTexts);
	}
	
	public void dateRanges(Collection<FieldDateRange> dateRanges) {
		if (dateRanges.isEmpty()) {
			return;
		}
		
		BooleanQuery dateRangesQuery = new BooleanQuery();
		
		for (FieldDateRange dateRange : dateRanges) {
			String field = dateRange.getField();
			Term lowerTerm = null;
			Term upperTerm = null;
			
			if (dateRange.getFrom() != null) {
				lowerTerm = new Term(field, DateTools.dateToString(dateRange.getFrom(), Resolution.DAY));
			}
			
			if (dateRange.getTo() != null) {
				upperTerm = new Term(field, DateTools.dateToString(dateRange.getTo(), Resolution.DAY));
			}
			
			RangeQuery rangeQuery = new RangeQuery(lowerTerm, upperTerm, true);
			dateRangesQuery.add(rangeQuery, Occur.SHOULD);
		}
		
		mainQuery.add(dateRangesQuery, Occur.MUST);
	}
	
	public void relationships(Collection<Relationship> relationships) {
		if (relationships.isEmpty()) {
			return;
		}
		
		BooleanQuery relationshipsQuery = new BooleanQuery();
		
		for (Relationship relationship : relationships) {
			Term term = new Term(relationship.getField(), relationship.getValue());
			
			relationshipsQuery.add(new TermQuery(term), Occur.SHOULD);
		}
		
		mainQuery.add(relationshipsQuery, Occur.MUST);
	}
	
	public void text(String text) {
		try {
			BooleanQuery textsQuery = new BooleanQuery();
			
			TokenStream tokenStream = analyzer.tokenStream(DocumentIndex.FIELD__TEXT, new StringReader(text));
			
			for (Token token = tokenStream.next(new Token()); token != null; token = tokenStream.next(token)) {
				String termText = token.term();
				
				Term headlineTerm = new Term(DocumentIndex.FIELD__META_HEADLINE, termText);
				Term metaTextTerm = new Term(DocumentIndex.FIELD__META_TEXT, termText);
				Term textTerm = new Term(DocumentIndex.FIELD__TEXT, termText);
				
				textsQuery.add(new TermQuery(headlineTerm), Occur.SHOULD);
				textsQuery.add(new TermQuery(metaTextTerm), Occur.SHOULD);
				textsQuery.add(new TermQuery(textTerm), Occur.SHOULD);
			}
			
			mainQuery.add(textsQuery, Occur.MUST);
		} catch (IOException ex) {
			log.warn(ex.getMessage(), ex);
		}
	}
	
	public void alias(String alias) {
		Term aliasTerm = new Term(DocumentIndex.FIELD__ALIAS, alias.toLowerCase());
		
		mainQuery.add(new TermQuery(aliasTerm), Occur.MUST);
	}
	
	public void metaRange(IntegerRange range) {
		String field = DocumentIndex.FIELD__META_ID_LEXICOGRAPHIC;
		
		Term lowerTerm = null;
		Term upperTerm = null;
		
		if (range.getFrom() != null) {
			lowerTerm = new Term(field, NumberTools.longToString(range.getFrom()));
		}
		
		if (range.getTo() != null) {
			upperTerm = new Term(field, NumberTools.longToString(range.getTo()));
		}
		
		RangeQuery metaQuery = new RangeQuery(lowerTerm, upperTerm, true);
		mainQuery.add(metaQuery, Occur.MUST);
	}
	
	public void searchInPreviousResults(Collection<Integer> metaIds) {
		BooleanQuery metaQuery = getIntegerORQuery(DocumentIndex.FIELD__META_ID, metaIds);
		
		mainQuery.add(metaQuery, Occur.MUST);
	}
	
	public static BooleanQuery includePreviousResults(BooleanQuery mainQuery, Collection<Integer> metaIds) {
		if (metaIds.isEmpty()) {
			return mainQuery;
		}
		
		BooleanQuery metaQuery = getIntegerORQuery(DocumentIndex.FIELD__META_ID, metaIds);
		
		BooleanQuery orQuery = new BooleanQuery();
		orQuery.add(mainQuery, Occur.SHOULD);
		orQuery.add(metaQuery, Occur.SHOULD);
		
		return orQuery;
	}
	
	public void role(int roleId, boolean not) {
		Term roleTerm = new Term(DocumentIndex.FIELD__ROLE_ID, Integer.toString(roleId));
		
		mainQuery.add(new TermQuery(roleTerm), (not ? Occur.MUST_NOT : Occur.MUST));
	}
	
	public void template(String template) {
		Term templateTerm = new Term(DocumentIndex.FIELD__TEMPLATE, template);
		
		mainQuery.add(new TermQuery(templateTerm), Occur.MUST);
	}
	
	private static BooleanQuery getIntegerORQuery(String field, Collection<Integer> ids) {
		BooleanQuery orQuery = new BooleanQuery();
		
		for (Integer id : ids) {
			Term idTerm = new Term(field, id.toString());
			orQuery.add(new TermQuery(idTerm), Occur.SHOULD);
		}
		
		return orQuery;
	}
	
	private void addIds(String field, int[] ids) {
		if (ids.length == 0) {
			return;
		}
		
		String[] idTexts = new String[ids.length];
		
		for (int i = 0; i < ids.length; i++) {
			idTexts[i] = Integer.toString(ids[i]);
		}
		
		addOrTerms(field, idTexts);
	}
	
	private void addOrTerms(String field, String[] termTexts) {
		BooleanQuery termsQuery = new BooleanQuery();
		
		for (String termText : termTexts) {
			Term term = new Term(field, termText);
			
			termsQuery.add(new TermQuery(term), Occur.SHOULD);
		}
		
		mainQuery.add(termsQuery, Occur.MUST);
	}
	
	public BooleanQuery toLuceneQuery() {
		return mainQuery;
	}
	
	@Override
	public String toString() {
		return String.format("%s[%s]", getClass().getName(), mainQuery.toString());
	}
}
