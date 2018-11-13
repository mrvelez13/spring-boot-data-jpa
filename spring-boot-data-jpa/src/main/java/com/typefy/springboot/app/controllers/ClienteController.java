package com.typefy.springboot.app.controllers;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.typefy.springboot.app.controllers.util.paginator.PageRender;
import com.typefy.springboot.app.models.entity.Cliente;
import com.typefy.springboot.app.models.service.IClienteService;
import com.typefy.springboot.app.models.service.IUploadFileService;

@Controller
@SessionAttributes("cliente")
public class ClienteController
{
	protected final Log logger = LogFactory.getLog(this.getClass());
	@Autowired
//	@Qualifier("clienteDaoJPA")// Es necesario sólo si más de una clase implementan la interfaz IClienteDao
	private IClienteService clienteService;
	
	@Autowired
	private IUploadFileService uploadFileService;
	
	@Autowired
	private MessageSource messageSource;
	
	//Ejemplo para indicar cómo se usa un método de controlador REST
	@GetMapping(value="/clientes-rest")
	@ResponseBody
	public List<Cliente> listarClientesRest()
	{
		return clienteService.findAll();
	}
	
	//{"/clientes", "/"} Indica que es el listado de clientes, pero tanbièn nuestra página de inicio.
	@RequestMapping(value= {"/clientes", "/"}, method=RequestMethod.GET)
	public String listarClientes(@RequestParam(name="page", defaultValue="0") int page, Model model, 
			Authentication authentication, 
			HttpServletRequest request,
			Locale locale)
	{	
		if(authentication != null)
		{
			logger.info("Usuario autenticado: ".concat(authentication.getName()).concat("."));
		}
		
		//El usuario autenticado también se puede obtener de forma estática. Sin necesidad de pasar el Authentication como argumento
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		if(auth != null)
		{
			logger.info("Usuario autenticado por clase estática: ".concat(auth.getName()).concat("."));
		}
		
		if(hasRole("ROLE_ADMIN"))
		{
			logger.info("Hola ".concat(auth.getName()).concat(", Tienes acceso"));
		}
		else
		{
			logger.info("Hola ".concat(auth.getName()).concat(", NO tienes acceso"));
		}
		
		//Otra forma de validar los roles de un usuario
		SecurityContextHolderAwareRequestWrapper securityContext = new SecurityContextHolderAwareRequestWrapper(request, "ROLE_");
		
		if(securityContext.isUserInRole("ADMIN"))
		{
			logger.info("Forma SecurityContextHolderAwareRequestWrapper. Hola ".concat(auth.getName()).concat(", Tienes acceso"));
		}
		else
		{
			logger.info("Forma SecurityContextHolderAwareRequestWrapper.. Hola ".concat(auth.getName()).concat(", NO tienes acceso"));
		}
		
		//Otra forma de validar los roles del usuario
		if(request.isUserInRole("ROLE_ADMIN"))
		{
			logger.info("Forma HttpServletRequest. Hola ".concat(auth.getName()).concat(", Tienes acceso"));
		}
		else
		{
			logger.info("Forma HttpServletRequest.. Hola ".concat(auth.getName()).concat(", NO tienes acceso"));
		}
		
		Pageable pageRequest = PageRequest.of(page, 5);
		Page<Cliente> clientes = clienteService.findAll(pageRequest);
		
		PageRender<Cliente> pageRender = new PageRender<>("/clientes", clientes);
		model.addAttribute("titulo",messageSource.getMessage("text.cliente.listar.titulo", null, locale));
		model.addAttribute("clientes", clientes);
		model.addAttribute("page", pageRender);
//		model.addAttribute("clientes", clienteService.findAll());
		return "clientes";
	}
	
	@Secured("ROLE_USER")
	@GetMapping(value="/detalle/{id}")
	public String clientDetail(@PathVariable(value="id") Long id, Map<String, Object> model, RedirectAttributes flash)
	{
		Cliente cliente = clienteService.fetchByIdWithFacturas(id);
		if(cliente == null) 
		{
			flash.addFlashAttribute("error", "El cliente no existe en la base de datos");
			return "redirect:/clientes";
		}
		
		model.put("cliente", cliente);
		model.put("titulo", "Detalle del cliente " + cliente.getId());
		
		return "detalle";
	}
	
	@Secured("ROLE_ADMIN")
	@RequestMapping(value="/form")
	public String crear(Map<String, Object> model)
	{
		Cliente cliente = new Cliente();
		
		model.put("cliente", cliente);
		model.put("titulo", "Creaci\u00f3n de clientes");
		return "form";
	}
	
	@Secured("ROLE_ADMIN")
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
	
	@Secured("ROLE_ADMIN")
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
			if(cliente.getId() != null 
					&& cliente.getId() > 0
					&& cliente.getImagen() != null
					&& cliente.getImagen().length() > 0 )
			{
				uploadFileService.delete( cliente.getImagen() );
			}
//			Path resourcesDirectory = Paths.get("src//main//resources//static/uploads");
//			String rootParh = resourcesDirectory.toFile().getAbsolutePath();
			
			String uniqueFileName = null;
			try {
				uniqueFileName = uploadFileService.copy( foto );
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			flash.addFlashAttribute("info","La foto " + uniqueFileName + " se ha cargado correctamente.");
			cliente.setImagen( uniqueFileName );
			
		}
		
		String mensajeFlahs = cliente.getId() != null ? "cliente actualizado satisfactoriamente!" : "cliente creado satisfactoriamente!";
		
		clienteService.save(cliente);
		status.setComplete();		
		
		flash.addFlashAttribute("success", mensajeFlahs);
		return "redirect:/clientes";
	}
	
	@Secured("ROLE_ADMIN")
	@RequestMapping(value="/eliminar/{id}")
	public String eliminar(@PathVariable(value="id") Long id, RedirectAttributes flash)
	{
		if( id>0 )
		{
			Cliente cliente = clienteService.findOne(id);
			clienteService.delete(id);
			flash.addFlashAttribute("success","cliente eliminado satisfactoriamente");
			
			if(uploadFileService.delete( cliente.getImagen() ))
			{
				flash.addFlashAttribute("info","Foto " + cliente.getImagen() + " eliminada con exito!");
			}
			
		}
		
		return "redirect:/clientes";
	}
	
	@Secured("ROLE_USER")
	@GetMapping(value="/uploads/{filename:.+}")
	public ResponseEntity<Resource> uploadPicture(@PathVariable String filename)
	{
		Resource resource = null;
		
		try {
			resource = uploadFileService.load(filename);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
				.body(resource);
	}
	
	//Método para recorrer los roles que tiene un usuario. Con esto se compara si el usuario posee un rol específico que se recibe como parámetro.
	private boolean hasRole(String role)
	{
		SecurityContext context = SecurityContextHolder.getContext();
		
		if( context == null )
		{
			return false;
		}
		
		Authentication auth = context.getAuthentication();
		
		if( auth == null )
		{
			return false;
		}
		
		Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
		
		for(GrantedAuthority authority : authorities)
		{
			if(role.equals(authority.getAuthority()))
			{
				return true;
			}
		}
		
		//Otra forma más corta de hacer la validación del anterior For sería:
		return authorities.contains(new SimpleGrantedAuthority(role));
		
		//return false;
	}
	
}
