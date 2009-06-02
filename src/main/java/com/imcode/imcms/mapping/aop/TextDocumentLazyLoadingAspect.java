package com.imcode.imcms.mapping.aop;

import imcode.server.document.DocumentVisitor;
import imcode.server.document.textdocument.TextDocumentDomainObject;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

import com.imcode.imcms.mapping.DocumentInitializingVisitor;
import com.imcode.imcms.mapping.TextDocumentInitializer;

/**
 * Text document lazy loading aspect.
 * Provides lazy-loading functionality for text document's fields.
 */
@Aspect
public class TextDocumentLazyLoadingAspect {
	
	/* TODO: Decide if it is necessary to advice the following removeXXX methods:	
	removeAllContentLoops()
	removeAllImages()
	removeAllIncludes()
	removeAllMenus()
	removeAllTexts()
	removeInclude(int)		
	*/	
		
    private boolean textsLoaded;
    private boolean imagesLoaded;
    private boolean menusLoaded;
    private boolean includesLoaded;
    private boolean templateNamesLoaded;
    private boolean contentLoopsLoaded;
    
	/**
	 * Text document initializer. 
	 */
	private TextDocumentInitializer textDocumentInitializer;

	
	public TextDocumentLazyLoadingAspect(TextDocumentInitializer textDocumentInitializer) {
		this.textDocumentInitializer = textDocumentInitializer;
	}    
	
	/*
	 clone()
	 setDependenciesMetaIdToNull()
	 */	   
    /** 
     * Loads all.
     * 
     * @param document 	 
     */
	@Before("(execution(* clone()) || execution(* setDependenciesMetaIdToNull())) && target(document)")
	public void loadAll(TextDocumentDomainObject document) {
		loadImages(document);
		loadIncludes(document);
		loadContentLoops(document);
		loadMenus(document);
		loadTemplates(document);
		loadTexts(document);
	} 
	
	@Around("execution(public void accept(..)) && args(documentVisitor)")
	public void accept(ProceedingJoinPoint pjp, DocumentVisitor documentVisitor) {
		documentVisitor.visitTextDocument((TextDocumentDomainObject)pjp.getThis());
	}
	
	
	/*
	getAllImages()
	setAllImages(Map<I18nLanguage, Map<Integer, ImageDomainObject>>)		
	setImage(I18nLanguage, int, ImageDomainObject)
	setImage(int, ImageDomainObject)		
	getImage(I18nLanguage, int)
	getImage(int)
	getImages()
	getImages(I18nLanguage) 
	 */
	@Before("execution(* *Image*(..)) && target(document)")
	public void loadImages(TextDocumentDomainObject document) {
		if (!imagesLoaded) {
			textDocumentInitializer.initImages(document);
			imagesLoaded = true;
		}
	}
	
	/*
	setAllTexts(Map<I18nLanguage, Map<Integer, TextDomainObject>>)
	getAllTexts()		
	setText(I18nLanguage, int, TextDomainObject)
	setText(int, TextDomainObject)
	getText(I18nLanguage, int)
	getText(int)
	getTexts()
	getTexts(I18nLanguage)
	getTextsMap(I18nLanguage) 
	 */
	@Before("execution(* *Text*(..)) && target(document)")
	public void loadTexts(TextDocumentDomainObject document) {
		if (!textsLoaded) {
			textDocumentInitializer.initTexts(document);
			textsLoaded = true;
		}
	}
	
	/*
	setMenu(int, MenuDomainObject)
	setMenusMap(Map<Integer, MenuDomainObject>)
	getMenu(int)
	getMenus() 
	 */
	@Before("(execution(* *Menu*(..)) || execution(* getChildDocumentIds())) && target(document)")
	public void loadMenus(TextDocumentDomainObject document) {
		if (!menusLoaded) {
			textDocumentInitializer.initMenus(document);
			menusLoaded = true;
		}
	}
	
	
	
	/*
	getIncludedDocumentId(int)
	getIncludesMap()
	setInclude(int, int)
	setIncludesMap(Map<Integer, Integer>) 
	 */
	@Before("execution(* *Include*(..)) && target(document)")
	public void loadIncludes(TextDocumentDomainObject document) {
		if (!includesLoaded) {
			textDocumentInitializer.initIncludes(document);
			includesLoaded = true;
		}
	}
	
	/*
	getDefaultTemplateName()
	getDefaultTemplateNameForRestricted1()
	getDefaultTemplateNameForRestricted2()
	setDefaultTemplateId(String)
	setDefaultTemplateIdForRestricted1(String)
	setDefaultTemplateIdForRestricted2(String)	
	
	getTemplateGroupId()
	getTemplateName()
	getTemplateNames()	
	setTemplateGroupId(int)
	setTemplateName(String)
	setTemplateNames(TemplateNames) 
	 */
	@Before("execution(* *Template*(..)) && target(document)")
	public void loadTemplates(TextDocumentDomainObject document) {
		if (!templateNamesLoaded) {
			textDocumentInitializer.initTemplateNames(document);
			templateNamesLoaded = true;
		}
	}
	
	/*
	getContentLoop(int)
	getContentLoopsMap()	
	setContentLoop(int, ContentLoop)
	setContentLoopsMap(Map<Integer, ContentLoop>) 
	 */
	@Before("execution(* *Loop*(..)) && target(document)")
	public void loadContentLoops(TextDocumentDomainObject document) {
		if (!contentLoopsLoaded) {
			textDocumentInitializer.initContentLoops(document);
			contentLoopsLoaded = true;
		}
	}  
}