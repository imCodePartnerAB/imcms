package imcode.server.document;

import imcode.server.Imcms;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity(name="Category")
@Table(name="category_types")
public class CategoryTypeDomainObject implements Comparable, Serializable {

	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="category_type_id")	
    private int id;
	
	@Column(name="name")
    private String name ;
	
	@Column(name="max_choices")
    private int maxChoices ;
	
	@Column(name="inherited")
    private boolean inherited;

	public CategoryTypeDomainObject() {}
			
    public CategoryTypeDomainObject(int id, String name, int maxChoices, boolean inherited) {
        this.id = id;
        this.name = name;
        this.maxChoices = maxChoices;  // 0=single choice, 1=multi choice
        this.inherited = inherited;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMaxChoices(int maxChoices) {
        this.maxChoices = maxChoices;
    }

    public int getMaxChoices() {
        return maxChoices;
    }

    public boolean isInherited() {
        return inherited;
    }

    public void setInherited( boolean inherited ) {
        this.inherited = inherited;
    }

    public boolean equals(Object o) {
    	return this == o 
    		|| (o instanceof CategoryTypeDomainObject && 
    		   ((CategoryTypeDomainObject)o).id == id);
    }

    public int hashCode() {
        return id;
    }

    public String toString() {
        return getName() ;
    }

    public int compareTo( Object o ) {
        return name.compareToIgnoreCase( ((CategoryTypeDomainObject)o).name) ;
    }

    public boolean hasImages() {
        CategoryDomainObject[] categories = Imcms.getServices().getCategoryMapper().getAllCategoriesOfType(this);
        boolean hasImages = false;
        for (int i = 0; i < categories.length; i++) {
            CategoryDomainObject category = categories[i];
            if( !"".equals(category.getImageUrl()) ) {
                hasImages = true;
                break;
            }
        }
        return hasImages;
    }
}