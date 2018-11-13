package com.typefy.springboot.app.controllers;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LoginController
{
	@GetMapping("/login")
	public String login(@RequestParam(value="error", required=false) String error,
			@RequestParam(value="logout", required=false) String logout, 
			Model model, Principal principal, RedirectAttributes flash)
	{
		//Si principal es distinto de null, quiere decir que ya se ha logueado anteriormente, por tanto redirigimos a la página de inicio.
//		System.err.println("Principal: " + principal.getName() );
		if(principal != null)
		{
			flash.addFlashAttribute("info", "Ya ha iniciado sesión anteriormente.");
			return "redirect:/";
		}
		
		if(error != null)
		{
			model.addAttribute("error", "Usuario o contraseña incorrecta");
		}
		
		if(logout != null)
		{
			model.addAttribute("success", "Ha cerrado sesión correctamente");
		}
		
		return "login";
	}
}
