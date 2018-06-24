package com.typefy.springboot.app.controllers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.typefy.springboot.app.controllers.util.paginator.PageRender;
import com.typefy.springboot.app.models.entity.Cliente;
import com.typefy.springboot.app.models.service.IClienteService;

@Controller
@SessionAttributes("cliente")
public class ClienteController
{
	@Autowired
//	@Qualifier("clienteDaoJPA")// Es necesario sólo si más de una clase implementan la interfaz IClienteDao
	private IClienteService clienteService;
	@RequestMapping(value="/clientes", method=RequestMethod.GET)
	public String listarClientes(@RequestParam(name="page", defaultValue="0") int page, Model model)
	{
		Pageable pageRequest = PageRequest.of(page, 5);
		Page<Cliente> clientes = clienteService.findAll(pageRequest);
		
		PageRender<Cliente> pageRender = new PageRender<>("/clientes", clientes);
		model.addAttribute("titulo","Listado de Clientes");
		model.addAttribute("clientes", clientes);
		model.addAttribute("page", pageRender);
//		model.addAttribute("clientes", clienteService.findAll());
		return "clientes";
	}
	
	@GetMapping(value="/detalle/{id}")
	public String clientDetail(@PathVariable(value="id") Long id, Map<String, Object> model, RedirectAttributes flash)
	{
		Cliente cliente = clienteService.findOne(id);
		if(cliente == null) 
		{
			flash.addFlashAttribute("error", "El cliente no existe en la base de datos");
			return "redirect:/clientes";
		}
		
		model.put("cliente", cliente);
		model.put("titulo", "Detalle del cliente " + cliente.getId());
		
		return "detalle";
	}
	
	@RequestMapping(value="/form")
	public String crear(Map<String, Object> model)
	{
		Cliente cliente = new Cliente();
		
		model.put("cliente", cliente);
		model.put("titulo", "Creaci\u00f3n de clientes");
		return "form";
	}
	
	@RequestMapping(value="/form/{id}")
	public String editarCliente(@PathVariable(value="id") Long id,  Map<String, Object> model, RedirectAttributes flash)
	{
		Cliente cliente = null;
		
		if( id>0 )
		{
			cliente = clienteService.findOne(id);
			if( cliente == null )
			{
				flash.addFlashAttribute("error","El cliente no existe en la base de datos!!");
				return "redirect:/clientes";
			}
		}
		else
		{
			flash.addFlashAttribute("error","El id del cliente no puede estar vacío o null!!");
			return "redirect:/clientes";
		}
		
		model.put("cliente", cliente);
		model.put("titulo", "Edidor de clientes");
		
		return "form";
	}
	
	@RequestMapping(value="/form", method=RequestMethod.POST)
	public String guardarCliente(@Valid Cliente cliente, BindingResult result, Model model, @RequestParam("foto") MultipartFile foto, RedirectAttributes flash, SessionStatus status)
	{
		if( result.hasErrors() )
		{
			model.addAttribute("titulo","Creaci\u00f3n de clientes");
			return "form";
		}
		
		if(!foto.isEmpty())
		{
			Path resourcesDirectory = Paths.get("src//main//resources//static/uploads");
			String rootParh = resourcesDirectory.toFile().getAbsolutePath();
			
			try
			{
				byte[] bytes = foto.getBytes();
				Path fullPath = Paths.get(rootParh + "//" + foto.getOriginalFilename());
				Files.write(fullPath, bytes);
				flash.addFlashAttribute("info","La foto " + foto.getOriginalFilename() + " se ha cargado correctamente.");
				cliente.setImagen( foto.getOriginalFilename() );
			}
			catch ( IOException e )
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		String mensajeFlahs = cliente.getId() != null ? "cliente actualizado satisfactoriamente!" : "cliente creado satisfactoriamente!";
		
		clienteService.save(cliente);
		status.setComplete();		
		
		flash.addFlashAttribute("success", mensajeFlahs);
		return "redirect:/clientes";
	}
	
	@RequestMapping(value="/eliminar/{id}")
	public String eliminar(@PathVariable(value="id") Long id, RedirectAttributes flash)
	{
		if( id>0 )
		{
			clienteService.delete(id);
			flash.addFlashAttribute("success","cliente eliminado satisfactoriamente");
			
		}
		return "redirect:/clientes";
	}
}
