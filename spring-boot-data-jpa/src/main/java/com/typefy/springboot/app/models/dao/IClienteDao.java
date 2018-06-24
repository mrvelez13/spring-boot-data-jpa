package com.typefy.springboot.app.models.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.typefy.springboot.app.models.entity.Cliente;

//Una forma rápida de implementer un crud es utilizando la Interfaz de Spring Data llamada CrudRepository, la cual internamente ya trae todos los metodos para implementar un crud completo.
//De esta manera la clase que implementa la interfaz IClienteDao queda obsoleta. Incluso no son necesarios los métodos creados en la interfaz IClienteDao.
//También es posible extender de la Interfaz PaginAndSortingRepository, la cual a su vez extiende de CrudRepository, de manera que podríamos conservar los mísmos métodos implementados. 
//La ventaja de PaginAndSortingRepository es que contiene métodos adicionales que nos permiten ordenar los resultados de los querys y además paginarlos.
public interface IClienteDao extends PagingAndSortingRepository<Cliente, Long>/*CrudRepository<Cliente, Long>*/
{
	//Comentarmos los métodos creados manualmente para usar los genéricos que están dentro de la interfaz CrudRepository
	/*public List<Cliente> findAll();
	
	public void save(Cliente cliente);
	
	public Cliente findOne(Long id);
	
	public void delete(Long id);*/
}
