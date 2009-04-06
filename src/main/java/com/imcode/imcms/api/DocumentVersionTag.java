package com.imcode.imcms.api;

/**
 * Document version tags.
 *   
 * For every document version in the system have: 
 *   - at most one WORKING version
 *   - at most one PUBLISHED version
 *   - unlimited numbers of POSTPONED versions
 *   - unlimited numbers of ARCHUVED versions   
 */
public enum DocumentVersionTag {
	
	/** Working version. */
    WORKING,
    
    /** Postponed working version. */
    POSTPONED,
    
    /** Published version. */
    PUBLISHED,		
    
    /** Archived version. */
    ARCHIVED,    
}