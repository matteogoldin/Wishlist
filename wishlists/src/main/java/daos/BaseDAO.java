package daos;

import java.util.List;

public interface BaseDAO<T,Q>{
	T findById(Q id);
	List<T> getAll();
	void add(T object);
	void remove(T object);
}
