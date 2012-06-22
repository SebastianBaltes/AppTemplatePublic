package models;

import java.sql.Timestamp;

import javax.persistence.Id;
import javax.persistence.Version;

import play.db.ebean.Model;

@SuppressWarnings("serial")
@javax.persistence.MappedSuperclass
public abstract class CrudModel<T extends CrudModel<T>> extends Model {

    @Id
    public Long id;
    @Version
    public Timestamp lastUpdate;

    public abstract String label();

    protected abstract CrudFinder<T> getCrudFinder();
    
    public void saveOrUpdate() {
        if (id == null) {
            save();
        } else {
            update();
        }
    }

    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Timestamp getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(Timestamp lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	@Override
    public String toString() {
        return this.getClass().getSimpleName()+"[id=" + id + ", label=" + label()+ "]";
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
        T other = (T) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

}
