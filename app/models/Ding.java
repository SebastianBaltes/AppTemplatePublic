package models;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;

import play.data.format.Formats.NonEmpty;
import play.data.validation.Constraints;
import play.db.ebean.Model;

@SuppressWarnings("serial")
@Entity
public class Ding extends Model {

    private static final Finder<Long, Ding> finder = new Finder<Long, Ding>(Long.class, Ding.class);

    @Id
    private Long id;
    @Constraints.Required
    @NonEmpty
    private String name;
    private String description;
    private boolean special;
    @Constraints.Required
    private BigDecimal price;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isSpecial() {
        return special;
    }

    public void setSpecial(boolean special) {
        this.special = special;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
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
                + ", price=" + price + "]";
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

}
