package com.typefy.springboot.app.models.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import com.typefy.springboot.app.models.entity.Cliente;

@Repository("clienteDaoJPA") //Esta notación de Spring permite reconocer la clase como un componente de persistencia. De Acceso a Datos. El hecho de que esta clase
//se pueda marcar como Repository permite de Spring pueda mapear de una mejor manera las excepciones generadas en el Acceso de datos. Habrá mayor detalle en los errores. 
//Aunque la especificación del repositorio "clienteDaoJPA" no es obligatoria, es necesario ponerle un nombre al repositorio en caso de que haya
//más de una clase implementadora de la interfaz IClienteDao. Si no se pone, y más de una clase usa IClienteDao, las peticiones que se hagan desde
//el controlador a los métodos de esta clase, quedarían ambiguos, porque JPA no sabría a cuál de todas las implementaciones invocar.
//para resolver esto, en el controlador, al momento de llamar al Dao, se usa la notacón @Qualifier y se le pasa el nombre del repositorio, para
//idicarle a JPA a cuál de todas las implementaciones debería ir a invocar.
public class ClienteDaoImpl /*implements IClienteDao*/
{
	//EntityManager es el encargado de administrar todos los comportamientos de las entidades. Las consultas en JPA se hace sobre las entidades y no sobre las BD directamente.
//	@PersistenceContext
//	private EntityManager em;
//	
//	@SuppressWarnings("unchecked")
//	@Override
//	public List<Cliente> findAll()
//	{
//		return em.createQuery("from Cliente").getResultList();
//	}
//	
//	@Override
//	public Cliente findOne(Long id)
//	{
//		return em.find(Cliente.class, id);
//	}
//
//	@Override
//	public void save(Cliente cliente)
//	{
//		if( cliente.getId() != null && cliente.getId() > 0 )
//		{
//			em.merge(cliente);
//		}
//		else
//		{
//			em.persist(cliente);
//		}	
//	}
//
//	@Override
//	public void delete(Long id)
//	{
//		em.remove(findOne(id));
//	}
}
