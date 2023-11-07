package model;

import java.util.UUID;

import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class BaseEntity {
	@Id
	protected String uuid = UUID.randomUUID().toString();

	public String getUuid() {
	        return uuid;
	    }

	public void setUuid(String uuid) {
	        this.uuid = uuid;
	    }
}
