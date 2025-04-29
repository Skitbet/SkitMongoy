package gay.skitbet.mongoy.repository;

import java.util.Date;

/**
 * BaseEntity class that represents a mongo model, contains a default createdAt and updatedAt variable.
 * Not required to use as a Entity model.
 */
public class BaseEntity {

    private Date createdAt;
    private Date updatedAt;

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}
