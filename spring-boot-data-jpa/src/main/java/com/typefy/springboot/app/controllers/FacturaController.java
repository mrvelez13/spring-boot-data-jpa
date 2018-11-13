package com.typefy.springboot.app.controllers;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.typefy.springboot.app.models.entity.Cliente;
import com.typefy.springboot.app.models.entity.DetalleFactura;
import com.typefy.springboot.app.models.entity.Factura;
import com.typefy.springboot.app.models.entity.Producto;
import com.typefy.springboot.app.models.service.IClienteService;

@Secured("ROLE_ADMIN")//Si se anota la clase, esta política de seguridad se aplicará a todos los métodos de la clase.
@Controller
@RequestMapping("/factura")
@SessionAttributes("factura")
public class FacturaController
{
	@Autowired
	private IClienteService clienteService;
	
	private final Logger log = LoggerFactory.getLogger(getClass());
	
	@GetMapping("/detalle/{id}")
	public String ver(@PathVariable(value="id") Long id,
			Model model,
			RedirectAttributes flash) 
	{
		Factura factura = clienteService.fetchFacturaByIdWithClienteWithDetalleFacturaWithProducto(id);
		
		if(factura == null)
		{
			flash.addFlashAttribute("error","La factura no existe en la base de datos");
			return "redirect:/clientes";
		}
		
		model.addAttribute("factura", factura);
		model.addAttribute("titulo", "Factura: ".concat(factura.getDescripcion()));
		return "factura/detalle";
	}
	
	@GetMapping("/form/{clienteId}")
	public String crear( @PathVariable(value="clienteId") Long clienteId, Map<String, Object> model, RedirectAttributes flash )
	{
		Cliente cliente = clienteService.findOne( clienteId );
		
		if(cliente == null)
		{
			flash.addFlashAttribute("error","El cliente no existe en la base de datos"); 
			return "redirect:/clientes";
		}
		
		Factura factura = new Factura();
		factura.setCliente(cliente);
		
		model.put("factura", factura);
		model.put("titulo", "Crear factura");
		
		return "factura/form";
	}
	
	@GetMapping(value= "/cargar-productos/{term}", produces= {"application/json"})
	public @ResponseBody List<Producto> cargarProductos(@PathVariable String term)
	{
		return clienteService.findByNombre(term);
	}
	
	@PostMapping("/form")
	public String guardar(@Valid Factura factura,
			BindingResult result,
			Model model,
			@RequestParam(name="item_id[]", required=false) Long[] itemId, 
			@RequestParam(name="cantidad[]", required=false) Integer[] cantidad, 
			RedirectAttributes flash,
			SessionStatus status)
	{
		if(result.hasErrors())
		{
			model.addAttribute("titulo", "Crear factura");
			return "factura/form";
		}
		
		if(itemId == null || itemId.length == 0)
		{
			model.addAttribute("titulo", "Crear factura");
			model.addAttribute("error","La factura debe contener al menos una línea");
			return "factura/form";
		}
		
		for(int i = 0; i < itemId.length; i++)
		{		
			Producto producto = clienteService.findProductoById(itemId[i]).get();
			
			DetalleFactura linea = new DetalleFactura();
			linea.setCantidad(cantidad[i]);
			linea.setProducto(producto);
			
			factura.addDetalleFactura(linea);
			
			log.info("id: " + itemId[i].toString() +  ", cantidad: " + cantidad[i].toString());
		}
		
		clienteService.saveFactura(factura);
		status.setComplete();
		flash.addFlashAttribute("success", "Factura creada exitosamente!");
		log.info("Practicamente la factura debería estar guardada.");
		return "redirect:/detalle/" + factura.getCliente().getId();
	}
	
	@GetMapping("eliminar/{id}")
	public String eliminar(@PathVariable(name="id") Long id, RedirectAttributes flash)
	{
		Factura factura = clienteService.findFacturaById(id).orElse(null);
		
		if(factura != null)
		{
			clienteService.deleteFactura(id);
			flash.addFlashAttribute("success","Factura eliminada con éxito!");
			return "redirect:/detalle/" + factura.getCliente().getId();
		}
		flash.addFlashAttribute("error","La factura no existe en la base de datos!");
		
		return "redirect:/clientes";
	}
}
