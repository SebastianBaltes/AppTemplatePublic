package models;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Version;

import com.avaje.ebean.Page;

import play.data.format.Formats.NonEmpty;
import play.data.validation.Constraints;
import play.db.ebean.Model;

@SuppressWarnings("serial")
@Entity
public class Ding extends Model implements HasLabel {

    private static final Finder<Long, Ding> finder = new Finder<Long, Ding>(Long.class, Ding.class);

    @Id
    public Long id;
    @Constraints.Required
    @NonEmpty
    public String name;
    public String description;
    public boolean special;
    @Constraints.Required
    public BigDecimal price;
    @Version
    public Timestamp lastUpdate;

    public void saveOrUpdate() {
        if (id == null) {
            save();
        } else {
            update();
        }
    }
    
    public static Page<Ding> page(final int page, final int pageSize, final String sortBy, final String order, final String filter) {
        return finder.where().ilike("name", "%" + filter + "%").orderBy(sortBy + " " + order).findPagingList(pageSize).getPage(page);
    }    
    
    public static List<Ding> findAll() {
        return finder.all();
    }

    public static Ding findById(final Long id) {
        return finder.byId(id);
    }

    public static Ding findByName(final String name) {
        return finder.where().eq("name", name).findUnique();
    }

    public static void deleteById(final Long id) {
        finder.ref(id).delete();
    }

    @Override
    public String toString() {
        return "Ding [id=" + id + ", name=" + name + ", description=" + description + ", special=" + special
                + ", price=" + price + ", lastUpdate=" + lastUpdate + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        Ding other = (Ding) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    @Override
    public String getLabel() {
        return name;
    }

}
