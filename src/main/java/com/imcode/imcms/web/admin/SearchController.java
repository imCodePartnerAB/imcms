package com.imcode.imcms.web.admin;

/**
 * Application info controller.
 */
import org.apache.commons.lang.math.IntRange;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

/**
 * Document search controller.
 */
@Controller
@RequestMapping("/search.html")
@SessionAttributes(types=SearchParams.class)
public class SearchController {

    @ModelAttribute("documentIdRange")
    public IntRange documentIdRange() {
    	// query from db
        return new IntRange(1001, 1111);
    }
	
    
    @RequestMapping(method = RequestMethod.GET)
    public String setupForm(ModelMap model) {
    	SearchParams searchParams = new SearchParams();
    	searchParams.setRange(new SearchParams.Range(1001, 1111));
    	
    	model.addAttribute(searchParams);
    	
        return "forward:/WEB-INF/admin/search/search.jsp";
    }

    
    @RequestMapping(method = RequestMethod.POST)
    public String processSubmit(@ModelAttribute SearchParams searchParams, 
    		BindingResult result, SessionStatus status) {

        // validate
    	// do search
        
    	return "forward:/WEB-INF/admin/search/search.jsp";
    }
}