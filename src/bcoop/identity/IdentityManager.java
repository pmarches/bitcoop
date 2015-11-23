package bcoop.identity;

import javax.persistence.Entity;
import javax.persistence.Transient;

import bcoop.exception.NoLocalIdentityDefinedException;
import bcoop.util.ObjectStore;

@Entity
public class IdentityManager {
	@Transient
	LocalIdentity localIdentity;
	private ObjectStore oStore;
	
	public IdentityManager(){
	}

	public IdentityManager(ObjectStore objectStore) {
		this.oStore = objectStore;
		this.localIdentity = objectStore.getLocalIdentity();
		if(this.localIdentity == null){
			throw new RuntimeException("No local identity defined!");
		}
	}

	public void setLocalIdentity(LocalIdentity localIdentity){
		this.localIdentity = localIdentity;
	}
	
	public LocalIdentity getLocalIdentity() throws NoLocalIdentityDefinedException{
		if(this.localIdentity == null){
			throw new NoLocalIdentityDefinedException();
		}
		return localIdentity;
	}
	
	public void addIdentity(Identity newIdentity){
		this.oStore.save(newIdentity);
	}
	
	public Identity getIdentity(Long uid){
		return oStore.getIdentity(uid);
	}
}
