package com.typefy.springboot.app.models.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.typefy.springboot.app.models.dao.IClienteDao;
import com.typefy.springboot.app.models.dao.IFacturaDao;
import com.typefy.springboot.app.models.dao.IProductoDao;
import com.typefy.springboot.app.models.entity.Cliente;
import com.typefy.springboot.app.models.entity.Factura;
import com.typefy.springboot.app.models.entity.Producto;

@Service
public class ClienteServiceImpl implements IClienteService
{
	@Autowired
	private IClienteDao clienteDao;
	
	@Autowired
	private IProductoDao productoDao;
	
	@Autowired
	private IFacturaDao facturaDao;

	@Override
	@Transactional(readOnly=true)
	public List<Cliente> findAll()
	{
		return (List<Cliente>) clienteDao.findAll();
	}

	@Override
	@Transactional
	public void save(Cliente cliente)
	{
		clienteDao.save(cliente);		
	}

	@Override
	@Transactional(readOnly=true)
	public Cliente findOne(Long id)
	{
		return clienteDao.findById(id).orElse(null);
	}
	
	@Override
	@Transactional(readOnly=true)
	public Cliente fetchByIdWithFacturas(Long id) {
		return clienteDao.fetchByIdWithFacturas(id);
	}

	@Override
	@Transactional
	public void delete(Long id)
	{
		clienteDao.deleteById(id);
	}

	@Override
	@Transactional(readOnly=true)
	public Page<Cliente> findAll(Pageable pageable) {
		return clienteDao.findAll(pageable);
	}

	@Override
	public List<Producto> findByNombre(String term) {
		return productoDao.findByNombre(term);
	}

	@Override
	@Transactional
	public void saveFactura(Factura factura) {
		facturaDao.save(factura);
	}

	@Override
	@Transactional(readOnly=true)
	public Optional<Producto> findProductoById(Long id) {
		return productoDao.findById(id);
	}

	@Override
	@Transactional(readOnly=true)
	public Optional<Factura> findFacturaById(Long id) {
		return facturaDao.findById(id);
	}

	@Override
	@Transactional
	public void deleteFactura(Long id) {
		facturaDao.deleteById(id);
	}

	@Override
	@Transactional(readOnly=true)
	public Factura fetchFacturaByIdWithClienteWithDetalleFacturaWithProducto(Long id) {
		return facturaDao.fetchFacturaByIdWithClienteWithDetalleFacturaWithProducto(id);
	}
}
