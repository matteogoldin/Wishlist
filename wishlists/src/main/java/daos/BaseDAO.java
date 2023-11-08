package daos;

import java.util.List;

public interface BaseDAO<T>{
	T findById(String id);
	List<T> getAll();
	void add(T object);
	void remove(String id);
}
